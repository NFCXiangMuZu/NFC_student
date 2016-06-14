package com.example.compaq.nfc_student;

import java.io.InputStream;


import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.style.BulletSpan;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nfc_student.R;

@SuppressLint("NewApi")
public class Bluetooth_Text extends Activity implements CreateNdefMessageCallback,OnNdefPushCompleteCallback{

	protected static final int MESSAGE_SENT = 0;
	NfcAdapter nfcadapter;
	PendingIntent pendingintent;
	TextView showtext;
	BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private final UUID MY_UUID = UUID.fromString("db764ac8-4b08-7f25-aafe-59d03c27bae3");
	private final String NAME = "Hyman";
	byte[] buffer = new byte[1024];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bluetooth_view);

		nfcadapter=NfcAdapter.getDefaultAdapter(this);
		showtext=(TextView)findViewById(R.id.textView1);
		if(bluetoothAdapter!=null){
			//Toast.makeText(this, "蓝牙适配器非空", Toast.LENGTH_LONG).show();
			System.out.println("蓝牙适配非空");
			System.out.println("蓝牙适配器非空");
			if(bluetoothAdapter.isEnabled()){
				//showtext.setText(macaddress);
				//Toast.makeText(this,bluetoothAdapter.getName()+": "+bluetoothAdapter.getAddress(), Toast.LENGTH_LONG).show();
				System.out.println(bluetoothAdapter.getName()+": "+bluetoothAdapter.getAddress());
			}else{
				Toast.makeText(this, "蓝牙未打开", Toast.LENGTH_LONG).show();
				Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivity(intent);
			}
		}



		pendingintent=PendingIntent.getActivity(this, 0,
				new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		nfcadapter.setNdefPushMessageCallback(this, this);
		nfcadapter.setOnNdefPushCompleteCallback(this, this);
		Thread thread=new AcceptThread();
		thread.start();

	}


	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MESSAGE_SENT:
					//Toast.makeText(getApplicationContext(), "传输成功", Toast.LENGTH_LONG).show();
					//弹出框定义

					AlertDialog.Builder alertdialog=new AlertDialog.Builder(Bluetooth_Text.this);
					if(StaticValue.status==1){
						alertdialog.setTitle("                 发送完成");
					}
					else{
						alertdialog.setTitle("                 签到未完成");
					}
					alertdialog.setPositiveButton("回到主页",new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							Intent intent=new Intent();
							intent.setClass(Bluetooth_Text.this,MainActivity.class);
							Bluetooth_Text.this.startActivity(intent);
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
		if (mac_address.equals("")) {
			return null;
		} else {
			NdefRecord textRecord = createRecord(mac_address);
			//System.out.println("要写入的text是："+getText(textRecord.getPayload()));
			return new NdefMessage(new NdefRecord[] {textRecord});
		}

	}

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


					Bluetooth_Text.this.startService(new Intent(Bluetooth_Text.this,ReadFileService.class));
					Thread readtext=new readThread(socket);
					readtext.start();
				}


			}catch (Exception e){

			}

		}
	}

	private class readThread extends Thread {

		BluetoothSocket socket;

		public readThread(BluetoothSocket mysocket){

			socket=mysocket;

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
