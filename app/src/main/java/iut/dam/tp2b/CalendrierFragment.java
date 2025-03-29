package iut.dam.tp2b;

import android.graphics.Color;
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

public class CalendrierFragment extends Fragment {

    private GridView gridView;
    private TextView txtMonth;

    // Associe une date (String) à un pourcentage de consommation
    private final Map<String, Integer> dayToPercentage = new HashMap<>();
    private final List<String> daysInMonth = new ArrayList<>();

    // Format de date utilisé pour les clés et l'affichage
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final Calendar calendar = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Affiche la vue du fragment à partir du layout XML
        View view = inflater.inflate(R.layout.fragment_calendrier, container, false);

        gridView = view.findViewById(R.id.gridCalendar);
        txtMonth = view.findViewById(R.id.txtMonth);

        // Génère les jours du mois actuel
        generateMonthDays();

        // Récupère les données de consommation pour chaque jour
        fetchConsumption();

        // Affiche un toast avec les infos conso quand on clique sur un jour
        gridView.setOnItemClickListener((parent, view1, position, id) -> {
            String dateStr = daysInMonth.get(position);
            int pct = dayToPercentage.getOrDefault(dateStr, 0);
            String msg = "Le " + dateStr + " : " + pct + "% de consommation";
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    // Crée la liste des jours du mois actuel au format "yyyy-MM-dd"
    private void generateMonthDays() {
        daysInMonth.clear();
        calendar.set(Calendar.DAY_OF_MONTH, 1); // Commence au 1er du mois

        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 1; i <= maxDay; i++) {
            calendar.set(Calendar.DAY_OF_MONTH, i);
            daysInMonth.add(sdf.format(calendar.getTime())); // Ex: 2025-03-29
        }

        // Affiche le mois et l’année en haut du calendrier (ex: "Mars 2025")
        txtMonth.setText(new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.getTime()));
    }

    // Récupère les pourcentages de consommation pour chaque jour depuis l’API
    private void fetchConsumption() {
        Ion.with(this)
                .load("GET", "http://10.0.2.2/powerhome_server/get_residence_consumption.php")
                .asJsonArray()
                .setCallback((e, result) -> {
                    if (result != null) {
                        dayToPercentage.clear();

                        // Stocke les données sous forme : date → pourcentage
                        for (JsonElement elem : result) {
                            JsonObject obj = elem.getAsJsonObject();
                            String date = obj.get("date").getAsString();
                            int percentage = obj.get("percentage").getAsInt();
                            dayToPercentage.put(date, percentage);
                        }

                        // Met à jour l'affichage du calendrier avec les couleurs
                        gridView.setAdapter(new CalendarAdapter());
                    }
                });
    }

    // Adapter personnalisé pour afficher le calendrier avec des couleurs selon le niveau de conso
    private class CalendarAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return daysInMonth.size(); // Nombre de jours dans le mois
        }

        @Override
        public Object getItem(int position) {
            return daysInMonth.get(position); // Retourne la date à une position donnée
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView cell = new TextView(getContext());
            String dateStr = daysInMonth.get(position);

            // Récupère le jour (ex: 2025-03-29 → 29)
            int day = Integer.parseInt(dateStr.substring(8));
            cell.setText(String.valueOf(day));

            cell.setGravity(Gravity.CENTER);
            cell.setTextSize(18);
            cell.setPadding(0, 30, 0, 30);

            // Récupère le pourcentage de consommation pour cette date
            int pct = dayToPercentage.getOrDefault(dateStr, 0);

            // Applique une couleur de fond selon le niveau de consommation
            if (pct <= 30) {
                cell.setBackgroundColor(Color.parseColor("#A5D6A7")); // Vert clair
            } else if (pct <= 70) {
                cell.setBackgroundColor(Color.parseColor("#FFE082")); // Orange
            } else {
                cell.setBackgroundColor(Color.parseColor("#EF9A9A")); // Rouge clair
            }

            return cell;
        }
    }
}