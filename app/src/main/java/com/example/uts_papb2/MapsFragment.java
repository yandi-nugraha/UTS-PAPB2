package com.example.uts_papb2;

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
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapsFragment extends Fragment {

    DatabaseReference databaseReference;

    private ArrayList<Marker> markerList = new ArrayList<>();

    private GoogleMap mMap;

    private com.google.android.gms.maps.model.Marker lastClickedMarker = null;

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
            mMap = googleMap;

            // Get all marker data from Firebase
            databaseReference = FirebaseDatabase.getInstance().getReference(Marker.class.getSimpleName());

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChildren()) {
                        for (DataSnapshot currentData : snapshot.getChildren()) {
                            Marker marker = new Marker();
                            marker = currentData.getValue(Marker.class);
                            markerList.add(marker);
                        }
                    }

                    // Menambahkan marker pada map
                    for (Marker currentMarker : markerList) {
                        String markerName = currentMarker.getMarkerName();
                        Float latitude = Float.parseFloat(currentMarker.getLatitude());
                        Float longitude = Float.parseFloat(currentMarker.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                                .title(markerName).icon(BitmapFromVector(getContext(), R.drawable.chest)));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            LatLng jogja = new LatLng(-7.782876, 110.367077);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(jogja, 10));

            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);

            mMap.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
                @Override
                public void onPoiClick(@NonNull PointOfInterest pointOfInterest) {
                    com.google.android.gms.maps.model.Marker poiMarker = mMap.addMarker(new MarkerOptions()
                            .position(pointOfInterest.latLng).title(pointOfInterest.name));
                    poiMarker.showInfoWindow();
                }
            });

            // My location button
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);

            setOnMarkerClick(mMap);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private void setOnMarkerClick(final GoogleMap map) {
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull com.google.android.gms.maps.model.Marker marker) {
                if (lastClickedMarker != null) {
                    if (lastClickedMarker.equals(marker)) {
                        Intent intent = new Intent(getActivity(), DetailMarkerActivity.class);
                        intent.putExtra("markerName", marker.getTitle());
                        intent.putExtra("latitude", String.valueOf(marker.getPosition().latitude));
                        intent.putExtra("longitude", String.valueOf(marker.getPosition().longitude));
                        startActivity(intent);
                    } else {
                        lastClickedMarker.setIcon(BitmapFromVector(getContext(), R.drawable.chest));
                        lastClickedMarker = marker;
                        marker.setIcon(BitmapFromVector(getContext(), R.drawable.chest_marker));
                    }
                } else {
                    lastClickedMarker = marker;
                    marker.setIcon(BitmapFromVector(getContext(), R.drawable.chest_marker));
                }
                return false;
            }
        });
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