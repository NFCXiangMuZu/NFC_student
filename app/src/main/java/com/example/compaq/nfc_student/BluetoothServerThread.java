package com.example.compaq.nfc_student;

/**
 * 接收教师端连接的学生端服务器线程
 */

import java.io.IOException;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

public class BluetoothServerThread extends Thread {

	private Handler serviceHandler;		//用于同Service通信的Handler
	private BluetoothAdapter adapter;
	private BluetoothSocket socket;		//用于通信的Socket
	private BluetoothServerSocket serverSocket;
	private boolean isInterrupted=false;
	//蓝牙通讯线程
	private  BluetoothComThread communThread;

	/**
	 * 构造函数
	 * @param handler
	 */
	public BluetoothServerThread(Handler handler) {
		this.serviceHandler = handler;
		adapter = BluetoothAdapter.getDefaultAdapter();
	}

	/**
	 * 关闭连接
	 */
	public void close(){
		isInterrupted = true;
		if (communThread != null) {
			communThread.close();
			communThread = null;
		}
		if (serverSocket != null) {
			try {
				serverSocket.close();
				Log.v("调试" , "serverSocket已关闭");
			} catch (IOException e) {
				Log.e("调试", "serverSocket关闭 failed", e);
				e.printStackTrace();
			}
		}
		if (socket != null) {
			try {
				socket.close();
				Log.v("调试" , "socket已关闭");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		super.interrupt();
	}

	@Override
	public void run() {
		try {
			System.out.println("进程开始！");
			serverSocket = adapter.listenUsingRfcommWithServiceRecord("Server", BluetoothTools.PRIVATE_UUID);
			while(!isInterrupted){
				socket = serverSocket.accept();
				//StaticValue.socket=socket;
				if (socket != null) {
					//开启通讯线程
					communThread = new BluetoothComThread(serviceHandler, socket);
					communThread.start();
				} else {
					//发送连接失败消息
					serviceHandler.obtainMessage(BluetoothTools.MESSAGE_CONNECT_ERROR).sendToTarget();
					break;
				}
			}
		} catch (Exception e) {
			//发送连接失败消息
			serviceHandler.obtainMessage(BluetoothTools.MESSAGE_CONNECT_ERROR).sendToTarget();
			e.printStackTrace();
			return;
		}
	}

}


