package com.example.compaq.nfc_student;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nfc_student.R;

public class self_information extends Activity implements OnClickListener {
	Button button;
	private EditText ednumber;
	private EditText edname;

	private int i = 0;
	//private Timer timer = null;
	//private TimerTask task = null;
	public static TextView time_show;
	public Intent time_service=new Intent(self_information.this,TimeService.class);


	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.self_information);

		ednumber = (EditText) findViewById(R.id.ednumber);
		edname = (EditText) findViewById(R.id.edname);
		button=(Button)findViewById(R.id.sure);
		button.setOnClickListener(this);
		time_show=(TextView)findViewById(R.id.time_show);
		//timer.schedule(task, 100);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.sure:
				//self_information.this.startService(time_service);
				if(TextUtils.isEmpty(ednumber.getText().toString().trim())
						||TextUtils.isEmpty(edname.getText().toString().trim())){
					//Toast.makeText(this,"学号或姓名不能为空",Toast.LENGTH_SHORT).show();
					AlertDialog.Builder alertdialog=new AlertDialog.Builder(self_information.this);
					alertdialog.setTitle("输入不允许为空");
					//alertdialog.setNegativeButton("取消", null);
					alertdialog.setPositiveButton("重新输入", null);
					alertdialog.show();
					return;
				}
				if(StaticValue.bind_mark==1||StaticValue.start_mark==0){
					AlertDialog.Builder alertdialog_button=new AlertDialog.Builder(self_information.this);
					alertdialog_button.setTitle("确认绑定后两小时不能解绑\n是否绑定？");
					alertdialog_button.setNegativeButton("再检查一下", null);
					alertdialog_button.setPositiveButton("确认绑定", new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							StaticValue.start_mark=1;
							StaticValue.bind_mark=0;
							System.out.println("start_mark="+StaticValue.start_mark);
							System.out.println("bind_mark="+StaticValue.bind_mark);
							StaticValue.setnumber = ednumber.getText().toString().trim();
							StaticValue.setname = edname.getText().toString().trim();
							System.out.println("+++++++"+StaticValue.setnumber);
							System.out.println("+++++++"+StaticValue.setname);
							Intent intent=new Intent();
							intent.setClass(self_information.this,MainActivity.class);
							self_information.this.startActivity(intent);
							self_information.this.startService(new Intent(self_information.this,TimeService.class));
							finish();
						}

					});
					alertdialog_button.show();
				}
				else
				{
					AlertDialog.Builder alertdialog_button=new AlertDialog.Builder(self_information.this);
					alertdialog_button.setTitle("请稍后绑定");
					alertdialog_button.setPositiveButton("好的",null);
					alertdialog_button.show();

				}

				break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "回到主页", Toast.LENGTH_LONG).show();
		Intent intent=new Intent();
		intent.setClass(self_information.this,MainActivity.class);
		self_information.this.startActivity(intent);
		finish();
		return super.onKeyDown(keyCode, event);
	}
	/*
	private Handler mHandler = new Handler(){
		  public void handleMessage(Message msg) {
		   time_show.setText(msg.arg1+"");
		   startTime();
		  };
		 };
		  
		 public void startTime(){
		  timer = new Timer();
		  task = new TimerTask() {
		    
		   @Override
		   public void run() {
		    i--;
		    Message  message = mHandler.obtainMessage();
		    message.arg1 = i;
		    mHandler.sendMessage(message);
		   }
		  };
		  timer.schedule(task, 1000);
		 }
		  
		 public void stopTime(){
		  timer.cancel();
		 }
       */

}
