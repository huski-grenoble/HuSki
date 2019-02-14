package com.example.huski.dataStructure;

import android.os.Parcel;
import android.os.Parcelable;

public class cardStruct implements Parcelable {
    private String name;
    private String chipId;
    private gpsStruct gps;
    private int RSSI;
    private int batteryLvl = 0;

    public cardStruct(String name){
        this.name = name;
        this.gps = new gpsStruct(0,0,0); //5.7445043 + 10,45.1825309 ,212);
    }

    public cardStruct(String name, String chipId,int rssi){
        this.name = name;
        this.chipId = chipId;
        this.gps = new gpsStruct(0,0,0); //5.7445043 + 10,45.1825309 ,212);
        this.RSSI = rssi;
    }


    private cardStruct(Parcel in) {
        this.name = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public int getRSSI(){
        return this.RSSI;
    }

    public void setRSSI(int rssi){
        this.RSSI = rssi;
    }

    public gpsStruct getGps() {
        return gps;
    }

    public void setGps(gpsStruct gps) {
        this.gps = gps;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getBatteryLvl() {
        return batteryLvl;
    }

    public void setBatteryLvl(int batteryLvl) {
        this.batteryLvl = batteryLvl;
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

    public String getChipId() {
        return chipId;
    }
}
