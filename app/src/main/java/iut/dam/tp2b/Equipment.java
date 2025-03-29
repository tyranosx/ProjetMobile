package iut.dam.tp2b;

// Classe représentant un équipement électroménager
public class Equipment {

    private String name;        // Nom de l'équipement (ex: "Lave-linge")
    private String reference;   // Référence du modèle
    private int wattage;        // Puissance en watts
    private boolean selected;   // Utilisé pour la sélection dans une liste ou UI
    private int id;             // Identifiant unique (venant de la base de données)

    // Constructeur
    public Equipment(int id, String name, String reference, int wattage, boolean selected) {
        this.id = id;
        this.name = name;
        this.reference = reference;
        this.wattage = wattage;
        this.selected = selected;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getReference() {
        return reference;
    }

    public int getWattage() {
        return wattage;
    }

    public boolean isSelected() {
        return selected;
    }

    // Setter
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}