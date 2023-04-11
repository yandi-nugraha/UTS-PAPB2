package com.example.uts_papb2;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {

    DatabaseReference databaseReference;

    public RecyclerView recyclerView;
    private static ArrayList<Marker> markerList = new ArrayList<>();
    public static MarkerAdapter markerAdapter;

    public static ArrayList<String> keys = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        markerList.clear();
        keys.clear();

        // Get all marker data from Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference(Marker.class.getSimpleName());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot currentData : snapshot.getChildren()) {
                        String key = currentData.getKey();
                        keys.add(key);
                        Marker marker = new Marker();
                        marker = currentData.getValue(Marker.class);
                        markerList.add(marker);
                    }
                }

                // Menampilkan data marker ke dalam recyclerView
                markerAdapter = new MarkerAdapter(getContext(), markerList);
                recyclerView = getActivity().findViewById(R.id.rv_marker);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(markerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        setHasOptionsMenu(true);

        // Button untuk menambahkan data marker
        FloatingActionButton fab = rootView.findViewById(R.id.add_marker_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddMarkerActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }
}