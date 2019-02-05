package com.example.huski;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.content.Context.SENSOR_SERVICE;


public class FindFragment extends Fragment implements SensorEventListener {
    // define the display assembly compass picture
    private ImageView imageArrow;
    private ImageView imageIntensity;

    // record the compass picture angle turned
    private float currentDegree = 0f;

    // device sensor manager
    private SensorManager mSensorManager;

    TextView tvHeading;
    private TextView mTextView;
    private FragmentActivity mFrgAct;
    private Intent mIntent;
    private LinearLayout mLinearLayout;

    public static FindFragment newInstance() {
        return (new FindFragment());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        //
        imageArrow = (ImageView) view.findViewById(R.id.imageViewCompass);
        imageIntensity = (ImageView) view.findViewById(R.id.SignalIntensity);

        // TextView that will tell the user what degree is he heading
        tvHeading = (TextView) view.findViewById(R.id.tvHeading);

        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager) this.getActivity().getSystemService(SENSOR_SERVICE);
    }

    @Override
    public void onResume() {
        super.onResume();

        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(getActivity() != null) {
            // get the angle around the z-axis rotated
            //float degree = Math.round(event.values[0]);
            gpsStruct p1 = new gpsStruct(0, 0);
            gpsStruct p2 = new gpsStruct(1, 0);
            float degree = p1.getAngle(p2) + Math.round(event.values[0]);

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
            ra.setDuration(210);

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
        mFrgAct = getActivity();
        mIntent = mFrgAct.getIntent();
    }

}
