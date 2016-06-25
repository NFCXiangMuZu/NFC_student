package com.example.compaq.nfc_student;

import java.io.IOException;

import java.io.Serializable;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class ReadFileService extends Service {


	//蓝牙通讯
	private BluetoothCommunSocket communSocket;

	//蓝牙连接进程
	BluetoothServerThread server_thread;

	//控制信息广播的接收器
	/*
	BroadcastReceiver readReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("广播收到！");
			//intent.getExtras().getSerializable(BluetoothTools.DATA);
			String action = intent.getAction();
			if (BluetoothTools.ACTION_DATA_TO_GAME.equals(action)) {
				communSocket = new BluetoothCommunSocket(handler,StaticValue.socket);
				if (communSocket != null) {
					class MyRunnable implements Runnable{
						public void run(){
							try {
								communSocket.read();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							System.out.println("=====接受成功！=======");
						}
					}
					Thread t=new Thread(new MyRunnable());
					t.start();
				}

			}
		}
	};
	*/


	//接收其他线程消息的Handler
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			//处理消息
			switch (msg.what) {
				case BluetoothTools.MESSAGE_CONNECT_ERROR://连接错误
					//发送连接错误广播
					Intent errorIntent = new Intent(BluetoothTools.ACTION_CONNECT_ERROR);
					sendBroadcast(errorIntent);
					break;
				case BluetoothTools.MESSAGE_CONNECT_SUCCESS://连接成功
					//开启通讯线程
					//	communSocket = new BluetoothCommunSocket(handler, (BluetoothSocket)msg.obj);
					//	communSocket.start();
					//发送连接成功广播
//						Intent succIntent = new Intent(BluetoothTools.ACTION_CONNECT_SUCCESS);
//						sendBroadcast(succIntent);
					break;
				case BluetoothTools.MESSAGE_READ_OBJECT://读取到对象
					//发送数据广播（包含数据对象）
					Intent dataIntent = new Intent(BluetoothTools.ACTION_DATA_TO_GAME);
					dataIntent.putExtra(BluetoothTools.DATA, (Serializable)msg.obj);
					sendBroadcast(dataIntent);
					break;
				case BluetoothTools.FILE_SEND_PERCENT://文件发送百分比
					//发送文件传输百分比广播，实现进度条用
					Intent flieIntent = new Intent(BluetoothTools.ACTION_FILE_SEND_PERCENT);
					flieIntent.putExtra(BluetoothTools.DATA, (Serializable)msg.obj);
					sendBroadcast(flieIntent);
					break;
				case BluetoothTools.FILE_RECIVE_PERCENT://文件接收百分比
					//接收文件传输百分比广播，实现进度条用
					//Log.v("调试" , "接收开始！！！！！");
					System.out.println("接收中！！！！");
					Intent flieIntent1 = new Intent(BluetoothTools.ACTION_FILE_RECIVE_PERCENT);
					flieIntent1.putExtra(BluetoothTools.DATA, (Serializable)msg.obj);
					sendBroadcast(flieIntent1);
					break;
				case BluetoothTools.FILE_RECEIVE_SUCCESS://文件接收成功
					Log.v("调试" , "接收成功！！！！！");
					Intent success_intent = new Intent(BluetoothTools.ACTION_FILE_RECEIVE_SUCCESS);
					success_intent.putExtra(BluetoothTools.DATA, (Serializable)msg.obj);
					sendBroadcast(success_intent);
					break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		System.out.println("服务开启！");
		/*
		IntentFilter intentfilter=new IntentFilter();
		intentfilter.addAction(BluetoothTools.ACTION_DATA_TO_GAME);
		registerReceiver(readReceiver, intentfilter);
		*/

	}



	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		server_thread=new BluetoothServerThread(handler);
		server_thread.start();
		super.onCreate();
	}



	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}

