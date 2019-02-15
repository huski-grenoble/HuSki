package com.example.huski;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huski.dataStructure.cardStruct;
import com.example.huski.dataStructure.gpsStruct;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.SENSOR_SERVICE;


public class FindFragment extends Fragment implements SensorEventListener, LocationListener {
    // define the display assembly compass picture
    private ImageView imageArrow;
    private ImageView imageIntensity;
    TextView tvDist, tvCardName, tvCardUuid;

    // record the compass picture angle turned
    private float currentDegree = 0f;

    // device sensor manager
    private SensorManager mSensorManager;

    // Manager for gps localisation
    LocationManager locationManager;
    double currentLon, currentLat, currentAlt;
    int currentDistance;
    private boolean displayGif = false;
    private cardStruct currentCard;
    static final int MIN_RSSI = 50;
    static final int MAX_RSSI = 106;
    static final int DISTANCE_RSSI = 20;
    private FragmentActivity mFrgAct;
    private Intent mIntent;

    /*public static FindFragment newInstance() {
        return (new FindFragment(new cardStruct("test")));
    }*/

    @SuppressLint("ValidFragment")
    public FindFragment(cardStruct currentCard){
        this.currentCard = currentCard;
    }

    public FindFragment(){}

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    /**
     * checks if the location permissions are enabled
     * @return true if the permissions are enabled
     */
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * Requests the location permissions
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 400, 1, this);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        imageArrow = (ImageView) view.findViewById(R.id.imageViewCompass);
        imageIntensity = (ImageView) view.findViewById(R.id.SignalIntensity);
        // TextView that will tell the user what degree is he heading
        tvCardName = (TextView) view.findViewById(R.id.tvCardName);
        tvCardUuid = (TextView) view.findViewById(R.id.tvCardUuid);
        tvDist = (TextView) view.findViewById(R.id.tvDist);
        //Set text for which card is tracked
        tvCardName.setText(currentCard.getName());
        tvCardUuid.setText(currentCard.getChipId().toString());
    }

    @Override
    public void onResume() {
        super.onResume();
        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
    }

    /**
     * Is called when the device is moved, calculates the angle between the user, the ski and the lat axis.
     * Sets the animation for the compass and recalculates the distance between the 2 gps points
     *
     * @param event the event of the sensor
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (getActivity() != null && getView() != null) {
            // get the angle around the z-axis rotated
            gpsStruct p1 = new gpsStruct(currentLon, currentLat, currentAlt);
            gpsStruct p2 = currentCard.getGps();
            currentDistance = (int) p1.distance(p2);
            float degree = 360 + (p1.getAngle(p2) + Math.round(event.values[0])) % 360;
            int lvlToDraw;
            int D = MAX_RSSI - MIN_RSSI;
            //Defines the rssi lvl to draw on the ui
            if(currentCard.getRSSI() > MAX_RSSI){
                lvlToDraw = 0;
            }
            else if(D-D/7 < currentCard.getRSSI() && currentCard.getRSSI() <= MAX_RSSI){
                lvlToDraw = 1;
            }
            else if(D-2*D/7 < currentCard.getRSSI() && currentCard.getRSSI() <= D-D/7){
                lvlToDraw = 2;
            }
            else if(D-3*D/7 < currentCard.getRSSI() && currentCard.getRSSI() <= D-2*D/7){
                lvlToDraw = 2;
            }
            else if(D-4*D/7 < currentCard.getRSSI() && currentCard.getRSSI() <= D-3*D/7){
                lvlToDraw = 2;
            }
            else if(D-5*D/7 < currentCard.getRSSI() && currentCard.getRSSI() <= D-4*D/7){
                lvlToDraw = 2;
            }
            else if(D-6*D/7 < currentCard.getRSSI() && currentCard.getRSSI() <= D-5*D/7){
                lvlToDraw = 2;
            }
            else {
                lvlToDraw = 7;
            }
            String lvl = "lvl" + lvlToDraw;
            imageIntensity.setImageResource(getResources().getIdentifier(lvl, "drawable", "com.example.huski"));
            if(!displayGif) {
                tvDist.setText("Approximate distance: " + currentDistance + "m");
                // create a rotation animation (reverse turn degree degrees)
                RotateAnimation ra = new RotateAnimation(
                        currentDegree,
                        -degree,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f);

                // how long the animation will take place
                ra.setDuration(120);

                // set the animation after the end of the reservation status
                ra.setFillAfter(true);

                // Start the animation
                imageArrow.startAnimation(ra);
                currentDegree = -degree;
            }
            else{
                currentDegree = 0;
                RotateAnimation ra = new RotateAnimation(
                        currentDegree,
                        0,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f);

                // how long the animation will take place
                ra.setDuration(120);

                // set the animation after the end of the reservation status
                ra.setFillAfter(true);

                // Start the animation
                imageArrow.startAnimation(ra);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager) this.getActivity().getSystemService(SENSOR_SERVICE);
        locationManager = (LocationManager) this.getActivity().getSystemService(LOCATION_SERVICE);
        checkLocationPermission();
        Location myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        while(myLocation == null){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,this);
            myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        currentLat = myLocation.getLatitude();
        currentLon = myLocation.getLongitude();
        currentAlt = myLocation.getAltitude();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) this);

        mFrgAct = getActivity();
        mIntent = mFrgAct.getIntent();
    }

    /**
     * Is called when the device location changes, resets the user position and calculates the distance
     *
     * @param location the new location of the device
     */
    @Override
    public void onLocationChanged(Location location) {
        currentLat = location.getLatitude();
        currentLon = location.getLongitude();
        gpsStruct p1 = new gpsStruct(currentLon, currentLat, currentAlt);
        gpsStruct p2 = currentCard.getGps();
        currentDistance = (int) p1.distance(p2);
        if(currentDistance <= DISTANCE_RSSI & !displayGif){
            imageArrow.setImageResource(R.drawable.huskysearching);
            tvDist.setText("The ski is very close to you! Please refer to the following indicator to find it.");
            displayGif = true;
            ListFragment.sendFromList(currentCard.getChipId()+"1");
        }
        else if(currentDistance > DISTANCE_RSSI){
            imageArrow.setImageResource(R.drawable.arrowski);
            displayGif = false;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onPause(){
        super.onPause();
        if(ListFragment.isConnectedToGW)
            ListFragment.periph.envoyer(currentCard.getChipId() + "0");
    }


}
