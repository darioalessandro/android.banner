/*
 * Copyright (C) 2015 Dario A Lencina Talarico
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dario.banner.util;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;

import java.util.Iterator;
import java.util.List;

public class App extends Application {

    private static Context context;

    public void onCreate(){
        super.onCreate();
        App.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return App.context;
    }

    public static void runOnUiThread(Runnable runnable){
        Handler mainHandler = new Handler(App.getAppContext().getMainLooper());
        mainHandler.post(runnable);
    }

    public static boolean isRunningInTheForeground(){
        String foregroundAppPackage= null;
        try {
            foregroundAppPackage= App.getForegroundApp();
        }catch (Exception e){
            return false;
        }
        //FIXME: Get package name in a dynamic way.
        return foregroundAppPackage.equalsIgnoreCase("com.gm.gcc.rpmandspeed");
    }

    public static String getForegroundApp() throws PackageManager.NameNotFoundException {
        ActivityManager.RunningTaskInfo info = null;
        ActivityManager am;
        am = (ActivityManager)getAppContext().getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> l = am.getRunningTasks(1000);
        Iterator<ActivityManager.RunningTaskInfo> i = l.iterator();

        String packName = new String();
        ApplicationInfo appInfo = new ApplicationInfo();

        while(i.hasNext()){
            info = i.next();
            packName = info.topActivity.getPackageName();
            break;
        }
        return packName;
    }

}