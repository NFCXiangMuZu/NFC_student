package com.example.compaq.nfc_student;

/**
 * 用于延迟解绑机制的计时服务
 */

import java.util.Timer;
import java.util.TimerTask;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class TimeService extends Service  
{
	
    private Timer timer = null;  
    private TimerTask task=new TimerTask() {  
        @Override  
        public void run() {
            System.out.println("-----------out----------"+StaticValue.count);
            StaticValue.count++;
            if(StaticValue.count>50){
            	StaticValue.count=0;
            	StaticValue.start_mark=0;
            	StaticValue.bind_mark=1;
            }
        }  
    };
    
    @Override  
    public void onCreate() {  
        super.onCreate();
        timer = new Timer();
        timer.schedule(task,1000,1000); 
        
    } 
	
	public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        System.out.println("-------ondestroy-------");
    }

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}  
