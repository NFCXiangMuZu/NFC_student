package com.example.compaq.nfc_student;

/**
 * 用于开启NFC的工具类
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

public class OpenNFC {
	public OpenNFC(final Context c) {
		new AlertDialog.Builder(c)
				.setTitle("提示")
				.setMessage("是否开启nfc呢?")
				.setNegativeButton("是", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						Intent callGPSSettingIntent = new Intent(
								android.provider.Settings.ACTION_SETTINGS);
						c.startActivity(callGPSSettingIntent);
					}
				})
				.setPositiveButton("否", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
					}
				}).create().show();
	}

}
