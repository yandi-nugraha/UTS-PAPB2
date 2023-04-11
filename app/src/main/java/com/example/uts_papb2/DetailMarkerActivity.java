package com.example.uts_papb2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class DetailMarkerActivity extends AppCompatActivity {

    TextView markerNameTV, latitudeTV, longitudeTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_marker);

        // Mendapatkan data marker yang terpilih dari map utama
        Intent intent = getIntent();
        String markerName = intent.getStringExtra("markerName").toString();
        String latitude = intent.getStringExtra("latitude").toString();
        String longitude = intent.getStringExtra("longitude").toString();

        markerNameTV = findViewById(R.id.markerNameTV);
        latitudeTV = findViewById(R.id.latitudeTV);
        longitudeTV = findViewById(R.id.longitudeTV);

        markerNameTV.setText(markerName);
        latitudeTV.setText(latitude);
        longitudeTV.setText(longitude);

        // Mengirimkan data lokasi marker ke DetailMapsFragment
        Bundle bundle = new Bundle();
        bundle.putString("latitude", latitude);
        bundle.putString("longitude", longitude);

        DetailMapsFragment detailMapsFragment = new DetailMapsFragment();
        detailMapsFragment.setArguments(bundle);

        // Menampilkan peta detailMapsFragment
        replaceFragment(detailMapsFragment);

        // Button navigasi
        findViewById(R.id.navigate_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Mengirimkan data lokasi tujuan ke NavigationActivity
                Intent intent1 = new Intent(getApplicationContext(), NavigationActivity.class);
                intent1.putExtra("destinationLatitude", latitude);
                intent1.putExtra("destinationLongitude", longitude);
                startActivity(intent1);
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.detail_map_container, fragment)
                .commit();
    }
}