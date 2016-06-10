package com.example.compaq.nfc_student;


import java.io.IOException;
import java.io.InputStream;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nfc_student.R;

@SuppressLint("NewApi")
public class NormalAttendence extends Activity implements CreateNdefMessageCallback,OnNdefPushCompleteCallback{

	protected static final int MESSAGE_SENT = 0;
	NfcAdapter nfcadapter;
	PendingIntent pendingintent;
	TextView showtext;
	BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private final UUID MY_UUID = UUID.fromString("db764ac8-4b08-7f25-aafe-59d03c27bae3");
	private final String NAME = "Hyman";
	byte[] buffer = new byte[1024];

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.normalattendence);


		nfcadapter=NfcAdapter.getDefaultAdapter(this);
		showtext=(TextView)findViewById(R.id.textView1);
		showtext.setText("正在签到");

		pendingintent=PendingIntent.getActivity(this, 0,
				new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		nfcadapter.setNdefPushMessageCallback(this, this);
		nfcadapter.setOnNdefPushCompleteCallback(this, this);

		NormalAttendence.this.startService(new Intent(NormalAttendence.this,ReadFileService.class));

		//开启蓝牙接收线程
		Thread thread=new readThread();
		thread.start();




	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MESSAGE_SENT:
					//Toast.makeText(getApplicationContext(), "传输成功", Toast.LENGTH_LONG).show();
					//弹出框定义

					AlertDialog.Builder alertdialog=new AlertDialog.Builder(NormalAttendence.this);
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
							intent.setClass(NormalAttendence.this,MainActivity.class);
							NormalAttendence.this.startActivity(intent);
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

	@SuppressWarnings("deprecation")
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
    /*
	try {
		NfcAdapter.getDefaultAdapter(this).enableForegroundNdefPush(this,getNoteAsNdef());
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	*/
	}


	@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
	@SuppressLint("NewApi")
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		NfcAdapter.getDefaultAdapter(this).disableForegroundDispatch(this);
		/*
		System.out.println("----onPause----");
		if(NfcAdapter.getDefaultAdapter(this)!=null){

			NfcAdapter.getDefaultAdapter(this).disableForegroundNdefPush(this);
		}*/
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "回到主页", Toast.LENGTH_LONG).show();
		Intent intent=new Intent();
		intent.setClass(NormalAttendence.this,MainActivity.class);
		NormalAttendence.this.startActivity(intent);
		finish();
		return super.onKeyDown(keyCode, event);
	}


	//NFC数据格式
	private static enum NFCtype{
		UNKNOWN,TEXT,URI,SMART_POSTER,ABSOLUTE_URI
	}

	private NFCtype getTagType(final NdefMessage msg){
		if(msg==null){
			return null;
		}
		for(NdefRecord record:msg.getRecords()){
			if(record.getTnf()==NdefRecord.TNF_WELL_KNOWN){
				if(Arrays.equals(record.getType(), NdefRecord.RTD_TEXT)){
					System.out.println("Tag的类型是text");
					return NFCtype.TEXT;
				}
				if(Arrays.equals(record.getType(), NdefRecord.RTD_URI)){
					System.out.println("Tag的类型是URI");
					return NFCtype.URI;
				}
				if(Arrays.equals(record.getType(), NdefRecord.RTD_SMART_POSTER)){
					System.out.println("Tag的类型是智能海报");
					return NFCtype.SMART_POSTER;
				}
			}
			else if(record.getTnf()==NdefRecord.TNF_ABSOLUTE_URI){
				System.out.println("Tag的类型是ABSOLUTE_URI");
				return NFCtype.ABSOLUTE_URI;
			}
		}
		return null;
	}

	//读取text格式的tag
	private String getText(final byte[] payload){

		System.out.println("----getText----");

		if(payload==null){
			return null;
		}
		try{
			String textencoding=((payload[0]&0200)==0)?"UTF-8":"UTF-16";
			int languageCodeLength=payload[0]&0077;
			return new String(payload,languageCodeLength+1,payload.length-languageCodeLength-1,textencoding);

		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	//新建一个record
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
    /*
    public class AcceptThread extends Thread{
        BluetoothServerSocket serverSocket;
        public AcceptThread() {
        	BluetoothServerSocket tmp=null;
            try{
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME,MY_UUID);
            }catch (Exception e){

            }
            serverSocket=tmp;
        }

        public void run(){
        	BluetoothSocket socket=null;
        	InputStream in;
            try {
            	socket=serverSocket.accept();
            	StaticValue.socket=socket;
            	if(socket!=null){
	            	System.out.println("蓝牙连接成功");
	            	//Toast.makeText(Bluetooth_Text.this, "蓝牙连接成功", Toast.LENGTH_LONG).show();


	            	NormalAttendence.this.startService(new Intent(NormalAttendence.this,ReadFileService.class));
	            	Thread readtext=new readThread(socket);
	            	readtext.start();
	            }


            }catch (Exception e){

            }

            }
        }
        */

	private class readThread extends Thread {


		public readThread(){

		}

		public void run() {

			Intent sendDataIntent = new Intent(BluetoothTools.ACTION_DATA_TO_GAME);
			sendBroadcast(sendDataIntent);
			System.out.println("广播成功！！！！");
			//Toast.makeText(Bluetooth_Text.this, buffer.toString(), Toast.LENGTH_LONG).show();
			//showtext.setText(buffer.toString());






		}
	}




}
