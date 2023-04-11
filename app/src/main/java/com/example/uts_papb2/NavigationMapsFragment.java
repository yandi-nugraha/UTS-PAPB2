package com.example.uts_papb2;

import static android.content.Context.SENSOR_SERVICE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.concurrent.Executor;

public class NavigationMapsFragment extends Fragment implements SensorEventListener {

    private double destinationLatitude, destinationLongitude;
    private double currentLatitude, currentLongitude;

    private FusedLocationProviderClient fusedLocationProviderClient;

    // Rotasi Map
    private SensorManager sensorManager;
    private Sensor magnetometer, accelerometer;
    private float currentDegree = 0f;
    private float[] lastAccelerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private float[] rotationMatrix = new float[9];
    private float[] orientation = new float[3];
    boolean isLastAccelerometerCopied = false;
    boolean isLastMagnetometerCopied = false;
    long lastUpdatedTime = 0;

    private GoogleMap mMap3;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap3 = googleMap;

            LatLng destination = new LatLng(destinationLatitude, destinationLongitude);
            mMap3.addMarker(new MarkerOptions().position(destination).title("Destination")
                    .icon(BitmapFromVector(getContext(), R.drawable.chest_marker)));
            mMap3.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 12));

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap3.setMyLocationEnabled(true);

            mMap3.getUiSettings().setCompassEnabled(true);


            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                }, 1);
            } else {
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), location -> {
                    if (location != null) {
                        currentLatitude = location.getLatitude();
                        currentLongitude = location.getLongitude();

                        LatLng currentLocation = new LatLng(currentLatitude, currentLongitude);
                        mMap3.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 11));

                        PolylineOptions polylineOptions = new PolylineOptions();
                        polylineOptions.add(currentLocation, destination);
                        polylineOptions.color(Color.BLUE);
                        polylineOptions.width(12);

                        mMap3.addPolyline(polylineOptions);

                        // Distance
                        Double marker1Lat = currentLatitude;
                        Double marker1Lng = currentLongitude;
                        Double marker2Lat = destinationLatitude;
                        Double marker2Lng = destinationLongitude;

                        float[] distance = new float[1];
                        Location.distanceBetween(marker1Lat, marker1Lng, marker2Lat, marker2Lng, distance);

                        Double distanceInKilometers = distance[0] / 100.0;

                        getActivity().findViewById(R.id.center_btn).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mMap3.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 11));
                            }
                        });

                        // Send distance data
//                        dataPasser.onDataPass(String.valueOf(distanceInKilometers));

//                        Bundle bundle = new Bundle();
//                        bundle.putString("distance", String.valueOf(distanceInKilometers));

//                        Intent intent = new Intent(getActivity(), NavigationActivity.class);
//                        intent.putExtras(bundle);
//                        startActivity(intent);
                    }
                });
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        destinationLatitude = Double.parseDouble(getArguments().getString("latitude"));
        destinationLongitude = Double.parseDouble(getArguments().getString("longitude"));

        // coba
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        return inflater.inflate(R.layout.fragment_navigation_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map3);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor == accelerometer) {
            System.arraycopy(sensorEvent.values, 0, lastAccelerometer, 0, sensorEvent.values.length);
            isLastAccelerometerCopied = true;
        } else if (sensorEvent.sensor == magnetometer) {
            System.arraycopy(sensorEvent.values, 0, lastMagnetometer, 0, sensorEvent.values.length);
            isLastMagnetometerCopied = true;
        }

        if (isLastAccelerometerCopied && isLastMagnetometerCopied && System.currentTimeMillis() - lastUpdatedTime > 250) {
            SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer);
            SensorManager.getOrientation(rotationMatrix, orientation);

            float azimuthInRadians = orientation[0];
            float azimuthInDegree = (float) Math.toDegrees(azimuthInRadians);

            CameraPosition oldPos = mMap3.getCameraPosition();
            CameraPosition pos = CameraPosition.builder(oldPos).bearing(azimuthInDegree).build();
            mMap3.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, magnetometer);
        sensorManager.unregisterListener(this, accelerometer);
    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}