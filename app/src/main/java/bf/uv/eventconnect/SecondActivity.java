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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class SecondActivity extends AppCompatActivity{
    RecyclerView recyclerView;
    List<EventModel> events = new ArrayList<>();
    EventAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_second);

        Button carte_button=findViewById(R.id.buttoncart);

        carte_button.setOnClickListener(v->{
            Log.d("TAG","Boutton clicker dans lapp pour migrer vers la Carte Activity");
            Intent intent=new Intent(SecondActivity.this, CarteActivity.class);
            intent.putExtra("events", new ArrayList<>(events));
            startActivity(intent);
        });

        recyclerView = findViewById(R.id.recyclerViewEvents);
        adapter = new EventAdapter(events);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        Button ajouter=findViewById(R.id.button2);

        ajouter.setOnClickListener( v->{
            Log.d("TAG","Boutton clicker dans lapp pour migrer vers la Add Activity");
            Intent intent=new Intent(SecondActivity.this, AddActivity.class);
            startActivity(intent);
        });

        //loadEvents();

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
        loadEvents();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("TAG", "On appelle la methode onStop");
    }

    private EventModel toModel(Event entity) {
        return new EventModel(
                entity.id,
                entity.titre,
                entity.date,
                entity.description,
                entity.lieu,
                entity.imagePath,
                entity.category,
                entity.organisateur,
                entity.latitude,
                entity.longitude
        );
    }

    private void loadEvents() {
        AppDatabase db = DbProvider.get(this);

        Executors.newSingleThreadExecutor().execute(() -> {
            List<Event> eventEntities = db.eventDao().getAll();

            events.clear();
            for (Event entity : eventEntities) {
                events.add(toModel(entity));
            }

            runOnUiThread(() -> {
                adapter.notifyDataSetChanged();
                Log.d("DB", "Events count=" + events.size());
            });
        });
    }



}
