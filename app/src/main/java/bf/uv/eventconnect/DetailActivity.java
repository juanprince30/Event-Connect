package bf.uv.eventconnect;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);

        ImageView imageView = findViewById(R.id.imageView);
        TextView nom = findViewById(R.id.nom);
        TextView date = findViewById(R.id.date);
        TextView description = findViewById(R.id.description);
        TextView lieu = findViewById(R.id.lieu);

        Intent intent = getIntent();

        nom.setText(intent.getStringExtra("titre"));
        date.setText("Date : " + intent.getStringExtra("date"));
        description.setText(intent.getStringExtra("description"));
        lieu.setText("Lieu : " + intent.getStringExtra("lieu"));

        // Récupérer le chemin de l'image
        String imagePath = intent.getStringExtra("imagePath");
        if (imagePath != null && !imagePath.isEmpty()) {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                imageView.setImageURI(Uri.fromFile(imgFile));
            } else {
                imageView.setImageResource(R.drawable.bg2);
            }
        } else {
            imageView.setImageResource(R.drawable.bg2);
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scrollView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("TAG", "On appelle la methode onStart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("TAG", "On appelle la methode onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("TAG", "On appelle la methode onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("TAG", "On appelle la methode onDestroy");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("TAG", "On appelle la methode onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("TAG", "On appelle la methode onStop");
    }
}
