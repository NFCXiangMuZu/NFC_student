package com.example.compaq.nfc_student;

/**
 * 点名中继功能实现
 */

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import com.example.nfc_student.R;

public class MiddleAttendence extends Activity implements CreateNdefMessageCallback,OnNdefPushCompleteCallback{

	private static final int MESSAGE_SENT = 0;
	NfcAdapter nfcadapter;
	PendingIntent pendingintent;
	BluetoothDevice bluetoothDevice;
	BluetoothSocket bluetoothSocket;
	BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

	//显示文件传输进度
	ProgressDialog file_send_dialog;

	//layout元素
	Button middle_attendence_NB_leftbutton;
	Button  middle_attendence_NB_rightbutton;
	ListView middle_attendence_listview;

	//存储已经拍卡的学生信息，需要发送到教师端处
	public List<String> list=new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置无标题
		setContentView(R.layout.middle_attendence);

		//初始化layout
		init_layout();

		nfcadapter=NfcAdapter.getDefaultAdapter(this);
		pendingintent=PendingIntent.getActivity(this, 0,
				new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		nfcadapter.setNdefPushMessageCallback(this, this);
		nfcadapter.setOnNdefPushCompleteCallback(this, this);

		//注册接收文件发送信息的广播
		IntentFilter intentfilter=new IntentFilter();
		intentfilter.addAction(BluetoothTools.ACTION_FILE_SEND_SUCCESS);
		intentfilter.addAction(BluetoothTools.ACTION_FILE_SEND_PERCENT);
		registerReceiver(receiver, intentfilter);

		//启动接收开始文件发送广播的服务
		MiddleAttendence.this.startService(new Intent(MiddleAttendence.this,SendFileService.class));

	}


	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	/**
	 * 初始化layout
	 */
	private void init_layout(){

		middle_attendence_NB_leftbutton = (Button)findViewById(R.id.middle_attendence_leftbutton);
		middle_attendence_NB_rightbutton = (Button)findViewById(R.id.middle_attendence_rightbutton);
		middle_attendence_listview = (ListView)findViewById(R.id.middle_attendence_listview);

		middle_attendence_NB_leftbutton.setOnClickListener(new listener());
		middle_attendence_NB_rightbutton.setOnClickListener(new listener());

	}

	/**
	 * 按钮监听器
	 */
	class listener implements View.OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (arg0.getId()) {

				case R.id.middle_attendence_leftbutton://点击返回按钮
					Intent intent_back=new Intent();
					intent_back.setClass(MiddleAttendence.this,MainActivity.class);
					MiddleAttendence.this.startActivity(intent_back);
					finish();
					break;

