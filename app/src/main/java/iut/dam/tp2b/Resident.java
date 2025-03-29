package iut.dam.tp2b;

import java.util.List;

// 🎯 Classe représentant un résident dans la résidence
public class Resident {
    private String firstname;
    private String lastname;
    private int etage; // étage de l'habitat
    private List<String> equipments; // liste des noms d'équipements

    // 🏗️ Constructeur
    public Resident(String firstname, String lastname, int etage, List<String> equipments) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.etage = etage;
        this.equipments = equipments;
    }

    // 👉 Getters
    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    // 🧑 Affiche le nom complet
    public String getFullName() {
        return firstname + " " + lastname;
    }

    // 🏢 Retourne l'étage du résident
    public int getEtage() {
        return etage;
    }

    // ⚡ Liste des équipements utilisés
    public List<String> getEquipments() {
        return equipments;
    }

    // 📦 Nombre d’équipements utilisés
    public int getEquipmentCount() {
        return equipments != null ? equipments.size() : 0;
    }

    // 🔍 Vérifie si un résident possède un équipement spécifique
    public boolean hasEquipment(String equipmentName) {
        return equipments != null && equipments.contains(equipmentName.toLowerCase());
    }
}