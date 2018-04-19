package org.openthos.taskmanager.utils.memory;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import android.os.Parcel;
import android.os.Parcelable;

public class ControlGroup implements Parcelable {
    public final int id;
    public final String subsystems;
    public final String group;
    public static final Creator<ControlGroup> CREATOR = new Creator<ControlGroup>() {
        public ControlGroup createFromParcel(Parcel source) {
            return new ControlGroup(source);
        }

        public ControlGroup[] newArray(int size) {
            return new ControlGroup[size];
        }
    };

    protected ControlGroup(String line) throws NumberFormatException, IndexOutOfBoundsException {
        String[] fields = line.split(":");
        this.id = Integer.parseInt(fields[0]);
        this.subsystems = fields[1];
        this.group = fields[2];
    }

    protected ControlGroup(Parcel in) {
        this.id = in.readInt();
        this.subsystems = in.readString();
        this.group = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.subsystems);
        dest.writeString(this.group);
    }
}

