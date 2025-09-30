package bf.uv.eventconnect;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.Executors;

public class CarteActivity extends AppCompatActivity {

    private MapView map;
    private Marker userMarker;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_carte);

        // Récupérer events depuis Intent
        Serializable serializable = getIntent().getSerializableExtra("events");
        ArrayList<EventModel> events = new ArrayList<>();

        if (serializable instanceof ArrayList<?>) {
            for (Object obj : (ArrayList<?>) serializable) {
                if (obj instanceof EventModel) {
                    events.add((EventModel) obj);
                }
            }
        }

        map = findViewById(R.id.map);
        map.setMultiTouchControls(true);

        // Position par défaut (Ouagadougou)
        GeoPoint startPoint = new GeoPoint(12.3714, -1.5197);
        map.getController().setZoom(12.0);
        map.getController().setCenter(startPoint);

        // Charger les marqueurs événements
        Executors.newSingleThreadExecutor().execute(() -> {
            ArrayList<GeoPoint> points = new ArrayList<>();
            for (EventModel event : events) {
                try {
                    double lat = event.getLatitude();
                    double lon = event.getLongitude();
                    GeoPoint point = new GeoPoint(lat, lon);
                    points.add(point);

                    runOnUiThread(() -> {
                        Marker marker = new Marker(map);
                        marker.setPosition(point);
                        marker.setTitle(event.getTitre());
                        marker.setSnippet("Date : " + event.getDate() + "\nLieu : " + event.getLieu());
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        map.getOverlays().add(marker);
                        map.invalidate();
                    });

                } catch (Exception e) {
                    Log.e("MAP", "Erreur sur EventModel: " + e.getMessage());
                }
            }

            if (!points.isEmpty()) {
                runOnUiThread(() -> {
                    BoundingBox box = BoundingBox.fromGeoPoints(points);
                    map.zoomToBoundingBox(box, true, 100);
                });
            }
        });

        // Gestion insets Android
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Demande permission et démarre localisation
        demander_la_permission(new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }

    // Gestion des permissions
    private void demander_la_permission(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (!permissionsToRequest.isEmpty()) {
            requestPermissions(permissionsToRequest.toArray(new String[0]), REQUEST_PERMISSIONS_REQUEST_CODE);
        } else {
            // Permissions déjà accordées → démarrer localisation
            demarrerLocalisation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            boolean granted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                    break;
                }
            }
            if (granted) {
                demarrerLocalisation();
            } else {
                Log.e("PERMISSIONS", "Permissions localisation refusées");
            }
        }
    }

    // Démarrer la localisation GPS
    private void demarrerLocalisation() {
        GpsMyLocationProvider provider = new GpsMyLocationProvider(this);
        provider.startLocationProvider(new IMyLocationConsumer() {
            @Override
            public void onLocationChanged(Location location, IMyLocationProvider source) {
                if (location != null) {
                    GeoPoint userLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                    runOnUiThread(() -> mettreAJourMarqueurUtilisateur(userLocation));
                }
            }
        });
    }

    // Mettre à jour le marqueur utilisateur
    private void mettreAJourMarqueurUtilisateur(GeoPoint userLocation) {
        if (userMarker == null) {
            userMarker = new Marker(map);
            userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            userMarker.setTitle("Vous êtes ici");

            // Cercle rouge
            int size = 40;
            ShapeDrawable circle = new ShapeDrawable(new OvalShape());
            circle.setIntrinsicWidth(size);
            circle.setIntrinsicHeight(size);
            circle.getPaint().setColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));

            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            circle.setBounds(0, 0, size, size);
            circle.draw(canvas);

            userMarker.setIcon(new android.graphics.drawable.BitmapDrawable(getResources(), bitmap));
            map.getOverlays().add(userMarker);
        }

        userMarker.setPosition(userLocation);
        map.getController().setCenter(userLocation);
        map.invalidate();
    }
}
