package com.example.compaq.nfc_student;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nfc_student.R;

public class Reflect_information extends Activity{

	TextView reflect_infor_title;
	EditText reflect_infor_input;
	Button reflect_infor_OK;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reflect_infor);

		reflect_infor_title=(TextView)findViewById(R.id.reflect_infor_title);
		reflect_infor_input=(EditText)findViewById(R.id.reflect_infor_input);
		//reflect_infor_input.setSelection(0);
		reflect_infor_OK=(Button)findViewById(R.id.reflect_infor_OK);
		reflect_infor_OK.setOnClickListener(new listener());
	}

	class listener implements View.OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (arg0.getId()) {
				case R.id.reflect_infor_OK:
					//Toast.makeText(Reflect_information.this, "你点击了确认", Toast.LENGTH_SHORT).show();

					//Toast.makeText(Reflect_information.this, "你点击了确认", Toast.LENGTH_SHORT).show();
					AlertDialog.Builder alertdialog_long=new AlertDialog.Builder(Reflect_information.this);
					alertdialog_long.setTitle("是否在签到时发送？");
					alertdialog_long.setNegativeButton("暂先保存", null);
					alertdialog_long.setPositiveButton("确认发送", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							StaticValue.reflect_information=reflect_infor_input.getText().toString();
							Toast.makeText(getApplicationContext(), StaticValue.reflect_information, Toast.LENGTH_LONG).show();
							Intent intent=new Intent();
							intent.setClass(Reflect_information.this,MainActivity.class);
							Reflect_information.this.startActivity(intent);
							finish();
						}
					});
					alertdialog_long.show();
					break;

				default:
					break;
			}
		}



	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "回到主页", Toast.LENGTH_LONG).show();
		Intent intent=new Intent();
		intent.setClass(Reflect_information.this,MainActivity.class);
		Reflect_information.this.startActivity(intent);
		finish();
		return super.onKeyDown(keyCode, event);
	}

}
