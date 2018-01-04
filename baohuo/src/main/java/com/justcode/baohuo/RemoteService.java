package com.justcode.baohuo;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class RemoteService extends Service {
    public static final String TAG = "RemoteService";
    RemoteServiceBinder remoteBinder;
    RemoteServiceConn remoteServiceConn;

    @Override
    public void onCreate() {
        super.onCreate();
        remoteBinder = new RemoteServiceBinder();
        remoteServiceConn = new RemoteServiceConn();
        RemoteService.this.bindService(new Intent(RemoteService.this,LocalService.class),remoteServiceConn, Context.BIND_IMPORTANT);
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        Notification notification;
        Notification.Builder builder = new Notification.Builder(this);
        builder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        builder.setContentTitle("测试数据");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentInfo("INfo");
        builder.setWhen(System.currentTimeMillis());
        PendingIntent pi = PendingIntent.getActivity(this,0,intent,0);
        builder.setContentIntent(pi);
        notification = builder.build();
        startForeground(startId,notification);
        startTimer();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return remoteBinder;
    }

    public class RemoteServiceConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            RemoteService.this.startService(new Intent(RemoteService.this,LocalService.class));
            RemoteService.this.bindService(new Intent(RemoteService.this,LocalService.class),remoteServiceConn, Context.BIND_IMPORTANT);
        }
    }
    public class RemoteServiceBinder extends IgServiceAidlInterface.Stub{

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }
    }


    private Timer timer;
    private TimerTask timerTask;
    long oldTime = 0;
    private int counter = 0;

    public void startTimer(){
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask,1000,1000);
    }

    public void stopTimerTask(){
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void initializeTimerTask() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.e(TAG, "int timer +++" + (counter++));
            }
        };
    }
}
