package com.example.huski.dataStructure;

public class gpsStruct {
    private double lon;
    private double lat;
    private double alt;

    public gpsStruct(double lon, double lat, double alt){
        this.lon = lon;
        this.lat = lat;
        this.alt = alt;
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

    public double getAlt() {
        return alt;
    }

    public void setAlt(double alt) {
        this.alt = alt;
    }

    public float getAngle(gpsStruct target){
        double lat2 = target.getLat();
        double lon2 = target.getLon();
        double dy = this.lat - lat2;
        double dx = Math.cos(Math.PI / 180 * lat2) * (lon2 - this.lon);
        double angle = Math.atan2(dy, dx);
        return (float) angle;
    }

    public double distance(gpsStruct p2) {
        double lat2 = p2.getLat();
        double lon2 = p2.getLon();
        double el2 = p2.getAlt();
        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - this.lat);
        double lonDistance = Math.toRadians(lon2 - this.lon);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(this.lat)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = this.alt - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }


}
