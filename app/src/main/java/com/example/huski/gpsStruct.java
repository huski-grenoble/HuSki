package com.example.huski;

public class gpsStruct {
    private double lon;
    private double lat;

    public gpsStruct(double lon, double lat){
        this.lon = lon;
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
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
