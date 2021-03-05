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

package com.google.samples.apps.sunflower.services

import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.google.samples.apps.sunflower.api.WOLService
import com.google.samples.apps.sunflower.data.Response
import com.google.samples.apps.sunflower.utilities.WOL
import com.xdandroid.hellodaemon.AbsWorkService
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@AndroidEntryPoint
class TraceServiceImpl: AbsWorkService() {

    @Inject
    lateinit var wolService: WOLService

    val wol=WOL("54BF647ED56B","192.168.2.255",9);

    companion object{
        //是否 任务完成, 不再需要服务运行?
        var sShouldStopService = false
    }
    var sDisposable: Disposable? = null

    fun stopService() {
        //我们现在不再需要服务运行了, 将标志位置为 true
        sShouldStopService = true
        //取消对任务的订阅
        if (sDisposable != null) sDisposable!!.dispose()
        //取消 Job / Alarm / Subscription
        cancelJobAlarmSub()
    }

    /**
     * 是否 任务完成, 不再需要服务运行?
     * @return 应当停止服务, true; 应当启动服务, false; 无法判断, 什么也不做, null.
     */
    override fun shouldStopService(intent: Intent?, flags: Int, startId: Int): Boolean? {
        return sShouldStopService
    }

    override fun startWork(intent: Intent?, flags: Int, startId: Int) {
        println("检查磁盘中是否有上次销毁时保存的数据")

        sDisposable = Observable
                .interval(5, TimeUnit.SECONDS) //取消任务时取消定时唤醒
                .doOnDispose {
                    println("保存数据到磁盘。")
                    cancelJobAlarmSub()
                }
                .subscribe { count: Long ->
                    println("---每 5 秒采集一次数据... count = $count")
                    if(!WhiteService.serviceRunning){
                        println("---if(!WhiteService.serviceRunning)")
                        val whiteIntent = Intent(applicationContext, WhiteService::class.java)
                        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
                            startForegroundService(whiteIntent)
                        }else {
                            startService(whiteIntent)
                        }
                    }
                    getAction()
                    if (count > 0 && count % 18 == 0L) println("保存数据到磁盘。 saveCount = " + (count / 18 - 1))
                }
    }

    fun getAction(){
        GlobalScope.launch {
            try {
                var r=wolService.getAction();
                if(r.data as Boolean){
                    println("send magic flag is "+r.data)
                    wol.wakeUp()
                }else{
                    println("else send magic flag is "+r.data)
                }
            }catch (e:Exception){

            }
        }

    }

    override fun stopWork(intent: Intent?, flags: Int, startId: Int) {
        stopService()
    }

    /**
     * 任务是否正在运行?
     * @return 任务正在运行, true; 任务当前不在运行, false; 无法判断, 什么也不做, null.
     */
    override fun isWorkRunning(intent: Intent?, flags: Int, startId: Int): Boolean? {
        //若还没有取消订阅, 就说明任务仍在运行.
        return sDisposable != null && !sDisposable!!.isDisposed
    }

    override fun onBind(intent: Intent?, v: Void?): IBinder? {
        return null
    }

    override fun onServiceKilled(rootIntent: Intent?) {
        println("保存数据到磁盘。")
    }
}