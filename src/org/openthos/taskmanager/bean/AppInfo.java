package org.openthos.taskmanager.bean;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import org.openthos.taskmanager.app.Constants;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class AppInfo {
    private ActivityManager mManager;
    private String appName;
    private String packageName;
    private List<String> processNames;
    private Drawable icon;
    private List<Integer> pids;
    private double cpuUsage;
    private double memoryUsage;
    private String batteryUsage;
    private boolean isRun;
    private boolean isDormant;
    private boolean isNonDormant;
    private boolean isAutoPrevent;

    public AppInfo() {
        pids = new ArrayList<>();
        processNames = new ArrayList<>();
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public List<String> getProcessNames() {
        return processNames;
    }

    public void addProcessName(String processName) {
        if (!processNames.contains(processName)) {
            processNames.add(processName);
        }
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public List<Integer> getPids() {
        return pids;
    }

    public void addPid(int pid) {
        if (!pids.contains(pid)) {
            pids.add(pid);
        }
    }

    public String getCpuUsage() {
        return cpuUsage + "%";
    }

    public void addCpuUsage(double cpuUsage) {
        this.cpuUsage += cpuUsage;
    }

    public double getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(double memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public String getBatteryUsage() {
        if (batteryUsage == null) {
            return "";
        }
        return batteryUsage;
    }

    public void setBatteryUsage(String batteryUsage) {
        this.batteryUsage = batteryUsage;
    }

    public boolean isRun() {
        return isRun;
    }

    public void setRun(boolean run) {
        isRun = run;
        if (!run) {
            setStop();
        }
    }

    public boolean isDormant() {
        return isDormant;
    }

    public void setDormant(boolean dormant) {
        isDormant = dormant;
    }

    public boolean isNonDormant() {
        return isNonDormant;
    }

    public void setNonDormant(boolean nonDormant) {
        isNonDormant = nonDormant;
    }

    public int getDormantState() {
        if (isNonDormant()) {
            return Constants.APP_NON_DORMANT;
        }
        return Constants.App_NON_DEAL;
    }

    public void setStop() {
        processNames.clear();
        pids.clear();
        cpuUsage = 0;
    }

    public boolean isAutoPrevent() {
        return isAutoPrevent;
    }

    public void setAutoPrevent(boolean autoPrevent) {
        isAutoPrevent = autoPrevent;
    }

    public Intent getIntent(Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }
}
