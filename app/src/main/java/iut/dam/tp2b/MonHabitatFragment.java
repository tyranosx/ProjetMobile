package iut.dam.tp2b;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

public class MonHabitatFragment extends Fragment {

    private RecyclerView recyclerEquipments;
    private EquipmentAdapter adapter;
    private List<Equipment> equipmentList = new ArrayList<>();
    private int userId; // TODO : remplacer par ID dynamique si besoin

    private TextView tvTotalWattage, tvWattageRemaining;
    private Button btnUpdateEquipments;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mon_habitat, container, false);

        SharedPreferences prefs = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Erreur")
                    .setMessage("Aucun utilisateur connecté")
                    .setPositiveButton("OK", null)
                    .show();
            return view;
        }

        tvTotalWattage = view.findViewById(R.id.tvTotalWattage);
        tvWattageRemaining = view.findViewById(R.id.tvWattageRemaining);
        btnUpdateEquipments = view.findViewById(R.id.btnUpdateEquipments);

        recyclerEquipments = view.findViewById(R.id.recyclerEquipments);
        recyclerEquipments.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EquipmentAdapter(getContext(), equipmentList, this::updateTotalWattage);
        recyclerEquipments.setAdapter(adapter);

        btnUpdateEquipments.setOnClickListener(v -> sendUpdateToServer());

        fetchMyEquipments();

        return view;
    }

    private void fetchMyEquipments() {
        String url = "http://10.0.2.2/powerhome_server/get_my_equipments.php?user_id=" + userId;

        Ion.with(this)
                .load("GET", url)
                .asJsonObject()
                .setCallback((e, result) -> {
                    if (e != null || result == null) {
                        Toast.makeText(getContext(), "Erreur réseau", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (result.get("status").getAsString().equals("success")) {
                        equipmentList.clear();
                        JsonArray eqs = result.getAsJsonArray("equipments");

                        for (int i = 0; i < eqs.size(); i++) {
                            JsonObject eq = eqs.get(i).getAsJsonObject();
                            equipmentList.add(new Equipment(
                                    eq.get("id").getAsInt(),
                                    eq.get("name").getAsString(),
                                    eq.get("reference").getAsString(),
                                    eq.get("wattage").getAsInt(),
                                    true // précochés car ils sont déjà liés à l'habitat
                            ));
                        }

                        adapter.notifyDataSetChanged();
                        updateTotalWattage();

                        if (result.has("max_wattage") && !result.get("max_wattage").isJsonNull()) {
                            int max = result.get("max_wattage").getAsInt();
                            int total = result.get("total_wattage").getAsInt();
                            int remaining = max - total;
                            tvWattageRemaining.setText("Restant : " + remaining + " W");
                        } else {
                            tvWattageRemaining.setText("Aucun créneau défini");
                        }

                    } else {
                        Toast.makeText(getContext(), "Erreur : " + result.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateTotalWattage() {
        int total = 0;
        for (Equipment eq : equipmentList) {
            if (eq.isSelected()) total += eq.getWattage();
        }
        tvTotalWattage.setText("Puissance totale : " + total + " W");
    }

    private void sendUpdateToServer() {
        JsonObject data = new JsonObject();
        data.addProperty("user_id", userId);

        JsonArray selectedEquipments = new JsonArray();
        for (Equipment eq : equipmentList) {
            if (eq.isSelected()) selectedEquipments.add(eq.getId());
        }

        data.add("equipment_ids", selectedEquipments);

        Ion.with(this)
                .load("POST", "http://10.0.2.2/powerhome_server/update_my_equipments.php")
                .setHeader("Content-Type", "application/json")
                .setJsonObjectBody(data)
                .asJsonObject()
                .setCallback((e, result) -> {
                    if (e != null || result == null) {
                        Toast.makeText(getContext(), "Erreur de mise à jour", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (result.get("status").getAsString().equals("success")) {
                        Toast.makeText(getContext(), "✅ Mise à jour effectuée", Toast.LENGTH_SHORT).show();
                    } else {
                        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                                .setTitle("❌ Erreur serveur")
                                .setMessage(result.get("message").getAsString())
                                .setPositiveButton("OK", null)
                                .show();
                    }
                });
    }
}