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

import android.app.Application
import com.dxy.samples.apps.sunflower.services.TraceServiceImpl
import com.xdandroid.hellodaemon.DaemonEnv
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application(){
    override fun onCreate() {
        super.onCreate()

        //需要在 Application 的 onCreate() 中调用一次 DaemonEnv.initialize()
        //需要在 Application 的 onCreate() 中调用一次 DaemonEnv.initialize()
        DaemonEnv.initialize(this, TraceServiceImpl::class.java, DaemonEnv.DEFAULT_WAKE_UP_INTERVAL)
        TraceServiceImpl.sShouldStopService = false
        DaemonEnv.startServiceMayBind(TraceServiceImpl::class.java)
    }
}
