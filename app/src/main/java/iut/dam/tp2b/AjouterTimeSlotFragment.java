package iut.dam.tp2b;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AjouterTimeSlotFragment extends Fragment {

    private Button btnDate, btnTime, btnCreate;
    private Calendar selectedDateTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // On "inflate" le layout du fragment pour afficher l'UI
        View view = inflater.inflate(R.layout.fragment_ajouter_time_slot, container, false);

        // Initialisation de l'objet Calendar avec la date/heure courante
        selectedDateTime = Calendar.getInstance();

        // Récupération des boutons de la vue
        btnDate = view.findViewById(R.id.btnSelectDate);
        btnTime = view.findViewById(R.id.btnSelectTime);
        btnCreate = view.findViewById(R.id.btnCreateSlot);

        // Gestion des clics sur les boutons
        btnDate.setOnClickListener(v -> showDatePicker());     // Sélection de la date
        btnTime.setOnClickListener(v -> showTimePicker());     // Sélection de l'heure
        btnCreate.setOnClickListener(v -> sendSlotToServer()); // Création du créneau

        return view;
    }

    // Affiche un sélecteur de date à l'utilisateur
    private void showDatePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dp = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    // Met à jour la date sélectionnée
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, month);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // Affiche la date choisie sur le bouton
                    btnDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .format(selectedDateTime.getTime()));
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dp.show();
    }

    // Affiche un sélecteur d'heure à l'utilisateur
    private void showTimePicker() {
        TimePickerDialog tp = new TimePickerDialog(
                getContext(),
                (view, hourOfDay, minute) -> {
                    // Met à jour l'heure sélectionnée (minute forcée à 0)
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDateTime.set(Calendar.MINUTE, 0);

                    // Affiche l'heure choisie sur le bouton (ex: "14:00")
                    btnTime.setText(String.format(Locale.getDefault(), "%02d:00", hourOfDay));
                },
                12, // Heure par défaut (12h)
                0,
                true // Format 24h
        );
        tp.show();
    }

    // Envoie le créneau choisi au serveur
    private void sendSlotToServer() {
        // Format pour la date et l'heure complète
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        // Début du créneau = date et heure choisies
        String beginStr = sdf.format(selectedDateTime.getTime());

        // Fin du créneau = +1h
        Calendar endTime = (Calendar) selectedDateTime.clone();
        endTime.add(Calendar.HOUR_OF_DAY, 1);
        String endStr = sdf.format(endTime.getTime());

        // Préparation des données à envoyer en JSON
        JsonObject slotData = new JsonObject();
        slotData.addProperty("begin", beginStr);
        slotData.addProperty("end", endStr);
        slotData.addProperty("max_wattage", 2000); // Limite de puissance (fixée)
        slotData.add("user_id", null); // Pas d'utilisateur spécifique pour ce slot

        // Envoi de la requête POST au serveur avec Ion
        Ion.with(this)
                .load("POST", "http://10.0.2.2/powerhome_server/create_time_slot.php")
                .setHeader("Content-Type", "application/json")
                .setJsonObjectBody(slotData)
                .asJsonObject()
                .setCallback((e, result) -> {
                    // Vérifie si la requête a réussi
                    if (e == null && result != null && result.get("status").getAsString().equals("success")) {
                        Toast.makeText(getContext(), "Créneau ajouté ✅", Toast.LENGTH_SHORT).show();
                        // Retour au fragment précédent
                        getParentFragmentManager().popBackStack();
                    } else {
                        // Erreur lors de l'envoi ou du traitement
                        Toast.makeText(getContext(), "Erreur lors de la création", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}