				default:
					break;
			}
		}

	}

	//接收NFC信息传输结果的handler
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MESSAGE_SENT:
					//弹出框定义
					AlertDialog.Builder alertdialog=new AlertDialog.Builder(MiddleAttendence.this);
					if(StaticValue.status==1){
						alertdialog.setTitle("                 签到完成");
					}
					else{
						alertdialog.setTitle("                 签到未完成");
					}
					alertdialog.setPositiveButton("回到主页",new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							Intent intent=new Intent();
							intent.setClass(MiddleAttendence.this,MainActivity.class);
							MiddleAttendence.this.startActivity(intent);
							finish();
						}

					});
					alertdialog.setNegativeButton("继续签到", null);
					alertdialog.show();
					break;
			}
		}
	};

	@Override
	public void onNdefPushComplete(NfcEvent arg0) {
		// TODO Auto-generated method stub
		StaticValue.status=1;
		System.out.println("----------status="+StaticValue.status);
		mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();

	}

	/**
	 * 创建通过拍卡传输到教师端的NdefMessage
	 * @param arg0
	 * @return
     */
	@Override
	public NdefMessage createNdefMessage(NfcEvent arg0) {
		// TODO Auto-generated method stub
		NdefMessage msg = null;
		try {
			msg = getNoteAsNdef();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return msg;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.out.println("----onResume----");
		if (this.nfcadapter == null)
			return;
		if (!this.nfcadapter.isEnabled()) {
			System.out.println("请在系统设置中先启用NFC功能");
		}
		this.nfcadapter.enableForegroundDispatch(this, pendingintent, null, null);

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		System.out.println("----onPause----");
		if(NfcAdapter.getDefaultAdapter(this)!=null){
			NfcAdapter.getDefaultAdapter(this).disableForegroundDispatch(this);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		System.out.println("----onNewIntent----");
		setIntent(intent);
		try {
			resolveIntent(intent);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 从字符串新建一个NdefRecord
	 * @param text
	 * @return
	 * @throws UnsupportedEncodingException
     */
	private NdefRecord createRecord(String text) throws UnsupportedEncodingException {

		System.out.println("----createRecord----");
		String nameVcard = text;
		byte[] uriField = nameVcard.getBytes();
		byte[] payload = new byte[uriField.length + 1];              //add 1 for the URI Prefix
		System.arraycopy(uriField, 0, payload, 1, uriField.length);  //appends URI to payload
		NdefRecord nfcRecord = new NdefRecord(
				NdefRecord.TNF_MIME_MEDIA, "text/vcard".getBytes(), new byte[0], payload);
		return nfcRecord;
	}

	/**
	 * 获取一个NdefMessage
	 * @return
	 * @throws UnsupportedEncodingException
     */
	private NdefMessage getNoteAsNdef() throws UnsupportedEncodingException {
		System.out.println("----getNoteAsNdef----");
		NdefRecord[] list_record=new NdefRecord[list.size()];
		if (StaticValue.setname.equals("")) {
			return null;
		} else {
			for(int i=0;i<list.size();i++){
				list_record[i]=createRecord(list.get(i).toString());
			}
			return new NdefMessage(list_record);
		}

	}

	/**
	 * 处理通过拍卡接收到的NdefMessage
	 * @param intent
	 * @throws UnsupportedEncodingException
	 * @throws FormatException
     */
	protected void resolveIntent(Intent intent) throws UnsupportedEncodingException, FormatException {
		// 得到是否TAG触发
		System.out.println("----resolveIntent----");
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())
				|| NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())
				|| NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()))
		{
			Parcelable[] rawMsgs =
					intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			// only one message sent during the beam
			NdefMessage msg = (NdefMessage)rawMsgs[0];
			String result_macaddress = new String(msg.getRecords()[0].getPayload(), "GBK").substring(1);
			String result_strname=new String(msg.getRecords()[1].getPayload(),"UTF-8");
			String result_strxuehao=new String(msg.getRecords()[2].getPayload(),"UTF-8").substring(1,11);
			String result_strreflect_infor=new String(msg.getRecords()[3].getPayload(),"UTF-8");
			//将获取的学生信息插入到list数组中
			list.add(result_macaddress);
			list.add(result_strname);
			list.add(result_strxuehao);
			list.add(result_strreflect_infor);

			//更新ListView内容
			ArrayList<HashMap<String,Object>> listitem = new ArrayList<HashMap<String,Object>>();
			for(int i=0;i<list.size();i+=4){

				HashMap<String,Object> map = new HashMap<String,Object>();
				map.put("middle_attendence_listview_xuehao_item",list.get(i+1));
				map.put("middle_attendence_listview_name_item",list.get(i+2));
				listitem.add(map);

			}
			//生成适配器
			SimpleAdapter listitemadapter = new SimpleAdapter(this,
					listitem,
					R.layout.middle_attendence_list_item,
					new String[] {"middle_attendence_listview_xuehao_item","middle_attendence_listview_name_item"},
					new int[] {R.id.middle_attendence_listview_xuehao_item,R.id.middle_attendence_listview_name_item}
					);

			middle_attendence_listview.setAdapter(listitemadapter);

			//连接蓝牙
			StaticValue.remote_macaddress = result_macaddress;
			bluetoothDevice = bluetoothAdapter.getRemoteDevice(result_macaddress);
			if (bluetoothDevice != null) {
				System.out.println("==获取成功==");
			}
			try {
				ClsUtils.cancelPairingUserInput(bluetoothDevice.getClass(), bluetoothDevice);
				ClsUtils.setPin(bluetoothDevice.getClass(), bluetoothDevice, "0000");
				ClsUtils.createBond(bluetoothDevice.getClass(), bluetoothDevice);
				System.out.println("配对成功！！");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//判断是否有文件需要发送
			if (StaticValue.filename_for_middle != null) {
				Thread thead = new sendThread();
				thead.start();
				//进度条对话框显示
				file_send_dialog = new ProgressDialog(MiddleAttendence.this);
				file_send_dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				file_send_dialog.setTitle("文件发送中");
				file_send_dialog.setCancelable(true);
				file_send_dialog.show();
			} else {
				System.out.println("无文件可发！！");
			}

		}
	}

	/**
	 * 发送 开始文件发送 广播的线程
	 */
	private class sendThread extends Thread {

		public sendThread(){}

		public void run() {

			TransmitBean transmit = new TransmitBean();
			String path=StaticValue.filename_for_middle;
			String filename=path.substring(path.lastIndexOf("/")+1,path.length());
			transmit.setFilename(filename);
			transmit.setFilepath(path);
			Intent sendDataIntent = new Intent(BluetoothTools.ACTION_DATA_TO_SERVICE);
			sendDataIntent.putExtra(BluetoothTools.DATA, transmit);
			sendBroadcast(sendDataIntent);

		}
	}

	/**
	 * 接收文件发送过程信息的广播接收器
	 */
	BroadcastReceiver receiver=new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			String action = arg1.getAction();
			if (BluetoothTools.ACTION_FILE_SEND_SUCCESS.equals(action)) {//文件传输成功
				file_send_dialog.cancel();
				Toast.makeText(MiddleAttendence.this, "文件发送成功！", Toast.LENGTH_LONG).show();
			}else if(BluetoothTools.ACTION_FILE_SEND_PERCENT.equals(action)){//文件传输过程
				file_send_dialog.setMax(StaticValue.file_send_length);
				file_send_dialog.setProgress(StaticValue.file_send_percent);
			}

		}
	};
}
