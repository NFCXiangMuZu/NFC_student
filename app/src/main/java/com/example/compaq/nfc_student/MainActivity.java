package com.example.compaq.nfc_student;
import android.content.Context;
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
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nfc_student.R;

import java.util.Vector;

public class MainActivity extends Activity {

	NfcAdapter nfcadapter;
	Button NavigationBar_leftbutton;
	Button NavigationBar_folderbutton;
	Button Normal_Attendence_button;
	Button Middle_Attendence_button;
	TextView NavigationBar_title;

	//popupwindow
	Button sign_in_window_close_button;
	EditText sign_in_window_xuehao_edittext;
	EditText sign_in_window_name_edittext;
	Button sign_in_window_confirm_button;

	PopupMenu folder_menu = null;
	PopupWindow sign_in_window = null;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置无标题
		setContentView(R.layout.activity_main);

		//初始化layout
		init_layout();

		//建立软件文件暂存文件夹
		FileHelper.mkDir(StaticValue.SDPATH+"/NFC-课堂点名/");

		//获取手机唯一的IMEI号
		TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		String szImei = TelephonyMgr.getDeviceId();
		System.out.println("IMEI为："+szImei);
	}

	//初始化layout
	private void init_layout(){

		NavigationBar_title = (TextView)findViewById(R.id.navigationbar_title);
		NavigationBar_folderbutton = (Button)findViewById(R.id.navigationbar_folderbutton);
		NavigationBar_leftbutton = (Button)findViewById(R.id.navigationbar_leftbutton);
		Normal_Attendence_button = (Button)findViewById(R.id.normal_attendence_button);
		Middle_Attendence_button = (Button)findViewById(R.id.middle_attendence_button);

		NavigationBar_leftbutton.setOnClickListener(new listener());
		NavigationBar_folderbutton.setOnClickListener(new listener());
		Normal_Attendence_button.setOnClickListener(new listener());
		Middle_Attendence_button.setOnClickListener(new listener());

	}

	//绑定身份信息窗口
	private void show_sign_in_window(){

		View contentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.popupwindows_layout, null);
		//sign_in_window = new PopupWindow(contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		sign_in_window = new PopupWindow(contentView,600, 450);
		sign_in_window.setFocusable(true);

		//初始化layout
		sign_in_window_close_button = (Button)contentView.findViewById(R.id.sign_in_window_close_button);
		sign_in_window_xuehao_edittext = (EditText)contentView.findViewById(R.id.sign_in_window_xuehao_edit);
		sign_in_window_name_edittext = (EditText)contentView.findViewById(R.id.sign_in_window_name_edit);
		sign_in_window_confirm_button = (Button)contentView.findViewById(R.id.sign_in_window_confirm_button);

		sign_in_window_close_button.setOnClickListener(new listener());
		sign_in_window_confirm_button.setOnClickListener(new listener());

		//显示PopupWindow
		View rootview = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_main, null);
		sign_in_window.showAtLocation(rootview, Gravity.CENTER, 0, 0);

	}

	//总按钮监听器
	class listener implements View.OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch(arg0.getId()){

				case R.id.navigationbar_folderbutton:

					folder_menu = new PopupMenu(MainActivity.this,NavigationBar_folderbutton);
					//加载menu资源
					getMenuInflater().inflate(R.menu.folder_menu_items,folder_menu.getMenu());
					//绑定点击事件
					folder_menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

						public boolean onMenuItemClick(MenuItem item) {

							switch (item.getItemId()) {

								case R.id.folder_item_NFC:
									new OpenNFC(MainActivity.this);
									break;
							}

							return false;

						}
					});

					folder_menu.show();

					break;

				case R.id.navigationbar_leftbutton:
					show_sign_in_window();
					break;
				case R.id.normal_attendence_button:
					Intent intent_normal=new Intent();
					intent_normal.setClass(MainActivity.this,NormalAttendence.class );
					MainActivity.this.startActivity(intent_normal);
					break;
				case R.id.middle_attendence_button:
					Intent intent_middleAttendence=new Intent();
					intent_middleAttendence.setClass(MainActivity.this,MiddleAttendence.class );
					MainActivity.this.startActivity(intent_middleAttendence);
					finish();
					break;
				case R.id.sign_in_window_close_button:
					sign_in_window.dismiss();
					break;
				case R.id.sign_in_window_confirm_button:

					if(TextUtils.isEmpty(sign_in_window_xuehao_edittext.getText().toString().trim())
							||TextUtils.isEmpty(sign_in_window_name_edittext.getText().toString().trim())){
						//Toast.makeText(this,"学号或姓名不能为空",Toast.LENGTH_SHORT).show();
						AlertDialog.Builder alertdialog=new AlertDialog.Builder(MainActivity.this);
						alertdialog.setTitle("输入不允许为空");
						//alertdialog.setNegativeButton("取消", null);
						alertdialog.setPositiveButton("重新输入", null);
						alertdialog.show();
						return;
					}
					if(StaticValue.bind_mark==1||StaticValue.start_mark==0){

						//启动计时服务
						startService(new Intent(MainActivity.this,TimeService.class));

						AlertDialog.Builder alertdialog_button=new AlertDialog.Builder(MainActivity.this);
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
								StaticValue.setnumber = sign_in_window_xuehao_edittext.getText().toString().trim();
								StaticValue.setname = sign_in_window_name_edittext.getText().toString().trim();
								System.out.println("+++++++"+StaticValue.setnumber);
								System.out.println("+++++++"+StaticValue.setname);
								NavigationBar_leftbutton.setText(StaticValue.setname);
							}

						});
						alertdialog_button.show();
					}
					else
					{
						AlertDialog.Builder alertdialog_button=new AlertDialog.Builder(MainActivity.this);
						alertdialog_button.setTitle("请稍后绑定");
						alertdialog_button.setPositiveButton("好的",null);
						alertdialog_button.show();

					}

					break;
				default:
					break;
			}
		}

	}



	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		//更新左上角文字
		NavigationBar_leftbutton.setText(StaticValue.setname);
	}



	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//更新左上角文字
		NavigationBar_leftbutton.setText(StaticValue.setname);
	}



	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		//更新左上角文字
		NavigationBar_leftbutton.setText(StaticValue.setname);
	}



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
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

}
