package com.example.huski;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
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

import com.example.huski.dataStructure.gpsStruct;

import java.util.Arrays;

import static android.content.Context.SENSOR_SERVICE;


public class FindFragment extends Fragment implements SensorEventListener, LocationListener {
    // define the display assembly compass picture
    private ImageView imageArrow;
    private ImageView imageIntensity;

    // record the compass picture angle turned
    private float currentDegree = 0f;

    // device sensor manager
    private SensorManager mSensorManager;

    // Manager for gps localisation
    LocationManager locationManager;
    double currentLon, currentLat, currentAlt;
    TextView tvLon, tvLat;

    TextView tvHeading;
    private FragmentActivity mFrgAct;
    private Intent mIntent;

    public static FindFragment newInstance() {
        return (new FindFragment());
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

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
        //
        imageArrow = (ImageView) view.findViewById(R.id.imageViewCompass);
        imageIntensity = (ImageView) view.findViewById(R.id.SignalIntensity);
        tvLat = (TextView) view.findViewById(R.id.tvLat);
        tvLon = (TextView) view.findViewById(R.id.tvLon);

        // TextView that will tell the user what degree is he heading
        tvHeading = (TextView) view.findViewById(R.id.tvHeading);

    }

    @Override
    public void onResume() {
        super.onResume();
        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (getActivity() != null && getView() != null) {
            // get the angle around the z-axis rotated
            //float degree = Math.round(event.values[0]);
            gpsStruct p1 = new gpsStruct(currentLon, currentLat, currentAlt);
            gpsStruct p2 = new gpsStruct(currentLon + 10, currentLat, currentAlt); //0 90 north pole
            double distance = p1.distance(p2);
            Toast.makeText(getContext(), Float.toString(p1.getAngle(p2)), Toast.LENGTH_SHORT).show();
            float degree = (p1.getAngle(p2) + Math.round(event.values[0]) + 180) % 360; //TODO revoir le calcul
            tvHeading.setText("Heading: " + Float.toString(degree) + " degrees");

            int lvlToDraw = (int) degree % 7;
            String lvl = "lvl" + lvlToDraw;
            imageIntensity.setImageResource(getResources().getIdentifier(lvl, "drawable", "com.example.huski"));

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
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        checkLocationPermission();
        Location myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        currentLat = myLocation.getLatitude();
        currentLon = myLocation.getLongitude();
        currentAlt = myLocation.getAltitude();
        tvLat.setText("Lat: " + myLocation.getLatitude());
        tvLon.setText("Lon: " + myLocation.getLongitude());
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);
        mFrgAct = getActivity();
        mIntent = mFrgAct.getIntent();
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLat = location.getLatitude();
        currentLon = location.getLongitude();
        tvLat.setText("Latitude:" + location.getLatitude());
        tvLon.setText("Longitude: " + location.getLongitude());
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
}
