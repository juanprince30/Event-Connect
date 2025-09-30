package bf.uv.eventconnect;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ZoomControls;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.config.Configuration;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class GetMapsActivity extends AppCompatActivity {
    private MapView map;
    private double selectedLat;
    private double selectedLon;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_getmaps);
        Log.d("TAG","onCreate appelle");


        map = findViewById(R.id.map);
        map.setMultiTouchControls(true); // zoom avec deux doigts

        // Position initiale (ex: Ouagadougou)
        GeoPoint startPoint = new GeoPoint(12.3714, -1.5197);
        map.getController().setZoom(12.0);
        map.getController().setCenter(startPoint);
        map.setMultiTouchControls(true);


        // Gestion du clic utilisateur
        map.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                map.performClick(); // ⚡ appelle la méthode performClick() de la MapView

                GeoPoint clickedPoint = (GeoPoint) map.getProjection().fromPixels(
                        (int) event.getX(),
                        (int) event.getY()
                );

                selectedLat = clickedPoint.getLatitude();
                selectedLon = clickedPoint.getLongitude();

                map.getOverlays().clear();

                Marker marker = new Marker(map);
                marker.setPosition(clickedPoint);
                marker.setTitle("Emplacement choisi");
                map.getOverlays().add(marker);

                map.invalidate();

                Log.d("LOCATION", "Lat: " + selectedLat + ", Lon: " + selectedLon);
            }
            return true;
        });

        Button btnSave = findViewById(R.id.btnSaveLocation);
        btnSave.setOnClickListener(v -> {
            if (selectedLat != 0 && selectedLon != 0) {
                // Ici tu peux sauvegarder la position dans Room, SharedPreferences, ou la renvoyer
                Log.d("LOCATION_SAVED", "Lat: " + selectedLat + ", Lon: " + selectedLon);

                // Exemple : renvoyer les coordonnées à l’activité précédente
                Intent resultIntent = new Intent();
                resultIntent.putExtra("latitude", selectedLat);
                resultIntent.putExtra("longitude", selectedLon);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Veuillez sélectionner un point sur la carte", Toast.LENGTH_SHORT).show();
            }
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


}

