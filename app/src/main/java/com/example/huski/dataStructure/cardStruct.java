package com.example.huski.dataStructure;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

public class cardStruct implements Parcelable {
    private String name;
    private UUID uuid;

    public cardStruct(String name){
        this.name = name;
        this.uuid = UUID.randomUUID();
    }

    private cardStruct(Parcel in) {
        this.name = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.name);
    }

    public static final Parcelable.Creator<cardStruct> CREATOR = new Parcelable.Creator<cardStruct>() {
        public cardStruct createFromParcel(Parcel in) {
            return new cardStruct(in);
        }
        public cardStruct[] newArray(int size) {
            return new cardStruct[size];
        }
    };
}
