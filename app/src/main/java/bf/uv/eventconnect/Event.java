package bf.uv.eventconnect;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "events")
public class Event {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String titre;
    public String date;
    public String lieu;
    public String description;
    public String imagePath;
    public double longitude;
    public double latitude;
    public String organisateur;
    public String category;

    public long createdAt;
} 