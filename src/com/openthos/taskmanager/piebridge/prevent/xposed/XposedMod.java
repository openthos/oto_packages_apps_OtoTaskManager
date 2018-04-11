package com.openthos.taskmanager.piebridge.prevent.xposed;

import android.app.ActivityThread;
import android.util.Log;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XposedBridge;

public class XposedMod implements IXposedHookZygoteInit {

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) throws Throwable {
        Log.i("initZygote:::", "Smaster");
        XposedBridge.hookAllMethods(ActivityThread.class, "systemMain", new SystemServiceHook());
    }
}
