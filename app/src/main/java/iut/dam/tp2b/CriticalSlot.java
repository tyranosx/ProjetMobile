package iut.dam.tp2b;

public class CriticalSlot {
    private int id;
    private String date;
    private String hourRange;
    private int maxWattage;
    private int currentWattage;
    private boolean isEngaged = false;

    public CriticalSlot(int id, String date, String hourRange, int maxWattage, int currentWattage) {
        this.id = id;
        this.date = date;
        this.hourRange = hourRange;
        this.maxWattage = maxWattage;
        this.currentWattage = currentWattage;
    }

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

    public void setEngaged(boolean engaged) {
        isEngaged = engaged;
    }
}
