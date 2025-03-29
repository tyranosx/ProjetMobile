package iut.dam.tp2b;

// Représente un créneau critique de consommation électrique
public class CriticalSlot {
    private int id;                   // Identifiant unique du créneau
    private String date;             // Date du créneau (ex: "2025-03-29")
    private String hourRange;        // Plage horaire (ex: "19h-20h")
    private int maxWattage;          // Limite maximale de consommation (W)
    private int currentWattage;      // Consommation actuelle (W)
    private boolean isEngaged = false; // Si l'utilisateur s'est engagé à respecter le créneau

    // Constructeur
    public CriticalSlot(int id, String date, String hourRange, int maxWattage, int currentWattage) {
        this.id = id;
        this.date = date;
        this.hourRange = hourRange;
        this.maxWattage = maxWattage;
        this.currentWattage = currentWattage;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getHourRange() {
        return hourRange;
    }

    public int getMaxWattage() {
        return maxWattage;
    }

    public int getCurrentWattage() {
        return currentWattage;
    }

    public boolean isEngaged() {
        return isEngaged;
    }

    // Setter
    public void setEngaged(boolean engaged) {
        isEngaged = engaged;
    }
}