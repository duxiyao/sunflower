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

package com.dxy.samples.apps.sunflower

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import com.dxy.samples.apps.sunflower.databinding.ActivityGardenBinding
import com.dxy.samples.apps.sunflower.services.WhiteService
import com.dxy.samples.apps.sunflower.utilities.NotificationUtils
import com.xdandroid.hellodaemon.IntentWrapper
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class GardenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView<ActivityGardenBinding>(this, R.layout.activity_garden)
        val whiteIntent = Intent(applicationContext, WhiteService::class.java)
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            startForegroundService(whiteIntent)
        }else {
            startService(whiteIntent)
        }
        IntentWrapper.whiteListMatters(this, "轨迹跟踪服务的持续运行")

        if(!NotificationUtils.checkNotifySetting(this)){
            NotificationUtils.jumpSettings(this)
        }
    }


    //防止华为机型未加入白名单时按返回键回到桌面再锁屏后几秒钟进程被杀
    override fun onBackPressed() {
        IntentWrapper.onBackPressed(this)
    }
}
