/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dxy.samples.apps.sunflower.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.dxy.samples.apps.sunflower.GardenActivity
import com.dxy.samples.apps.sunflower.R
import com.dxy.samples.apps.sunflower.api.WOLService
import com.dxy.samples.apps.sunflower.utilities.WOL
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executors
import javax.inject.Inject


const val notificationId="WhiteService";
const val notificationName="WhiteService";

@AndroidEntryPoint
class WhiteService : Service() , IBinder.DeathRecipient{
    private val TAG = WhiteService::class.java.simpleName
    private var notificationManager: NotificationManager? = null
    private val FOREGROUND_ID = 1000

    @Inject
    lateinit var wolService: WOLService

//    val wol= WOL("54BF647ED56B","192.168.2.255",9);

//    var executors= Executors.newSingleThreadExecutor();

    companion object{
        @Volatile
        var serviceRunning=false;
    }

    override fun onCreate() {
        WhiteService.serviceRunning=true
        Log.i(TAG, "WhiteService->onCreate")
        super.onCreate()
        notificationManager= getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        //创建NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var channel = NotificationChannel(notificationId, notificationName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager?.createNotificationChannel(channel);
        }
//        executors.submit{
//            while (true){
//                Thread.sleep(3000);
//                println("executors")
//                try {
//                    var r=wolService.getAction();
//                    if(r.data as Boolean){
//                        println("send magic flag is "+r.data)
//                        wol.wakeUp()
//                    }else{
//                        println("else send magic flag is "+r.data)
//                    }
//                }catch (e:Exception){
//                    e.printStackTrace()
//                }
//            }
//        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "WhiteService->onStartCommand")
        val builder = NotificationCompat.Builder(this, notificationId)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(notificationId);
        }
//        builder.setAutoCancel(true)
//        builder.setVisibility(NotificationCompat.VISIBILITY_SECRET)
//        builder.setPriority(NotificationCompat.PRIORITY_MIN)
        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setContentTitle("Foreground")
        builder.setContentText("I am a foreground service")
        builder.setContentInfo("Content Info")
        builder.setWhen(System.currentTimeMillis())
        val activityIntent = Intent(this, GardenActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 1, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pendingIntent)
        val notification: Notification = builder.build()
        startForeground(FOREGROUND_ID, notification)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        // TODO: Return the communication channel to the service.
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onDestroy() {
        Log.i(TAG, "WhiteService->onDestroy")
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.i(TAG, "WhiteService->onTaskRemoved")
        super.onTaskRemoved(rootIntent)
    }

    override fun onTrimMemory(level: Int) {
        Log.i(TAG, "WhiteService->onTrimMemory")
        super.onTrimMemory(level)
    }

    override fun onLowMemory() {
        Log.i(TAG, "WhiteService->onLowMemory")
        super.onLowMemory()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        Log.i(TAG, "WhiteService->onConfigurationChanged")
        super.onConfigurationChanged(newConfig)
    }

    override fun binderDied() {
        Log.i(TAG, "WhiteService->binderDied")
    }
}