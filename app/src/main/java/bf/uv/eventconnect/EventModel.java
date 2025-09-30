package bf.uv.eventconnect;

import java.io.Serializable;

public class EventModel implements Serializable {
    private long id;
    private String titre;
    private String date;
    private String description;
    private String lieu;
    private String imagePath;   // ⚡ chemin de l’image stockée (ou null si pas d’image)
    private String category;
    private String organisateur;
    private double latitude;
    private double longitude;

    public EventModel(long id, String titre, String date, String description, String lieu,
                      String imagePath, String category, String organisateur,
                      double latitude, double longitude) {
        this.id = id;
        this.titre = titre;
        this.date = date;
        this.description = description;
        this.lieu = lieu;
        this.imagePath = imagePath;
        this.category = category;
        this.organisateur = organisateur;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // --- Getters ---
    public long getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getLieu() {
        return lieu;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getCategory() {
        return category;
    }

    public String getOrganisateur() {
        return organisateur;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
