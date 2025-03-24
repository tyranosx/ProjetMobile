package iut.dam.tp2b;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TimeSlot {

    public int id;
    public Date begin;
    public Date end;
    public int maxWattage;
    public List<Booking> bookings;

    public TimeSlot() {
        bookings = new ArrayList<>();
    }

    public TimeSlot(int id, Date begin, Date end, int maxWattage) {
        this.id = id;
        this.begin = begin;
        this.end = end;
        this.maxWattage = maxWattage;
        bookings = new ArrayList<>();
    }
}
