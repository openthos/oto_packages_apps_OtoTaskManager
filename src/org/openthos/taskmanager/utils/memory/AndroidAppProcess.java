package org.openthos.taskmanager.utils.memory;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.IOException;

public class AndroidAppProcess extends AndroidProcess {
    public boolean foreground;
    public int uid;
    private final Cgroup cgroup;
    public static final Parcelable.Creator<AndroidAppProcess> CREATOR = new Parcelable.Creator<AndroidAppProcess>() {
        public AndroidAppProcess createFromParcel(Parcel source) {
            return new AndroidAppProcess(source);
        }

        public AndroidAppProcess[] newArray(int size) {
            return new AndroidAppProcess[size];
        }
    };

    public AndroidAppProcess(int pid) throws IOException, AndroidAppProcess.NotAndroidAppProcessException {
        super(pid);
        this.cgroup = super.cgroup();
        ControlGroup cpuacct = this.cgroup.getGroup("cpuacct");
        ControlGroup cpu = this.cgroup.getGroup("cpu");
        if (cpu != null && cpuacct != null && cpuacct.group.contains("pid_")) {
            this.foreground = !cpu.group.contains("bg_non_interactive");

            try {
                this.uid = Integer.parseInt(cpuacct.group.split("/")[1].replace("uid_", ""));
            } catch (Exception var5) {
                this.uid = this.status().getUid();
            }

        } else {
            throw new AndroidAppProcess.NotAndroidAppProcessException(pid);
        }
    }

    public String getPackageName() {
        return this.name.split(":")[0];
    }

    public PackageInfo getPackageInfo(Context context, int flags) throws NameNotFoundException {
        return context.getPackageManager().getPackageInfo(this.getPackageName(), flags);
    }

    public Cgroup cgroup() {
        return this.cgroup;
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.cgroup, flags);
        dest.writeByte((byte)(this.foreground ? 1 : 0));
    }

    protected AndroidAppProcess(Parcel in) {
        super(in);
        this.cgroup = (Cgroup)in.readParcelable(Cgroup.class.getClassLoader());
        this.foreground = in.readByte() != 0;
    }

    public static final class NotAndroidAppProcessException extends Exception {
        public NotAndroidAppProcessException(int pid) {
            super(String.format("The process %d does not belong to any application", pid));
        }
    }
}

