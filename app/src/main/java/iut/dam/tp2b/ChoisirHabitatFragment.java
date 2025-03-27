package iut.dam.tp2b;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;

public class ChoisirHabitatFragment extends Fragment {

    private RadioGroup radioGroup;
    private Button btnChoisir;
    private int selectedHabitatId = -1;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_choisir_habitat, container, false);

        radioGroup = view.findViewById(R.id.radioGroupHabitats);
        btnChoisir = view.findViewById(R.id.btnChoisirHabitat);

        SharedPreferences prefs = getActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(getContext(), "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            return view;
        }

        fetchHabitats();

        btnChoisir.setOnClickListener(v -> {
            if (selectedHabitatId == -1) {
                Toast.makeText(getContext(), "Veuillez sélectionner un habitat", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("ChoisirHabitat", "user_id=" + userId + ", habitat_id=" + selectedHabitatId);
            updateUserHabitat();
        });

        return view;
    }

    private void fetchHabitats() {
        String url = "http://10.0.2.2/powerhome_server/get_all_habitats.php"; // à créer côté serveur

        Ion.with(this)
                .load("GET", url)
                .asJsonObject()
                .setCallback((e, result) -> {
                    if (e != null || result == null || !result.get("status").getAsString().equals("success")) {
                        showErrorDialog("Erreur lors du chargement des habitats :\n" +
                                (e != null ? e.getMessage() : result != null ? result.get("message").getAsString() : "Réponse vide"));
                        return;
                    }

                    JsonArray habitats = result.getAsJsonArray("habitats");
                    for (int i = 0; i < habitats.size(); i++) {
                        JsonObject h = habitats.get(i).getAsJsonObject();
                        int id = h.get("id").getAsInt();
                        int floor = h.get("floor").getAsInt();
                        int area = h.get("area").getAsInt();

                        RadioButton rb = new RadioButton(getContext());
                        rb.setText("Habitat #" + id + " • Étage : " + floor + " • Surface : " + area + "m²");
                        rb.setId(id);
                        rb.setOnClickListener(v -> selectedHabitatId = id);
                        radioGroup.addView(rb);
                    }
                });
    }

    private void updateUserHabitat() {
        JsonObject data = new JsonObject();
        data.addProperty("user_id", userId);
        data.addProperty("habitat_id", selectedHabitatId);

        Ion.with(this)
                .load("POST", "http://10.0.2.2/powerhome_server/set_user_habitat.php") // à créer côté serveur
                .setHeader("Content-Type", "application/json")
                .setJsonObjectBody(data)
                .asJsonObject()
                .setCallback((e, result) -> {
                    if (e != null || result == null) {
                        showErrorDialog("Erreur réseau :\n" + (e != null ? e.getMessage() : "Réponse vide du serveur"));
                        return;
                    }

                    if (result.get("status").getAsString().equals("success")) {
                        Toast.makeText(getContext(), "✅ Habitat mis à jour !", Toast.LENGTH_SHORT).show();
                    } else {
                        showErrorDialog("❌ " + result.get("message").getAsString());
                    }
                });
    }

    private void showErrorDialog(String message) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Erreur")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}
