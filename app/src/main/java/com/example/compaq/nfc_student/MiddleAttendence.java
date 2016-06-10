package com.example.compaq.nfc_student;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.FormatException;
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
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nfc_student.R;

@SuppressLint("NewApi")
public class MiddleAttendence extends Activity implements CreateNdefMessageCallback,OnNdefPushCompleteCallback{

	private static final int MESSAGE_SENT = 0;
	TextView middle_atten_text;
	Button middle_atten_button;
	NfcAdapter nfcadapter;
	PendingIntent pendingintent;
	public List<String> list=new ArrayList<String>();

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.middle_attendence);

		//初始化list
		list.add(StaticValue.setname);
		list.add(StaticValue.setnumber);
		list.add(StaticValue.reflect_information);

		nfcadapter=NfcAdapter.getDefaultAdapter(this);
		pendingintent=PendingIntent.getActivity(this, 0,
				new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		middle_atten_text=(TextView)findViewById(R.id.middle_atten_text);
		middle_atten_button=(Button)findViewById(R.id.middle_atten_button);
		middle_atten_button.setOnClickListener(new listener());

		nfcadapter.setNdefPushMessageCallback(this, this);
		nfcadapter.setOnNdefPushCompleteCallback(this, this);

	}

	//按钮监听器
	class listener implements View.OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (arg0.getId()) {
				case R.id.middle_atten_button:
					Intent intent=new Intent();
					intent.setClass(MiddleAttendence.this,MainActivity.class);
					MiddleAttendence.this.startActivity(intent);
					finish();
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
					//Toast.makeText(getApplicationContext(), "传输成功", Toast.LENGTH_LONG).show();
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


	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
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

	@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
	@SuppressLint("NewApi")
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		System.out.println("----onPause----");
		if(NfcAdapter.getDefaultAdapter(this)!=null){
			NfcAdapter.getDefaultAdapter(this).disableForegroundDispatch(this);
		}
	}

	@SuppressLint("NewApi")
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



	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "回到主页", Toast.LENGTH_LONG).show();
		Intent intent=new Intent();
		intent.setClass(MiddleAttendence.this,MainActivity.class);
		MiddleAttendence.this.startActivity(intent);
		finish();
		return super.onKeyDown(keyCode, event);
	}

	//新建一个record
	@SuppressLint("NewApi")
	private NdefRecord createRecord(String text) throws UnsupportedEncodingException {

		System.out.println("----createRecord----");

		// String nameVcard = "BEGIN:VCARD" +"\n"+ "VERSION:2.1" +"\n" + "N:;" + "\n" +"ORG: PlanAyala"+"\n"+ "TEL;HOME:6302421" +"\n"+ "END:VCARD";
		String nameVcard = text;
		byte[] uriField = nameVcard.getBytes();
		byte[] payload = new byte[uriField.length + 1];              //add 1 for the URI Prefix
		//payload[0] = 0x01;                                      //prefixes http://www. to the URI
		System.arraycopy(uriField, 0, payload, 1, uriField.length);  //appends URI to payload

		NdefRecord nfcRecord = new NdefRecord(
				NdefRecord.TNF_MIME_MEDIA, "text/vcard".getBytes(), new byte[0], payload);


		return nfcRecord;
	}

	@SuppressLint("NewApi")
	private NdefMessage getNoteAsNdef() throws UnsupportedEncodingException {
		System.out.println("----getNoteAsNdef----");

		NdefRecord[] list_record=new NdefRecord[list.size()];

		if (StaticValue.setname.equals("")) {
			return null;
		} else {
			for(int i=0;i<list.size();i++){
				list_record[i]=createRecord(list.get(i).toString());

				//System.out.println("结果"+(i+1)+"为："+list.get(i).toString());
			}

			//System.out.println("要写入的text是："+getText(textRecord.getPayload()));
			return new NdefMessage(list_record);
		}

	}

	@SuppressLint("NewApi")
	protected void resolveIntent(Intent intent) throws UnsupportedEncodingException, FormatException {
		// 得到是否TAG触发
		System.out.println("----resolveIntent----");
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())
				|| NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())
				|| NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()))
		{
			//autowrite(intent);
			// 处理该intent
			//Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			Parcelable[] rawMsgs =
					intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			// only one message sent during the beam
			NdefMessage msg = (NdefMessage)rawMsgs[0];
			// 获取id数组
			//byte[] bytesId = tag.getId();
			System.out.println("自动写入成功");
			String result_strname=new String(msg.getRecords()[0].getPayload(),"UTF-8");
			String result_strxuehao=new String(msg.getRecords()[1].getPayload(),"UTF-8").substring(1,11);
			String result_strreflect_infor=new String(msg.getRecords()[2].getPayload(),"UTF-8");
			list.add(result_strname);
			list.add(result_strxuehao);
			list.add(result_strreflect_infor);
			middle_atten_text.setText("保存成功\n"+"数组的长度为"+list.size()
					+"\n姓名是："+list.get(list.size()-3).toString()
					+"\n学号是："+list.get(list.size()-2).toString()
					+"\n反馈信息是："+list.get(list.size()-1).toString());
			Toast.makeText(this, "保存成功\n"
					+"姓名是："+list.get(list.size()-3).toString()
					+"\n学号是："+list.get(list.size()-2).toString()
					+"\n反馈信息是："+list.get(list.size()-1).toString(), Toast.LENGTH_LONG).show();

		}
	}



}
