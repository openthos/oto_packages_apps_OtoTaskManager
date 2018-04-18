package com.openthos.taskmanager.utils.memory;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.io.IOException;

public class AndroidProcess implements Parcelable {
    public final String name;
    public final int pid;
    public static final Creator<AndroidProcess> CREATOR = new Creator<AndroidProcess>() {
        public AndroidProcess createFromParcel(Parcel source) {
            return new AndroidProcess(source);
        }

        public AndroidProcess[] newArray(int size) {
            return new AndroidProcess[size];
        }
    };

    static String getProcessName(int pid) throws IOException {
        String cmdline = null;

        try {
            cmdline = ProcFile.readFile(String.format("/proc/%d/cmdline", pid)).trim();
        } catch (IOException var3) {
            ;
        }

        return !TextUtils.isEmpty(cmdline) && !"null".equals(cmdline) ? cmdline : Stat.get(pid).getComm();
    }

    public AndroidProcess(int pid) throws IOException {
        this.pid = pid;
        this.name = getProcessName(pid);
    }

    public String read(String filename) throws IOException {
        return ProcFile.readFile(String.format("/proc/%d/%s", this.pid, filename));
    }

    public String attr_current() throws IOException {
        return this.read("attr/current");
    }

    public String cmdline() throws IOException {
        return this.read("cmdline");
    }

    public Cgroup cgroup() throws IOException {
        return Cgroup.get(this.pid);
    }

    public int oom_adj() throws IOException {
        return Integer.parseInt(this.read("oom_adj"));
    }

    public int oom_score_adj() throws IOException {
        return Integer.parseInt(this.read("oom_score_adj"));
    }

    public Stat stat() throws IOException {
        return Stat.get(this.pid);
    }

    public Statm statm() throws IOException {
        return Statm.get(this.pid);
    }

    public Status status() throws IOException {
        return Status.get(this.pid);
    }

    public String wchan() throws IOException {
        return this.read("wchan");
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.pid);
    }

    protected AndroidProcess(Parcel in) {
        this.name = in.readString();
        this.pid = in.readInt();
    }
}

