package com.example.uts_papb2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
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
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class NavigationActivity extends AppCompatActivity {

//  // distance
//    TextView distanceTV;
//    private String distanceData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        // Mendapatkan data lokasi tujuan dari DetailMarkerActivity
        Intent intent = getIntent();
        String destinationLatitude = intent.getStringExtra("destinationLatitude").toString();
        String destinationLongitude = intent.getStringExtra("destinationLongitude").toString();

        // Mengirimkan data lokasi tujuan ke NavigationMapsFragment
        Bundle bundle = new Bundle();
        bundle.putString("latitude", destinationLatitude);
        bundle.putString("longitude", destinationLongitude);

        NavigationMapsFragment navigationMapsFragment = new NavigationMapsFragment();
        navigationMapsFragment.setArguments(bundle);

        // Menampilkan map NavigationMapsFragment
        replaceFragment(navigationMapsFragment);

//      // Get distance data
//        Bundle bundle1 = getIntent().getExtras();
//        String distance = bundle1.getString("distance");
//
//        distanceTV = findViewById(R.id.distanceData);
//        distanceTV.setText(distanceData);
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.navigation_map_container, fragment)
                .commit();
    }
}