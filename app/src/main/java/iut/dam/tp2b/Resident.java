package iut.dam.tp2b;

import java.util.List;

public class Resident {
    private String firstname;
    private String lastname;
    private int etage;
    private List<String> equipments;

    public Resident(String firstname, String lastname, int etage, List<String> equipments) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.etage = etage;
        this.equipments = equipments;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getFullName() {
        return firstname + " " + lastname;
    }

    public int getEtage() {
        return etage;
    }

    public List<String> getEquipments() {
        return equipments;
    }

    public int getEquipmentCount() {
        return equipments != null ? equipments.size() : 0;
    }

    public boolean hasEquipment(String equipmentName) {
        return equipments != null && equipments.contains(equipmentName.toLowerCase());
    }
}
