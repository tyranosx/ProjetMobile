package iut.dam.tp2b;

import java.util.ArrayList;
import java.util.List;

public class Appliance {

    public int id;
    public String name;
    public String reference;
    public int wattage;
    public List<Booking> bookings;


    public Appliance() {
        bookings = new ArrayList<>();
    }

    public Appliance(int id, String name, String reference, int wattage) {
        this.id = id;
        this.name = name;
        this.reference = reference;
        this.wattage = wattage;
        bookings = new ArrayList<>();
    }
}
