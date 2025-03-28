package iut.dam.tp2b;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.gson.*;
import com.koushikdutta.ion.Ion;

import java.text.SimpleDateFormat;
import java.util.*;

public class AjouterUsageFragment extends Fragment {

    private Spinner spinnerAppliances, spinnerTimeSlots;
    private Button btnAjouter;
    private List<Appliance> appliances = new ArrayList<>();
    private List<TimeSlot> timeSlots = new ArrayList<>();
    private SharedPreferences prefs;

    private class Appliance {
        int id;
        String name;
        public String toString() { return name; }
    }

    private class TimeSlot {
        int id;
        String begin;
        public String toString() { return begin; }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ajouter_usage, container, false);

        prefs = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);

        spinnerAppliances = view.findViewById(R.id.spinnerAppliances);
        spinnerTimeSlots = view.findViewById(R.id.spinnerTimeSlots);
        btnAjouter = view.findViewById(R.id.btnAjouterUsage);

        fetchAppliances();
        fetchTimeSlots();

        btnAjouter.setOnClickListener(v -> ajouterUsage());

        return view;
    }

    private void fetchAppliances() {
        int userHabitatId = prefs.getInt("habitat_id", -1);
        if (userHabitatId == -1) return;

        Ion.with(this)
                .load("GET", "http://10.0.2.2/powerhome_server/get_appliances.php?habitat_id=" + userHabitatId)
                .asJsonArray()
                .setCallback((e, result) -> {
                    if (result != null) {
                        appliances.clear();
                        for (JsonElement elem : result) {
                            JsonObject obj = elem.getAsJsonObject();
                            Appliance a = new Appliance();
                            a.id = obj.get("id").getAsInt();
                            a.name = obj.get("name").getAsString();
                            appliances.add(a);
                        }
                        spinnerAppliances.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, appliances));
                    }
                });
    }

    private void fetchTimeSlots() {
        Ion.with(this)
                .load("GET", "http://10.0.2.2/powerhome_server/get_time_slots.php")
                .asJsonArray()
                .setCallback((e, result) -> {
                    if (result != null) {
                        timeSlots.clear();
                        for (JsonElement elem : result) {
                            JsonObject obj = elem.getAsJsonObject();
                            TimeSlot t = new TimeSlot();
                            t.id = obj.get("id").getAsInt();
                            t.begin = obj.get("begin").getAsString();
                            timeSlots.add(t);
                        }
                        spinnerTimeSlots.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, timeSlots));
                    }
                });
    }

    private void ajouterUsage() {
        Appliance appliance = (Appliance) spinnerAppliances.getSelectedItem();
        TimeSlot timeSlot = (TimeSlot) spinnerTimeSlots.getSelectedItem();
        if (appliance == null || timeSlot == null) return;

        Ion.with(this)
                .load("GET", "http://10.0.2.2/powerhome_server/get_order_count.php?time_slot_id=" + timeSlot.id)
                .asJsonObject()
                .setCallback((e, result) -> {
                    if (result != null && result.has("count")) {
                        int order = result.get("count").getAsInt() + 1;

                        JsonObject data = new JsonObject();
                        data.addProperty("appliance_id", appliance.id);
                        data.addProperty("time_slot_id", timeSlot.id);
                        data.addProperty("order", order);

                        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                        data.addProperty("booked_at", now);

                        Ion.with(this)
                                .load("POST", "http://10.0.2.2/powerhome_server/add_appliance_usage.php")
                                .setHeader("Content-Type", "application/json")
                                .setJsonObjectBody(data)
                                .asJsonObject()
                                .setCallback((e2, res2) -> {
                                    if (res2 != null && res2.get("status").getAsString().equals("success")) {
                                        Toast.makeText(getContext(), "✅ Équipement ajouté au créneau", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), "❌ Erreur lors de l'ajout", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
    }
}
