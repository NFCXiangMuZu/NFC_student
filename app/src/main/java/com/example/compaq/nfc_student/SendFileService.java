package com.example.compaq.nfc_student;

import java.io.IOException;
import java.io.Serializable;



import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class SendFileService extends Service {

	private BluetoothCommunSocket communSocket;
	
	BluetoothAdapter adapter=BluetoothAdapter.getDefaultAdapter();
	
	BluetoothSocket socket=null;
	BluetoothDevice device;
			BroadcastReceiver controlReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					System.out.println("开始连接");
					device=adapter.getRemoteDevice(StaticValue.macaddress);
	            	    if(device!=null){
	            	    	System.out.println("连接成功");
	            	    	System.out.println("设备名为："+device.getName());
	            	    }
					try {
						socket = device.createRfcommSocketToServiceRecord(BluetoothTools.PRIVATE_UUID);
						socket.connect();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				    
					String action = intent.getAction();		
			        if (BluetoothTools.ACTION_DATA_TO_SERVICE.equals(action)) {			
						communSocket = new BluetoothCommunSocket(handler,socket);				
						final TransmitBean transmit = (TransmitBean)intent.getExtras().getSerializable(BluetoothTools.DATA);
						if (communSocket != null) {
						class MyRunnable implements Runnable{
							public void run(){
								communSocket.write(transmit);
								System.out.println("======写入成功======");
							}
							}
						Thread t=new Thread(new MyRunnable());
						t.start();
						}
					}
				}
			};
			
			//接收信息Handler
			Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case BluetoothTools.MESSAGE_CONNECT_ERROR:
						//连接错误
						Intent errorIntent = new Intent(BluetoothTools.ACTION_CONNECT_ERROR);
						sendBroadcast(errorIntent);
						break;
					case BluetoothTools.MESSAGE_CONNECT_SUCCESS://���ӳɹ�			
						//连接成功
					//	communSocket = new BluetoothCommunSocket(handler, (BluetoothSocket)msg.obj);
					//	communSocket.start();
//						Intent succIntent = new Intent(BluetoothTools.ACTION_CONNECT_SUCCESS);
//						sendBroadcast(succIntent);
						break;
					case BluetoothTools.MESSAGE_READ_OBJECT://��ȡ������
						//信息读取成功
						Intent dataIntent = new Intent(BluetoothTools.ACTION_DATA_TO_GAME);
						dataIntent.putExtra(BluetoothTools.DATA, (Serializable)msg.obj);
						sendBroadcast(dataIntent);
						break;
					case BluetoothTools.FILE_SEND_PERCENT://文件读取成功
						System.out.println("=====文件发送中====");
						Intent flieIntent = new Intent(BluetoothTools.ACTION_FILE_SEND_PERCENT);
						flieIntent.putExtra(BluetoothTools.DATA, (Serializable)msg.obj);
						sendBroadcast(flieIntent);
						break;
					case BluetoothTools.FILE_RECIVE_PERCENT:
						//文件读取成功
						Intent flieIntent1 = new Intent(BluetoothTools.ACTION_FILE_RECIVE_PERCENT);
						flieIntent1.putExtra(BluetoothTools.DATA, (Serializable)msg.obj);
						sendBroadcast(flieIntent1);			
						break;
					case BluetoothTools.FILE_SEND_SUCCESS:
						//文件发送成功
						System.out.println("====+++++====文件发送成功===++++=====");
						//unregisterReceiver(controlReceiver);
						Intent file_send_success_Intent = new Intent(BluetoothTools.ACTION_FILE_SEND_SUCCESS);
						sendBroadcast(file_send_success_Intent);
					}
					super.handleMessage(msg);
				}
			};
	
	
	
	
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		System.out.println("服务开启");
		IntentFilter intentfilter=new IntentFilter();
		intentfilter.addAction(BluetoothTools.ACTION_DATA_TO_SERVICE);
		registerReceiver(controlReceiver, intentfilter);
		super.onCreate();
	}





	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(controlReceiver);
		super.onDestroy();
	}



	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
}

