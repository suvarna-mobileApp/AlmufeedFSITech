package com.android.almufeed.ui.services;

import static com.android.almufeed.business.domain.utils.BaseUtilsKt.isOnline;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.room.Room;
import com.android.almufeed.R;
import com.android.almufeed.business.domain.utils.dataStore.BasePreferencesManager;
import com.android.almufeed.datasource.cache.database.BookDatabase;
import com.android.almufeed.datasource.cache.models.book.BookEntity;
import com.android.almufeed.datasource.cache.models.offlineDB.TaskEntity;
import com.android.almufeed.datasource.network.models.tasklist.TaskListRequest;
import com.android.almufeed.datasource.network.models.tasklist.TaskListResponse;
import com.android.almufeed.ui.home.APIServices;
import com.android.almufeed.ui.home.TaskActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyService extends Service  {
    private static final String CHANNEL_ID = "this.is.my.channelId";
    private static BasePreferencesManager basePreferencesManager;
    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments", 1);
        thread.start();
        Log.d("onCreate()", "After service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(isOnline(this)) {
            checkForTask();
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void checkForTask() {
        SharedPreferences sh = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        String token = sh.getString("token", "");
        String username = sh.getString("username", "");
        TaskListRequest taskRequest = new TaskListRequest(username);
        Call<TaskListResponse> getToken = APIServices.Companion.notcreate(this).getTaskListForNotification(token,taskRequest);
        BookDatabase db = Room.databaseBuilder(this, BookDatabase.class, BookDatabase.DATABASE_NAME).allowMainThreadQueries().build();
        System.out.println("sharedpreference " + token + " " + username);
        try {
            getToken.enqueue(new Callback<TaskListResponse>() {
                @Override
                public void onResponse(Call<TaskListResponse> call, Response<TaskListResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            List<TaskEntity> taskList = db.bookDao().getAllTask();
                            int commonSize = Math.min(response.body().getTask().size(), taskList.size());
                            System.out.println("running service response size " + response.body().getTask().size() + " DB size " + taskList.size());
                           /* for (int i = commonSize; i < response.body().getTask().size(); i++) {
                                PushNotification();
                            }*/
                            if(response.body().getTask().size() > taskList.size()){
                                PushNotification();
                            }
                           /* if(taskList.size() < response.body().getTask().size()){
                                PushNotification();
                            }*/
                            /*if(taskId != null && taskId.size() > 0){
                                for(int i=0; i < response.body().getTask().size(); i++){
                                    System.out.println("running service for 5 sec inside response for " + response.body().getTask().get(i).getTaskId());
                                    if(response.body().getTask().get(i).getTaskId().equals(taskId.get(i).getTask_id())){
                                        //PushNotification();
                                        //System.out.println("taskId " + taskId.getTask_id());
                                    }else{
                                        System.out.println("running service for 5 sec inside notify " + taskId);
                                        PushNotification();
                                    }
                                }
                            }*/
                        }
                    }else{

                    }
                    db.close();
                }

                @Override
                public void onFailure(Call<TaskListResponse> call, Throwable t) {
                   // Log.d("failure", t.getMessage());
                    //Toast.makeText(LoginActivity.this, "onFailure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void PushNotification(){
        Intent notificationIntent = new Intent(this, TaskActivity.class);//on tap this activity will open
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(TaskActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,  PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(this);

        Notification notification = builder.setContentTitle("Al Mufeed Group")
                .setContentText("New Task Assigned")
                .setTicker("New Message Alert!")
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.almufeedlogoclr)
                .setDefaults(Notification.DEFAULT_ALL) // must requires VIBRATE permission
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(pendingIntent).build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "NotificationDemo",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0, notification);
    }
}
