package com.example.uts_papb2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MarkerAdapter extends RecyclerView.Adapter<MarkerAdapter.ViewHolder> {

    private final ArrayList<Marker> values;
    private final LayoutInflater inflater;

    DatabaseReference databaseReference;

    public MarkerAdapter(Context context, ArrayList<Marker> values) {
        this.values = values;
        this.inflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public MarkerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_marker, parent, false);
        return new MarkerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MarkerAdapter.ViewHolder holder, int position) {
        int pos = position;
        Marker marker = values.get(position);
        holder.txtNumber.setText(String.valueOf(position+1));
        holder.txtMarkerName.setText(marker.getMarkerName());
        holder.txtLatitude.setText(marker.getLatitude());
        holder.txtLongitude.setText(marker.getLongitude());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String key = ListFragment.keys.get(pos);

                databaseReference = FirebaseDatabase.getInstance().getReference(Marker.class.getSimpleName());

                databaseReference.child(key).removeValue();

                Toast.makeText(view.getContext(), "The marker has been successfully removed!", Toast.LENGTH_LONG).show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNumber, txtMarkerName, txtLatitude, txtLongitude;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNumber = itemView.findViewById(R.id.number);
            txtMarkerName = itemView.findViewById(R.id.markerNameData);
            txtLatitude = itemView.findViewById(R.id.latitudeData);
            txtLongitude = itemView.findViewById(R.id.longitudeData);
        }
    }
}
