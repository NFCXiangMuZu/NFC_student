package com.example.compaq.nfc_student;

import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.net.Uri;
import android.nfc.NdefMessage;

public class StaticValue {
	public static BluetoothAdapter bluetoothadapter= BluetoothAdapter.getDefaultAdapter();
	public static String setnumber="1325114014";
	public static String setname="黄明";
	public static String reflect_information="无信息反馈";
	public static int status=0;
	public static  Uri path=null;
	public static int count=0;
	//public static int mark=0;
	public static int start_mark=0;
	public static int bind_mark=1;
	public static BluetoothSocket socket=null;
	public static String macaddress=bluetoothadapter.getAddress();
	public static String filename_for_send=null;
	public static String file_path=null;


}