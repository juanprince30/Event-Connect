package bf.uv.eventconnect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Log.d("TAG","onCreate appelle");
        Button commencer=findViewById(R.id.button2);
        commencer.setOnClickListener(v->{
            Log.d("TAG","Boutton clicker dans lapp pour migrer vers la second Activity");
            Intent intent=new Intent(MainActivity.this, SecondActivity.class);
            startActivity(intent);
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
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