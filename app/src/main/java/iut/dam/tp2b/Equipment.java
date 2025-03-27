package iut.dam.tp2b;

public class Equipment {
    private String name;
    private String reference;
    private int wattage;
    private boolean selected;
    private int id;

    public Equipment(int id, String name, String reference, int wattage, boolean selected) {
        this.id = id;
        this.name = name;
        this.reference = reference;
        this.wattage = wattage;
        this.selected = selected;
    }

    public int getId() {
        return id;
    }

    public String getName() { return name; }
    public String getReference() { return reference; }
    public int getWattage() { return wattage; }
    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }
}
