package com.justcode.baohuo;

import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import java.util.List;

/**
 * Created by niejun on 2018/1/4.
 */

public class JobHandlerService extends JobService {
    private int jobId = 0x0008;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        scheduleJob(getJobInfo());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        //用于唤醒线程
        boolean isLocalServiceWorking = isServiceWork(this,"com.justcode.baohuo.LocalService");
        boolean isRemoteServiceWorking = isServiceWork(this,"com.justcode.baohuo.RemoteService");

        if (!isLocalServiceWorking || !isRemoteServiceWorking){
            this.startService(new Intent(this,LocalService.class));
            this.startService(new Intent(this,RemoteService.class));
        }
        return true;
    }

    public boolean isServiceWork(Context context,String serviceName){
        boolean isWorked = false;
        ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> list = manager.getRunningServices(128);
        if (list.size() < 0) {
            return false;
        }

        for (int i = 0; i < list.size(); i++) {
            String name = list.get(i).service.getClassName().toString();
            if (serviceName.equals(name)) {
                isWorked = true;
                break;
            }
        }
        return isWorked;
    }
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }

    private void scheduleJob(JobInfo job){
        JobScheduler js = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        js.schedule(job);
    }

    private JobInfo getJobInfo(){
        JobInfo.Builder builder = new JobInfo.Builder(jobId,new ComponentName(this,JobHandlerService.class));
        builder.setPersisted(true);
        builder.setPeriodic(100);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setRequiresCharging(false);
        builder.setRequiresDeviceIdle(false);
        return builder.build();
    }
}
