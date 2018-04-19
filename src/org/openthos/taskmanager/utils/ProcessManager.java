package org.openthos.taskmanager.utils;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build.VERSION;
import android.os.Process;


import org.openthos.taskmanager.utils.memory.AndroidAppProcess;
import org.openthos.taskmanager.utils.memory.AndroidProcess;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class ProcessManager {
    private ProcessManager() {
        throw new AssertionError("no instances");
    }

    public static List<AndroidProcess> getRunningProcesses() {
        List<AndroidProcess> processes = new ArrayList();
        File[] files = (new File("/proc")).listFiles();
        File[] var5 = files;
        int var4 = files.length;

        for(int var3 = 0; var3 < var4; ++var3) {
            File file = var5[var3];
            if (file.isDirectory()) {
                int pid;
                try {
                    pid = Integer.parseInt(file.getName());
                } catch (NumberFormatException var9) {
                    continue;
                }

                try {
                    processes.add(new AndroidProcess(pid));
                } catch (IOException var8) {
                    ;
                }
            }
        }

        return processes;
    }

    public static List<AndroidAppProcess> getRunningAppProcesses() {
        List<AndroidAppProcess> processes = new ArrayList();
        File[] files = (new File("/proc")).listFiles();
        File[] var5 = files;
        int var4 = files.length;

        for(int var3 = 0; var3 < var4; ++var3) {
            File file = var5[var3];
            if (file.isDirectory()) {
                int pid;
                try {
                    pid = Integer.parseInt(file.getName());
                } catch (NumberFormatException var10) {
                    continue;
                }

                try {
                    processes.add(new AndroidAppProcess(pid));
                } catch (AndroidAppProcess.NotAndroidAppProcessException var8) {
                    ;
                } catch (IOException var9) {
                    ;
                }
            }
        }

        return processes;
    }

    public static List<AndroidAppProcess> getRunningForegroundApps(Context ctx) {
        List<AndroidAppProcess> processes = new ArrayList();
        File[] files = (new File("/proc")).listFiles();
        PackageManager pm = ctx.getPackageManager();
        File[] var7 = files;
        int var6 = files.length;

        for(int var5 = 0; var5 < var6; ++var5) {
            File file = var7[var5];
            if (file.isDirectory()) {
                int pid;
                try {
                    pid = Integer.parseInt(file.getName());
                } catch (NumberFormatException var12) {
                    continue;
                }

                try {
                    AndroidAppProcess process = new AndroidAppProcess(pid);
                    if (process.foreground && (process.uid < 1000 || process.uid > 9999) && !process.name.contains(":") && pm.getLaunchIntentForPackage(process.getPackageName()) != null) {
                        processes.add(process);
                    }
                } catch (AndroidAppProcess.NotAndroidAppProcessException var10) {
                    ;
                } catch (IOException var11) {
                    ;
                }
            }
        }

        return processes;
    }

    public static boolean isMyProcessInTheForeground() {
        List<AndroidAppProcess> processes = getRunningAppProcesses();
        int myPid = Process.myPid();
        Iterator var3 = processes.iterator();

        AndroidAppProcess process;
        do {
            if (!var3.hasNext()) {
                return false;
            }

            process = (AndroidAppProcess)var3.next();
        } while(process.pid != myPid || !process.foreground);

        return true;
    }

    public static List<RunningAppProcessInfo> getRunningAppProcessInfo(Context ctx) {
        if (VERSION.SDK_INT < 21) {
            @SuppressLint("WrongConstant") ActivityManager am = (ActivityManager)ctx.getSystemService("activity");
            return am.getRunningAppProcesses();
        } else {
            List<AndroidAppProcess> runningAppProcesses = getRunningAppProcesses();
            List<RunningAppProcessInfo> appProcessInfos = new ArrayList();
            Iterator var4 = runningAppProcesses.iterator();

            while(var4.hasNext()) {
                AndroidAppProcess process = (AndroidAppProcess)var4.next();
                RunningAppProcessInfo info = new RunningAppProcessInfo(process.name, process.pid, (String[])null);
                info.uid = process.uid;
                appProcessInfos.add(info);
            }

            return appProcessInfos;
        }
    }

    public static final class ProcessComparator implements Comparator<AndroidProcess> {
        public ProcessComparator() {
        }

        public int compare(AndroidProcess lhs, AndroidProcess rhs) {
            try {
                int oomScoreAdj1 = lhs.oom_score_adj();
                int oomScoreAdj2 = rhs.oom_score_adj();
                if (oomScoreAdj1 < oomScoreAdj2) {
                    return -1;
                }

                if (oomScoreAdj1 > oomScoreAdj2) {
                    return 1;
                }
            } catch (IOException var5) {
                ;
            }

            return lhs.name.compareToIgnoreCase(rhs.name);
        }
    }
}

