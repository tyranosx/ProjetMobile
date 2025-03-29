package iut.dam.tp2b;

import android.app.AlertDialog;
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

    private AutoCompleteTextView autoAppliances, autoTimeSlots;
    private Button btnAjouter;
    private List<Appliance> appliances = new ArrayList<>();
    private List<TimeSlot> timeSlots = new ArrayList<>();
    private SharedPreferences prefs;

    private Appliance selectedAppliance;
    private TimeSlot selectedTimeSlot;

    // Modèle pour un équipement
    private class Appliance {
        int id;
        String name;

        // Ce que l'utilisateur voit dans la liste déroulante
        @NonNull
        public String toString() { return name; }
    }

    // Modèle pour un créneau horaire
    private class TimeSlot {
        int id;
        String begin;

        @NonNull
        public String toString() { return begin; }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate de la vue du fragment
        View view = inflater.inflate(R.layout.fragment_ajouter_usage, container, false);

        // Récupération des préférences utilisateur (stockées localement)
        prefs = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);

        // Initialisation des vues
        autoAppliances = view.findViewById(R.id.autoAppliances);
        autoTimeSlots = view.findViewById(R.id.autoTimeSlots);
        btnAjouter = view.findViewById(R.id.btnAjouterUsage);

        // Affiche la suggestion dès qu'on tape une lettre
        autoAppliances.setThreshold(1);
        autoTimeSlots.setThreshold(1);

        // Affiche la liste au clic
        autoAppliances.setOnClickListener(v -> autoAppliances.showDropDown());
        autoTimeSlots.setOnClickListener(v -> autoTimeSlots.showDropDown());

        // Récupération des données
        fetchAppliances();
        fetchTimeSlots();

        // Clic sur le bouton d'ajout
        btnAjouter.setOnClickListener(v -> ajouterUsage());

        return view;
    }

    // Récupération des équipements via l'API
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

                        // Remplissage de l'autocomplete
                        ArrayAdapter<Appliance> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, appliances);
                        autoAppliances.setAdapter(adapter);

                        // Sélection d'un équipement
                        autoAppliances.setOnItemClickListener((parent, view, position, id) -> {
                            selectedAppliance = (Appliance) parent.getItemAtPosition(position);
                        });
                    }
                });
    }

    // Récupération des créneaux horaires disponibles
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

                        // Remplissage de l'autocomplete
                        ArrayAdapter<TimeSlot> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, timeSlots);
                        autoTimeSlots.setAdapter(adapter);

                        // Sélection d’un créneau
                        autoTimeSlots.setOnItemClickListener((parent, view, position, id) -> {
                            selectedTimeSlot = (TimeSlot) parent.getItemAtPosition(position);
                        });
                    }
                });
    }

    // Ajoute un usage (équipement utilisé dans un créneau)
    private void ajouterUsage() {
        // Récupère les valeurs tapées dans les champs
        String applianceInput = autoAppliances.getText().toString().trim();
        String timeSlotInput = autoTimeSlots.getText().toString().trim();

        // Si pas encore sélectionné via clic mais tapé à la main, on cherche l'objet correspondant
        if (selectedAppliance == null) {
            for (Appliance a : appliances) {
                if (a.toString().equalsIgnoreCase(applianceInput)) {
                    selectedAppliance = a;
                    break;
                }
            }
        }

        if (selectedTimeSlot == null) {
            for (TimeSlot t : timeSlots) {
                if (t.toString().equalsIgnoreCase(timeSlotInput)) {
                    selectedTimeSlot = t;
                    break;
                }
            }
        }

        // Vérifie que les deux champs sont bien renseignés
        if (selectedAppliance == null || selectedTimeSlot == null) {
            Toast.makeText(getContext(), "Sélectionnez un équipement et un créneau", Toast.LENGTH_SHORT).show();
            return;
        }

        // Vérifie que l'utilisateur est bien identifié
        int userId = prefs.getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(getContext(), "Utilisateur non identifié", Toast.LENGTH_SHORT).show();
            return;
        }

        // Récupère le nombre d'usages déjà enregistrés sur ce créneau (pour définir l'ordre)
        Ion.with(this)
                .load("GET", "http://10.0.2.2/powerhome_server/get_order_count.php?time_slot_id=" + selectedTimeSlot.id)
                .asJsonObject()
                .setCallback((e, result) -> {
                    if (result != null && result.has("count")) {
                        int order = result.get("count").getAsInt() + 1;

                        // Construction de l'objet à envoyer
                        JsonObject data = new JsonObject();
                        data.addProperty("appliance_id", selectedAppliance.id);
                        data.addProperty("time_slot_id", selectedTimeSlot.id);
                        data.addProperty("order", order);

                        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                        data.addProperty("booked_at", now);
                        data.addProperty("user_id", userId);

                        // Envoi de l’usage au serveur
                        Ion.with(this)
                                .load("POST", "http://10.0.2.2/powerhome_server/add_appliance_usage.php")
                                .setHeader("Content-Type", "application/json")
                                .setJsonObjectBody(data)
                                .asJsonObject()
                                .setCallback((e2, res2) -> {
                                    if (res2 != null && res2.get("status").getAsString().equals("success")) {
                                        Toast.makeText(getContext(), "✅ Équipement ajouté au créneau", Toast.LENGTH_SHORT).show();

                                        // Réinitialisation des champs
                                        autoAppliances.setText("");
                                        autoTimeSlots.setText("");

                                        // Envoie des données pour bonus/malus éco-coin
                                        JsonObject ecoBody = new JsonObject();
                                        ecoBody.addProperty("user_id", userId);
                                        ecoBody.addProperty("time_slot_id", selectedTimeSlot.id);

                                        Ion.with(this)
                                                .load("POST", "http://10.0.2.2/powerhome_server/apply_eco_coin.php")
                                                .setHeader("Content-Type", "application/json")
                                                .setJsonObjectBody(ecoBody)
                                                .asJsonObject()
                                                .setCallback((err, ecoRes) -> {
                                                    if (ecoRes != null && ecoRes.has("amount")) {
                                                        int amount = ecoRes.get("amount").getAsInt();
                                                        String reason = ecoRes.get("reason").getAsString();
                                                        showEcoCoinDialog(amount, reason);
                                                    }

                                                    // Nettoyage final
                                                    autoAppliances.setText("");
                                                    autoTimeSlots.setText("");
                                                    selectedAppliance = null;
                                                    selectedTimeSlot = null;
                                                });

                                    } else {
                                        Toast.makeText(getContext(), "❌ Erreur lors de l'ajout", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
    }

    // Affiche une boîte de dialogue avec le résultat eco-coin
    private void showEcoCoinDialog(int amount, String reason) {
        String title = amount > 0 ? "🟢 Bonus éco-coin !" : "🔴 Malus éco-coin";
        String message = (amount > 0 ? "+" : "") + amount + " éco-coin(s)\n\n" + reason;

        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}