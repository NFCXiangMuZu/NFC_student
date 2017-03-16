package com.example.compaq.nfc_student;

/**
 * 拍卡签到功能学生端实现
 */

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.Toast;
import com.example.nfc_student.R;

public class NormalAttendence extends Activity implements CreateNdefMessageCallback,OnNdefPushCompleteCallback{

	protected static final int MESSAGE_SENT = 0;
	NfcAdapter nfcadapter;
	PendingIntent pendingintent;
	BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	//界面元素定义
	Button normal_attendence_NB_backbutton;
	Button normal_attendence_NB_folderbutton;
	Button normal_attendence_centerbutton;
	PopupMenu normal_attendence_NB_folder_menu;
	//检查身份信息窗口
	PopupWindow normal_attendence_window;
	Button normal_attendence_window_closebutton;
	EditText normal_attendence_window_xuehao_edit;
	EditText normal_attendence_window_name_edit;
	EditText normal_attendence_window_reflect_infor_edit;
	Button normal_attendence_window_confirmbutton;

	//文件传输进度条实现
	ProgressDialog read_file_show_dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置无标题
		setContentView(R.layout.normalattendence);

		//初始化layout元素
		init_layout();

		//新建文件夹暂存从教师端处接收的文件
		Timestamp now = new Timestamp(System.currentTimeMillis());//获取系统当前时间
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");//定义格式，不显示毫秒
		String str = df.format(now);
		System.out.println(StaticValue.SDPATH+"/NFC—课堂点名/已接收文件/"+str+"/");
        StaticValue.save_file_path = StaticValue.SDPATH+"/NFC—课堂点名/已接收文件/"+str+"/";
		if(FileHelper.mkDir(StaticValue.SDPATH+"/NFC—课堂点名/已接收文件/"+str+"/"))//创建新文件夹
		{
			System.out.println("文件创建成功");
		}else {
			System.out.println("文件创建失败");

		}

		nfcadapter=NfcAdapter.getDefaultAdapter(this);

		pendingintent=PendingIntent.getActivity(this, 0,
				new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		nfcadapter.setNdefPushMessageCallback(this, this);
		nfcadapter.setOnNdefPushCompleteCallback(this, this);

		//接收 开始接受文件 广播的服务
		NormalAttendence.this.startService(new Intent(NormalAttendence.this,ReadFileService.class));

		//注册接收文件接收过程信息的广播接收器
		IntentFilter intentfilter=new IntentFilter();
		intentfilter.addAction(BluetoothTools.ACTION_FILE_RECEIVE_SUCCESS);
		intentfilter.addAction(BluetoothTools.ACTION_FILE_RECIVE_PERCENT);
		registerReceiver(readReceiver, intentfilter);

	}

	/**
	 * 初始化layout
	 */
	private void init_layout(){

		normal_attendence_NB_backbutton = (Button)findViewById(R.id.normal_attendence_NB_backbutton);
		normal_attendence_NB_folderbutton = (Button)findViewById(R.id.normal_attendence_NB_folderbutton);
		normal_attendence_centerbutton = (Button)findViewById(R.id.normal_attendence_centerbutton);

        normal_attendence_NB_backbutton.setOnClickListener(new listener());
		normal_attendence_NB_folderbutton.setOnClickListener(new listener());
		normal_attendence_centerbutton.setText("点击查看");
		normal_attendence_centerbutton.setOnClickListener(new listener());
	}

	/**
	 * 显示已绑定信息的弹出窗口
	 */
	private void show_normal_attendence_window(){

		View contentView = LayoutInflater.from(NormalAttendence.this).inflate(R.layout.normal_attendence_window, null);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		normal_attendence_window = new PopupWindow(contentView,dm.widthPixels*2/3,dm.heightPixels*2/5);
		normal_attendence_window.setFocusable(true);

		//初始化layout
		normal_attendence_window_closebutton = (Button)contentView.findViewById(R.id.normal_attendence_window_close_button);
		normal_attendence_window_xuehao_edit = (EditText)contentView.findViewById(R.id.normal_attendence_window_xuehao_edit);
		normal_attendence_window_name_edit = (EditText)contentView.findViewById(R.id.normal_attendence_window_name_edit);
		normal_attendence_window_reflect_infor_edit = (EditText)contentView.findViewById(R.id.normal_attendence_window_reflect_infor_edit);
		normal_attendence_window_confirmbutton = (Button)contentView.findViewById(R.id.normal_attendence_window_confirm_button);

		normal_attendence_window_xuehao_edit.setText(StaticValue.setnumber);
		normal_attendence_window_name_edit.setText(StaticValue.setname);
		normal_attendence_window_xuehao_edit.setEnabled(false);
		normal_attendence_window_name_edit.setEnabled(false);
		normal_attendence_window_reflect_infor_edit.setText(StaticValue.reflect_information);

		normal_attendence_window_closebutton.setOnClickListener(new listener());
		normal_attendence_window_confirmbutton.setOnClickListener(new listener());

		//显示PopupWindow
		View rootview = LayoutInflater.from(NormalAttendence.this).inflate(R.layout.activity_main, null);
		normal_attendence_window.showAtLocation(rootview, Gravity.CENTER, 0, 0);

	}

	/**
	 * 按钮监听器
	 */
	class listener implements View.OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch(arg0.getId()){

