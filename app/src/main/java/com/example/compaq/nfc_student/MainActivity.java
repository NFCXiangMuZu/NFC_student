package com.example.compaq.nfc_student;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.nfc_student.R;

public class MainActivity extends Activity {

	Button set_button;
	Button Open_NFC;
	Button sign_button;
	Button reflect_button;
	Button middle_button;
	NfcAdapter nfcadapter;


	int count=0;

	private Handler handler = new Handler();

	private Runnable myRunnable= new Runnable() {
		public void run() {
			handler.postDelayed(this, 1000);
			count++;
			//file_chooser.setText("Count: " + count);

		}
	};

	//定义service
	TimeService mytime_service;

	private ServiceConnection m_serviceConn = new ServiceConnection()
	{

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			// TODO Auto-generated method stub
			mytime_service=(TimeService)arg1;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			// TODO Auto-generated method stub

		}
	};

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		set_button=(Button)findViewById(R.id.set_button);
		set_button.setOnClickListener(new listener());
		Open_NFC=(Button)findViewById(R.id.NFC_button);
		Open_NFC.setOnClickListener(new listener());
		sign_button=(Button)findViewById(R.id.sign_button);
		sign_button.setOnClickListener(new listener());
		reflect_button=(Button)findViewById(R.id.reflect_button);
		reflect_button.setOnClickListener(new listener());
		middle_button=(Button)findViewById(R.id.middle_button);
		middle_button.setOnClickListener(new listener());

		setTitle(StaticValue.setname+"("+StaticValue.setnumber+")");

		//获取手机唯一的IMEI号
		TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		String szImei = TelephonyMgr.getDeviceId();
		System.out.println("IMEI为："+szImei);
		//Toast.makeText(this, "IMEI为："+szImei,Toast.LENGTH_LONG).show();
		/*
		if(StaticValue.path!=null){
			Uri[] uris = new Uri[1];
			uris[0] = StaticValue.path;
			nfcadapter.setBeamPushUris(uris, this);
			System.out.println("callback成功");
			//android Beam功能使用结束
		}
		else{
			Toast.makeText(this,"没有文件可发!", Toast.LENGTH_LONG).show();
		}
        */
	}







	//总监听器
	class listener implements View.OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch(arg0.getId()){
				case R.id.set_button:
					Intent intent=new Intent();
					intent.setClass(MainActivity.this,self_information.class );
					MainActivity.this.startActivity(intent);
					finish();
					break;
				case R.id.NFC_button:
					new OpenNFC(MainActivity.this);
					break;
				case R.id.sign_button:
					Intent intent_sign=new Intent();
					intent_sign.setClass(MainActivity.this,NormalAttendence.class );
					MainActivity.this.startActivity(intent_sign);
					finish();
					break;
				case R.id.reflect_button:
					Intent intent_relect=new Intent();
					intent_relect.setClass(MainActivity.this,Reflect_information.class );
					MainActivity.this.startActivity(intent_relect);
					finish();
					break;
				case R.id.middle_button:
					Intent intent_middleAttendence=new Intent();
					intent_middleAttendence.setClass(MainActivity.this,MiddleAttendence.class );
					MainActivity.this.startActivity(intent_middleAttendence);
					finish();
					break;
			}
		}

	}



	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}



	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}



	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if(StaticValue.start_mark==1){
			startService(new Intent(MainActivity.this,TimeService.class));
		}
	}



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		stopService(new Intent(MainActivity.this,TimeService.class));
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, 1, 1, R.string.exit);
		menu.add(0, 2, 2, R.string.about);
		return super.onCreateOptionsMenu(menu);
	}



	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		AlertDialog.Builder alertdialog_long=new AlertDialog.Builder(MainActivity.this);
		alertdialog_long.setTitle("是否退出我们的软件");
		alertdialog_long.setNegativeButton("暂时不用", null);
		alertdialog_long.setPositiveButton("确认退出", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub

				finish();
			}
		});
		alertdialog_long.show();
		return super.onKeyDown(keyCode, event);
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId()==1){
			finish();
		}
		switch(item.getItemId()){
			case 1:
				finish();
				break;
			case 2:
				Toast.makeText(getApplicationContext(), "我们棒棒哒！", Toast.LENGTH_LONG).show();
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}


}
