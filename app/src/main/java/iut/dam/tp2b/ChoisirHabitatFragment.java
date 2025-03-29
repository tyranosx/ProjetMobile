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
    private int selectedHabitatId = -1; // Stocke l’ID de l’habitat sélectionné
    private int userId; // ID de l’utilisateur connecté

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Création de la vue à partir du layout XML
        View view = inflater.inflate(R.layout.fragment_choisir_habitat, container, false);

        // Récupération des composants de la vue
        radioGroup = view.findViewById(R.id.radioGroupHabitats);
        btnChoisir = view.findViewById(R.id.btnChoisirHabitat);

        // Récupère l'identifiant de l'utilisateur depuis les préférences
        SharedPreferences prefs = getActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        // Si l'utilisateur n'est pas identifié, on affiche un message d'erreur
        if (userId == -1) {
            Toast.makeText(getContext(), "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Chargement de la liste des habitats disponibles
        fetchHabitats();

        // Gestion du clic sur le bouton de validation
        btnChoisir.setOnClickListener(v -> {
            if (selectedHabitatId == -1) {
                Toast.makeText(getContext(), "Veuillez sélectionner un habitat", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("ChoisirHabitat", "user_id=" + userId + ", habitat_id=" + selectedHabitatId);
            updateUserHabitat(); // Envoie la sélection au serveur
        });

        return view;
    }

    // Récupère les habitats depuis le serveur et les affiche sous forme de boutons radio
    private void fetchHabitats() {
        String url = "http://10.0.2.2/powerhome_server/get_all_habitats.php";

        Ion.with(this)
                .load("GET", url)
                .asJsonObject()
                .setCallback((e, result) -> {
                    // Gestion des erreurs de réponse ou de réseau
                    if (e != null || result == null || !result.get("status").getAsString().equals("success")) {
                        showErrorDialog("Erreur lors du chargement des habitats :\n" +
                                (e != null ? e.getMessage() : result != null ? result.get("message").getAsString() : "Réponse vide"));
                        return;
                    }

                    // Récupération et affichage dynamique des habitats
                    JsonArray habitats = result.getAsJsonArray("habitats");
                    for (int i = 0; i < habitats.size(); i++) {
                        JsonObject h = habitats.get(i).getAsJsonObject();
                        int id = h.get("id").getAsInt();
                        int floor = h.get("floor").getAsInt();
                        int area = h.get("area").getAsInt();

                        // Création d'un bouton radio pour chaque habitat
                        RadioButton rb = new RadioButton(getContext());
                        rb.setText("Habitat #" + id + " • Étage : " + floor + " • Surface : " + area + "m²");
                        rb.setId(id);

                        // Mise à jour de l'habitat sélectionné au clic
                        rb.setOnClickListener(v -> selectedHabitatId = id);

                        radioGroup.addView(rb);
                    }
                });
    }

    // Envoie au serveur l’ID de l’habitat sélectionné pour l’utilisateur
    private void updateUserHabitat() {
        JsonObject data = new JsonObject();
        data.addProperty("user_id", userId);
        data.addProperty("habitat_id", selectedHabitatId);

        Ion.with(this)
                .load("POST", "http://10.0.2.2/powerhome_server/set_user_habitat.php")
                .setHeader("Content-Type", "application/json")
                .setJsonObjectBody(data)
                .asJsonObject()
                .setCallback((e, result) -> {
                    if (e != null || result == null) {
                        showErrorDialog("Erreur réseau :\n" + (e != null ? e.getMessage() : "Réponse vide du serveur"));
                        return;
                    }

                    // Si tout s'est bien passé, on affiche une confirmation
                    if (result.get("status").getAsString().equals("success")) {
                        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                                .setTitle("Succès")
                                .setMessage("✅ Habitat mis à jour ! Déconnectez puis reconnectez-vous pour ajouter un équipement (mise à jour du habitat_id)")
                                .setPositiveButton("OK", null)
                                .show();
                    } else {
                        showErrorDialog("❌ " + result.get("message").getAsString());
                    }
                });
    }

    // Affiche une boîte de dialogue en cas d’erreur
    private void showErrorDialog(String message) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Erreur")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}