				case R.id.normal_attendence_NB_backbutton://点击左上角返回主界面按钮
					Intent intent_back=new Intent();
					intent_back.setClass(NormalAttendence.this,MainActivity.class);
					NormalAttendence.this.startActivity(intent_back);
					finish();
					break;

				case R.id.normal_attendence_NB_folderbutton://点击右上角折叠按钮
					normal_attendence_NB_folder_menu = new PopupMenu(NormalAttendence.this,normal_attendence_NB_folderbutton);
					//加载menu资源
					getMenuInflater().inflate(R.menu.normal_attendence_folder_menu,normal_attendence_NB_folder_menu.getMenu());
					//绑定点击事件
					normal_attendence_NB_folder_menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

						public boolean onMenuItemClick(MenuItem item) {

							switch (item.getItemId()) {
							}
							return false;

						}
					});
					normal_attendence_NB_folder_menu.show();
					break;

				case R.id.normal_attendence_centerbutton://点击中央按钮
					show_normal_attendence_window();
					break;

				//显示已绑定信息的弹出窗口中的按钮
				case R.id.normal_attendence_window_close_button://弹出窗口中的关闭按钮
					normal_attendence_window.dismiss();
					break;

				case R.id.normal_attendence_window_confirm_button://弹出窗口中的确定按钮
				    StaticValue.reflect_information = normal_attendence_window_reflect_infor_edit.getText().toString().trim();
					normal_attendence_window.dismiss();
					break;
				default:
					break;
			}
		}

	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MESSAGE_SENT:
					//身份信息已经通过拍卡传达至教师端
					if(StaticValue.status==1){
						//实现文件接受进度dialog
						read_file_show_dialog = new ProgressDialog(NormalAttendence.this);
						read_file_show_dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
						read_file_show_dialog.setCancelable(true);
						read_file_show_dialog.setTitle("文件接收中");
						read_file_show_dialog.show();
					}
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
		System.out.println("=====rusume=====");
		if (this.nfcadapter == null)
		{
			System.out.println("----onResume----");
			return;
		}
		if (!this.nfcadapter.isEnabled()) {
			System.out.println("请在系统设置中先启用NFC功能");
		}

		this.nfcadapter.enableForegroundDispatch(this, pendingintent, null, null);

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		NfcAdapter.getDefaultAdapter(this).disableForegroundDispatch(this);
	}

	/**
	 * 从字符串新建一个NdefRecord
	 * @param text
	 * @return
	 * @throws UnsupportedEncodingException
     */
	private NdefRecord createRecord(String text) throws UnsupportedEncodingException {

		System.out.println("----createRecord----");;
		String nameVcard = text;
		byte[] uriField = nameVcard.getBytes();
		byte[] payload = new byte[uriField.length + 1];              //add 1 for the URI Prefix
		System.arraycopy(uriField, 0, payload, 1, uriField.length);  //appends URI to payload
		NdefRecord nfcRecord = new NdefRecord(
				NdefRecord.TNF_MIME_MEDIA, "text/vcard".getBytes(), new byte[0], payload);
		return nfcRecord;
	}

	/**
	 * 获取一个NdefMessage,用于拍卡传送至教师端
	 * @return
	 * @throws UnsupportedEncodingException
     */
	private NdefMessage getNoteAsNdef() throws UnsupportedEncodingException {
		System.out.println("----getNoteAsNdef----");
		String mac_address = bluetoothAdapter.getAddress();
		String vcard = StaticValue.setname;
		String num=StaticValue.setnumber;
		String reflect_infor=StaticValue.reflect_information;
		if (vcard.equals("")) {
			return null;
		} else {
			NdefRecord mac_add = createRecord(mac_address);
			NdefRecord textRecord = createRecord(vcard);
			NdefRecord numRecord = createRecord(num);
			NdefRecord reflect_inforRecord=createRecord(reflect_infor);
			//System.out.println("要写入的text是："+getText(textRecord.getPayload()));
			return new NdefMessage(new NdefRecord[] {mac_add,textRecord,numRecord,reflect_inforRecord});
		}

	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(readReceiver);
		super.onDestroy();
	}

	/**
	 * 接收文件传输过程信息的广播接收器
	 */
	BroadcastReceiver readReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("传输成功广播收到！");
			String action = intent.getAction();
			if (BluetoothTools.ACTION_FILE_RECEIVE_SUCCESS.equals(action)) {//文件接收成功
				Toast.makeText(NormalAttendence.this,"文件接收成功！",Toast.LENGTH_LONG).show();
				read_file_show_dialog.cancel();
				String receive_file_path = StaticValue.save_file_path+"传输中间文件.zip";
				StaticValue.filename_for_middle = receive_file_path;
				String release_path = StaticValue.save_file_path;
				//解压文件
				try {
					ZIPControl.readByApacheZipFile(receive_file_path,release_path);
					//FileHelper.deleteFile(receive_file_path);
					Toast.makeText(NormalAttendence.this,"文件存储位置："+StaticValue.save_file_path,Toast.LENGTH_LONG).show();
				}catch (IOException e){
					Toast.makeText(NormalAttendence.this,"解压失败！",Toast.LENGTH_LONG).show();
					System.out.println("解压失败");
				}
			}else if(BluetoothTools.ACTION_FILE_RECIVE_PERCENT.equals(action)){//文件接收过程
				read_file_show_dialog.setMax(StaticValue.file_send_length);
				read_file_show_dialog.setProgress(StaticValue.file_send_percent);
			}

		}

	};




}
