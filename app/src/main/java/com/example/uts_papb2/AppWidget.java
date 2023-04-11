package com.example.uts_papb2;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

/**
 * Implementation of App Widget functionality.
 */
public class AppWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
            int appWidgetId) {

        // Menampilkan data widget
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Marker.class.getSimpleName());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Marker dataMarker = new Marker();
                if (snapshot.hasChildren()) {
                    for (DataSnapshot currentData : snapshot.getChildren()) {
                        dataMarker = currentData.getValue(Marker.class);
                    }
                }

                // Construct the RemoteViews object
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);

                // Menampilkan data marker
                views.setTextViewText(R.id.marker_name_widget, dataMarker.getMarkerName());
                views.setTextViewText(R.id.latitude_widget, String.valueOf(dataMarker.getLatitude()));
                views.setTextViewText(R.id.longitude_widget, String.valueOf(dataMarker.getLongitude()));

                // Menampilkan last updated
                String dateString = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    dateString = DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date());
                }
                views.setTextViewText(R.id.last_updated_widget, dateString);

                // Button update
                Intent intentUpdate = new Intent(context, AppWidget.class);
                intentUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                int[] idArray = new int[]{appWidgetId};
                intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intentUpdate,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
                views.setOnClickPendingIntent(R.id.button_update, pendingIntent);

                // Instruct the widget manager to update the widget
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}