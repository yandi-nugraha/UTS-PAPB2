package com.example.uts_papb2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddMarkerActivity extends AppCompatActivity {

    EditText markerNameET, latitudeET, longitudeET;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marker);

        databaseReference = FirebaseDatabase.getInstance().getReference(Marker.class.getSimpleName());

        markerNameET = findViewById(R.id.markerName);
        latitudeET = findViewById(R.id.latitude);
        longitudeET = findViewById(R.id.longitude);

        findViewById(R.id.add_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addData();
                toMainActivity();
                Toast.makeText(getApplicationContext(), "Successfully added marker!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addData() {
        Marker marker = new Marker();
        String markerName = markerNameET.getText().toString();
        String latitude = latitudeET.getText().toString();
        String longitude = longitudeET.getText().toString();

        if (markerName!="" && latitude!="" && longitude!="") {
            marker.setMarkerName(markerName);
            marker.setLatitude(latitude);
            marker.setLongitude(longitude);

            databaseReference.push().setValue(marker);
        }
    }

    private void toMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}