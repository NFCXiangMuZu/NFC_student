package com.example.compaq.nfc_student;

/**
 * 学生端主界面实现
 */

import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.example.nfc_student.R;

public class MainActivity extends Activity {

	NfcAdapter nfcadapter;
	Button NavigationBar_leftbutton;
	Button NavigationBar_folderbutton;
	Button Normal_Attendence_button;
	Button Middle_Attendence_button;
	TextView NavigationBar_title;

	//身份信息绑定弹出窗口
	Button sign_in_window_close_button;
	EditText sign_in_window_xuehao_edittext;
	EditText sign_in_window_name_edittext;
	Button sign_in_window_confirm_button;
	PopupWindow sign_in_window = null;

	//折叠菜单对象
	PopupMenu folder_menu = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置无标题
		setContentView(R.layout.activity_main);

		//初始化layout
		init_layout();

		//建立文件暂存文件夹
		FileHelper.mkDir(StaticValue.SDPATH+"/NFC-课堂点名/");

	}

	/**
	 * 初始化layout
	 */
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

	//绑定身份信息弹出窗口
	private void show_sign_in_window(){

		View contentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.popupwindows_layout, null);
		//sign_in_window = new PopupWindow(contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		//获取屏幕大小
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		sign_in_window = new PopupWindow(contentView,dm.widthPixels/2,dm.heightPixels/3);
		sign_in_window.setFocusable(true);

		//初始化窗口中的layout元素
		sign_in_window_close_button = (Button)contentView.findViewById(R.id.sign_in_window_close_button);
		sign_in_window_xuehao_edittext = (EditText)contentView.findViewById(R.id.sign_in_window_xuehao_edit);
		sign_in_window_name_edittext = (EditText)contentView.findViewById(R.id.sign_in_window_name_edit);
		sign_in_window_confirm_button = (Button)contentView.findViewById(R.id.sign_in_window_confirm_button);

		sign_in_window_close_button.setOnClickListener(new listener());
		sign_in_window_confirm_button.setOnClickListener(new listener());

		//显示窗口
		View rootview = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_main, null);
		sign_in_window.showAtLocation(rootview, Gravity.CENTER, 0, 0);

	}

	/**
	 * 按钮动作总监听器
	 */
	class listener implements View.OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch(arg0.getId()){

				case R.id.navigationbar_folderbutton://点击右上角折叠按钮

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

				case R.id.navigationbar_leftbutton://点击左上角身份绑定按钮
					show_sign_in_window();
					break;
				//身份绑定弹出窗口中的按钮
				case R.id.sign_in_window_close_button://身份绑定窗口关闭按钮
					sign_in_window.dismiss();
					break;
				case R.id.sign_in_window_confirm_button://身份绑定窗口中的确认按钮
					//首先检查用户输入是否为空
					if(TextUtils.isEmpty(sign_in_window_xuehao_edittext.getText().toString().trim())
							||TextUtils.isEmpty(sign_in_window_name_edittext.getText().toString().trim())){
						AlertDialog.Builder alertdialog=new AlertDialog.Builder(MainActivity.this);
						alertdialog.setTitle("输入不允许为空");
						alertdialog.setPositiveButton("重新输入", null);
						alertdialog.show();
						return;
					}
					//实现延迟解绑机制
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
								//学生身份信息绑定
								StaticValue.setnumber = sign_in_window_xuehao_edittext.getText().toString().trim();
								StaticValue.setname = sign_in_window_name_edittext.getText().toString().trim();
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
				case R.id.normal_attendence_button://点击正常签到按钮
					Intent intent_normal=new Intent();
					intent_normal.setClass(MainActivity.this,NormalAttendence.class );
					MainActivity.this.startActivity(intent_normal);
					break;
				case R.id.middle_attendence_button://点击点名中继按钮
					Intent intent_middleAttendence=new Intent();
					intent_middleAttendence.setClass(MainActivity.this,MiddleAttendence.class );
					MainActivity.this.startActivity(intent_middleAttendence);
					finish();
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
