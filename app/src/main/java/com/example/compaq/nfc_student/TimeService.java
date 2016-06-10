package com.example.compaq.nfc_student;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public class TimeService extends Service  
{
	
    private Timer timer = null;  
    private TimerTask task=new TimerTask() {  
        @Override  
        public void run() {  
            //���͹㲥  
            System.out.println("-----------out----------"+StaticValue.count);
            StaticValue.count++;
            //self_information.time_show.setText("ʣ��ʱ�䣺"+(20-StaticValue.count));
            if(StaticValue.count>50){
            	StaticValue.count=0;
            	StaticValue.start_mark=0;
            	StaticValue.bind_mark=1;
            	timer.cancel();
            	task.cancel();
            }
        }  
    };
    
    @Override  
    public void onCreate() {  
        super.onCreate();   
        
        timer = new Timer();  
        //��ʱ�����͹㲥  
        timer.schedule(task,1000,1000); 
        
    } 
	
	public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
        /*
         * //������OnCreate���洴�������ֵ����ӣ�Ҳ������OnStart���洴�� mediaPlayer =
         * MediaPlayer.create(this, R.raw.test);
         */
        //System.out.println("��ʱ��ʼ");
        	 //Toast.makeText(this, "��ʱ��ʼ", Toast.LENGTH_LONG).show();
       
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        System.out.println("-------ondestroy-------");
        /*
        if(StaticValue.mark==1){
        	System.out.println("��ʱ����,mark="+StaticValue.mark);
        	
				//timer.cancel();
        	    task.cancel();
				System.out.println("timer����,mark="+StaticValue.mark);
        	Toast.makeText(this, "��ʱ����", Toast.LENGTH_LONG).show();
        }*/

    }

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

  
}  
