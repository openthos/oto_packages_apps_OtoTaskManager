package com.openthos.greenify;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.openthos.greenify.entity.AppInfo;
import com.openthos.greenify.utils.SPUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseActivity extends Activity {
    public static final int FROM_LOOK_APP_ACTIVITY = 100;
    public static final int FROM_MAIN_ACTIVITY = 101;
    private static Map<String, AppInfo> mNotSystemApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initView();
        initData();
        initListener();
    }

    /**
     * A set of non - system applications required to initialize an entire application
     */
    public void initAppInfos() {
        if (mNotSystemApps == null) {
            mNotSystemApps = new HashMap<>();
        }
        PackageManager manager = getPackageManager();
        List<PackageInfo> packageInfos = manager.getInstalledPackages(0);
        Map<String, String> allAddedApp = SPUtils.getInstance(this).getAllAddedApp();
        AppInfo appInfo = null;
        for (PackageInfo packageInfo : packageInfos) {
            if (getAppInfoByPkgName(packageInfo.packageName) != null || isSystemApps(packageInfo)) {
                continue;
            }
            appInfo = new AppInfo();
            appInfo.setAppName(packageInfo.applicationInfo.loadLabel(manager).toString());
            appInfo.setPackageName(packageInfo.packageName);
            appInfo.setIcon(packageInfo.applicationInfo.loadIcon(manager));
            if (allAddedApp != null && allAddedApp.containsKey(appInfo.getPackageName())) {
                appInfo.setAdd(true);
            } else {
                appInfo.setAdd(false);
            }
            //TODO Determine whether the application is integrated with a Google official
            // message push mechanism  appInfo.setPush()
            mNotSystemApps.put(appInfo.getPackageName(), appInfo);
        }
    }

    /**
     * Obtaining a list of application data for the entire application
     *
     * @return
     */
    public Map<String, AppInfo> getNotSystemApps() {
        initRunningAPP();
        return mNotSystemApps;
    }

    public void initRunningAPP() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(100);
        for (String packageName : mNotSystemApps.keySet()) {
            AppInfo appInfo = mNotSystemApps.get(packageName);
            appInfo.setRun(false);
            for (ActivityManager.RunningServiceInfo info : runningServices) {
                if (packageName.equals(info.service.getPackageName())) {
                    appInfo.setRun(true);
                    Log.i("ljh", "-----------" + packageName);
                }
            }
            appInfo.setSleep(appInfo.isAdd() && !appInfo.isRun());
        }
    }

    /**
     * Obtaining the application of the specified key from the data list
     *
     * @param packageName
     * @return
     */
    public AppInfo getAppInfoByPkgName(String packageName) {
        return mNotSystemApps.get(packageName);
    }

    /**
     * Whether it is a system application
     *
     * @param packageInfo
     * @return
     */
    private boolean isSystemApps(PackageInfo packageInfo) {
        return !((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0);
    }

    /**
     * Store the package name as packageName or remove the saved SP
     *
     * @param packageName
     * @param isAdd
     */
    public void addSleepList(String packageName, boolean isAdd) {
        AppInfo appInfo = mNotSystemApps.get(packageName);
        if (appInfo != null) {
            if (isAdd) {
                SPUtils.getInstance(this).saveAddedApp(packageName, appInfo.getAppName());
                appInfo.setAdd(true);
            } else {
                SPUtils.getInstance(this).removeAddApp(packageName);
                appInfo.setAdd(false);
            }
        }
    }

    /**
     * Get the loaded layout file
     *
     * @return
     */
    public abstract int getLayoutId();

    public abstract void initView();

    public abstract void initData();

    public abstract void initListener();
}
