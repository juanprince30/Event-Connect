package bf.uv.eventconnect;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EventPrince {
    @Insert
    long insert(Event event);

    @Query("SELECT * FROM events ORDER BY createdAt DESC")
    List<Event> getAll();

    @Query("SELECT * FROM events WHERE id = :id LIMIT 1")
    Event findById(long id);

    @Query("DELETE FROM events")
    void clear();
}