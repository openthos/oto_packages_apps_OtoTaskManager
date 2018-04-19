package org.openthos.taskmanager.utils.memory;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//
import android.os.Parcel;
import android.os.Parcelable;

import java.io.IOException;

public final class Statm extends ProcFile {
    public final String[] fields;
    public static final Parcelable.Creator<Statm> CREATOR = new Parcelable.Creator<Statm>() {
        public Statm createFromParcel(Parcel source) {
            return new Statm(source, (Statm)null);
        }

        public Statm[] newArray(int size) {
            return new Statm[size];
        }
    };

    public static Statm get(int pid) throws IOException {
        return new Statm(String.format("/proc/%d/statm", pid));
    }

    private Statm(String path) throws IOException {
        super(path);
        this.fields = this.content.split("\\s+");
    }

    private Statm(Parcel in, Statm statm) {
        super(in);
        this.fields = in.createStringArray();
    }

    public long getSize() {
        return Long.parseLong(this.fields[0]) * 1024L;
    }

    public long getResidentSetSize() {
        return Long.parseLong(this.fields[1]) * 1024L;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeStringArray(this.fields);
    }
}

