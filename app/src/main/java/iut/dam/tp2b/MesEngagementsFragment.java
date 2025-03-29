package iut.dam.tp2b;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.google.gson.*;
import com.koushikdutta.ion.Ion;
import java.util.*;

// Fragment affichant la liste des créneaux critiques sur lesquels l'utilisateur s'est engagé
public class MesEngagementsFragment extends Fragment {

    private RecyclerView recyclerView;
    private CriticalSlotAdapter adapter;
    private List<CriticalSlot> engagedSlots = new ArrayList<>();
    private SharedPreferences prefs;

    // Clé utilisée pour stocker localement les ID des engagements
    private static final String PREF_ENGAGED_SLOTS = "engaged_slots";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mes_engagements, container, false);

        // 📦 Récupère les préférences (user_session)
        prefs = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);

        // 🔧 Mise en place du RecyclerView
        recyclerView = view.findViewById(R.id.recyclerEngagedSlots);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 🔁 Adapter avec la liste des créneaux engagés
        adapter = new CriticalSlotAdapter(getContext(), engagedSlots, this::handleDisengage, true);
        recyclerView.setAdapter(adapter);

        // 🌐 Charge les engagements depuis l'API
        fetchEngagedSlots();

        return view;
    }

    // 🔁 Récupère les créneaux critiques engagés depuis l'API
    private void fetchEngagedSlots() {
        int userId = prefs.getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(getContext(), "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        Ion.with(this)
                .load("GET", "http://10.0.2.2/powerhome_server/get_critical_slots.php")
                .asJsonObject()
                .setCallback((e, result) -> {
                    if (e != null || result == null) {
                        Toast.makeText(getContext(), "Erreur réseau", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (result.has("status") && result.get("status").getAsString().equals("success")) {
                        JsonArray slots = result.getAsJsonArray("critical_slots");
                        engagedSlots.clear(); // Nettoie la liste précédente
                        Set<String> newPrefsSet = new HashSet<>(); // Pour mémoriser les ID localement

                        for (JsonElement element : slots) {
                            JsonObject slot = element.getAsJsonObject();
                            int id = slot.get("id").getAsInt();
                            boolean isEngaged = slot.has("is_engaged") && slot.get("is_engaged").getAsBoolean();

                            if (isEngaged) {
                                CriticalSlot c = new CriticalSlot(
                                        id,
                                        slot.get("date").getAsString(),
                                        slot.get("hour_range").getAsString(),
                                        slot.get("current_wattage").getAsInt(),
                                        slot.get("max_wattage").getAsInt()
                                );
                                c.setEngaged(true);
                                engagedSlots.add(c);
                                newPrefsSet.add(String.valueOf(id)); // Stocke l'engagement
                            }
                        }

                        // 💾 Met à jour les engagements enregistrés en local
                        prefs.edit().putStringSet(PREF_ENGAGED_SLOTS, newPrefsSet).apply();

                        adapter.notifyDataSetChanged();

                        if (engagedSlots.isEmpty()) {
                            Toast.makeText(getContext(), "Aucun engagement trouvé", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // ❌ Désengage l'utilisateur d'un créneau critique (appel POST à l'API)
    private void handleDisengage(CriticalSlot slot) {
        int userId = prefs.getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(getContext(), "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObject data = new JsonObject();
        data.addProperty("user_id", userId);
        data.addProperty("time_slot_id", slot.getId());

        Ion.with(this)
                .load("POST", "http://10.0.2.2/powerhome_server/disengage.php")
                .setHeader("Content-Type", "application/json")
                .setJsonObjectBody(data)
                .asJsonObject()
                .setCallback((e, result) -> {
                    if (e != null || result == null) {
                        Toast.makeText(getContext(), "Erreur réseau", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (result.get("status").getAsString().equals("success")) {
                        // 🔄 Retire le créneau de la liste et met à jour l'affichage
                        engagedSlots.remove(slot);
                        adapter.notifyDataSetChanged();

                        // 💾 Supprime également l'engagement localement
                        Set<String> engagedSet = prefs.getStringSet(PREF_ENGAGED_SLOTS, new HashSet<>());
                        engagedSet = new HashSet<>(engagedSet); // clone (évite bug avec Set non modifiable)
                        engagedSet.remove(String.valueOf(slot.getId()));
                        prefs.edit().putStringSet(PREF_ENGAGED_SLOTS, engagedSet).apply();

                        Toast.makeText(getContext(), "❌ Engagement annulé", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Erreur : " + result.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}