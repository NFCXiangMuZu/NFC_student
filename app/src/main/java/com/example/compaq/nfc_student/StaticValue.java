package com.example.compaq.nfc_student;

/**
 * 公共变量定义
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.os.Environment;

public class StaticValue {
	public static BluetoothAdapter bluetoothadapter= BluetoothAdapter.getDefaultAdapter();

	//默认绑定的信息
	public static String setnumber="请登录";
	public static String setname="请登录";
	public static String reflect_information="无信息反馈";

	//签到状态标志，拍卡成功值为1
	public static int status=0;

	//延迟解绑机制所用变量
	public static int count=0;
	public static int start_mark=0;
	public static int bind_mark=1;

	//蓝牙所用变量
	public static BluetoothSocket socket=null;
	public static String macaddress=bluetoothadapter.getAddress();

	//远程设备蓝牙地址
	public static String remote_macaddress=null;

	//从教师端接收文件的暂存位置
	public static String save_file_path = null;

	//点名中继要发送文件的暂存位置
	public static String filename_for_middle=null;

	//点名中继文件发送过程所用变量
	public static int file_send_percent = 0;
	public static int file_send_length = 0;
	public static double file_send_time = 0.0;

	public static String SDPATH = Environment.getExternalStorageDirectory().getPath();
}
