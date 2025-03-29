package iut.dam.tp2b;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.util.List;

// Classe représentant un habitat dans la résidence
public class Habitat {

    @SerializedName("residentName")  // Liaison avec le champ "residentName" du JSON
    private final String residentName;

    @SerializedName("floor")         // Étage de l'habitat
    private final int floor;

    @SerializedName("area")          // Surface de l'habitat en m²
    private final double area;

    @SerializedName("appliances")    // Nombre d’équipements présents dans l’habitat
    private final int appliances;

    @SerializedName("equipmentIcons") // Liste des icônes associés aux équipements
    private final List<Integer> equipmentIcons;

    // Constructeur
    public Habitat(String residentName, int floor, double area, int appliances, List<Integer> equipmentIcons) {
        this.residentName = residentName;
        this.floor = floor;
        this.area = area;
        this.appliances = appliances;
        this.equipmentIcons = equipmentIcons;
    }

    // Getters
    public String getResidentName() {
        return residentName;
    }

    public int getFloor() {
        return floor;
    }

    public double getArea() {
        return area;
    }

    public int getAppliances() {
        return appliances;
    }

    public List<Integer> getEquipmentIcons() {
        return equipmentIcons;
    }

    // 🧩 Convertit une chaîne JSON en objet Habitat
    public static Habitat getFromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Habitat.class);
    }

    // 🧩 Convertit un JSON en liste d'objets Habitat
    public static List<Habitat> getListFromJson(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Habitat>>() {}.getType();
        return gson.fromJson(json, type);
    }
}