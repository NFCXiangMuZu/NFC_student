package com.example.compaq.nfc_student;

import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.os.Environment;

public class StaticValue {
	public static BluetoothAdapter bluetoothadapter= BluetoothAdapter.getDefaultAdapter();
	public static String setnumber="请登录";
	public static String setname="请登录";
	public static String reflect_information="无信息反馈";
	public static int status=0;
	public static  Uri path=null;
	public static int count=0;
	//public static int mark=0;
	public static int start_mark=0;
	public static int bind_mark=1;
	public static BluetoothSocket socket=null;
	public static String macaddress=bluetoothadapter.getAddress();
	public static String remote_macaddress=null;
	public static String filename_for_send=null;
	public static String save_file_path = null;
	public static String file_path=null;
	public static String filename_for_middle=null;
	public static int file_send_percent = 0;
	public static int file_send_length = 0;
	public static double file_send_time = 0.0;
	public static String SDPATH = Environment.getExternalStorageDirectory().getPath();




}
