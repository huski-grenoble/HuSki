package com.example.huski;

public class gpsStruct {
    private float lon;
    private float lat;

    public gpsStruct(float lon, float lat){
        this.lon = lon;
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getAngle(gpsStruct target){
        double lat2 = target.getLat() / 1E6;
        double lon2 = target.getLon() / 1E6;
        double dy = this.lat - lat2;
        double dx = Math.cos(Math.PI / 180 * lat2) * (lon2 - this.lon);
        double angle = Math.atan2(dy, dx);
        return (float) angle;
    }
}
