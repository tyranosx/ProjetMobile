package iut.dam.tp2b;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;

public class AjouterEquipementFragment extends Fragment {

    private EditText etNom, etReference, etWattage;
    private Button btnAjouter;
    private int habitatId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ajouter_equipement, container, false);

        etNom = view.findViewById(R.id.etNomEquipement);
        etReference = view.findViewById(R.id.etReferenceEquipement);
        etWattage = view.findViewById(R.id.etWattageEquipement);
        btnAjouter = view.findViewById(R.id.btnAjouterEquipement);

        SharedPreferences prefs = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        habitatId = prefs.getInt("habitat_id", -1);

        Log.d("AjouterEquipement", "habitat_id = " + habitatId);

        btnAjouter.setOnClickListener(v -> {
            if (habitatId == -1) {
                showDialog("Aucun habitat associé à votre compte. Veuillez en sélectionner un d'abord.");
                return;
            }

            String nom = etNom.getText().toString().trim();
            String ref = etReference.getText().toString().trim();
            String wattStr = etWattage.getText().toString().trim();

            if (TextUtils.isEmpty(nom) || TextUtils.isEmpty(ref) || TextUtils.isEmpty(wattStr)) {
                showDialog("Veuillez remplir tous les champs");
                return;
            }

            int watt;
            try {
                watt = Integer.parseInt(wattStr);
            } catch (NumberFormatException e) {
                showDialog("La puissance doit être un nombre");
                return;
            }

            JsonObject data = new JsonObject();
            data.addProperty("name", nom);
            data.addProperty("reference", ref);
            data.addProperty("wattage", watt);
            data.addProperty("habitat_id", habitatId);

            Ion.with(this)
                    .load("POST", "http://10.0.2.2/powerhome_server/add_appliance.php")
                    .setHeader("Content-Type", "application/json")
                    .setJsonObjectBody(data)
                    .asJsonObject()
                    .setCallback((e, result) -> {
                        if (e != null || result == null) {
                            showDialog("Erreur réseau : " + (e != null ? e.getMessage() : "Réponse vide"));
                            return;
                        }

                        if (result.get("status").getAsString().equals("success")) {
                            showDialog("✅ Équipement ajouté !");
                            etNom.setText("");
                            etReference.setText("");
                            etWattage.setText("");
                        } else {
                            showDialog("❌ " + result.get("message").getAsString());
                        }
                    });
        });

        return view;
    }

    private void showDialog(String msg) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Info")
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .show();
    }
}
