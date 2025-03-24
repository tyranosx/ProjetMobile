package iut.dam.tp2b;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.util.List;

public class Habitat {

    @SerializedName("residentName")  // Correspondance avec le JSON
    private final String residentName;

    @SerializedName("floor")
    private final int floor;

    @SerializedName("area")
    private final double area;

    @SerializedName("appliances")
    private final int appliances;

    @SerializedName("equipmentIcons")
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

    // Méthode pour parser un objet Habitat depuis un JSON
    public static Habitat getFromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Habitat.class);
    }

    // Méthode pour parser une liste d'objets Habitat depuis un JSON
    public static List<Habitat> getListFromJson(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Habitat>>() {}.getType();
        return gson.fromJson(json, type);
    }
}
