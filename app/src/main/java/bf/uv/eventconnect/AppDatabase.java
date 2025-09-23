package bf.uv.eventconnect;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import bf.uv.eventconnect.Event;
import bf.uv.eventconnect.EventPrince;

@Database(entities = {Event.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract EventPrince eventPrince();
}