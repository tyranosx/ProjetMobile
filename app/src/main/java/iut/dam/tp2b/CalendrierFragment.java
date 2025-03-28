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

    private final Map<String, Integer> dayToPercentage = new HashMap<>();
    private final List<String> daysInMonth = new ArrayList<>();

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final Calendar calendar = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendrier, container, false);

        gridView = view.findViewById(R.id.gridCalendar);
        txtMonth = view.findViewById(R.id.txtMonth);

        generateMonthDays();
        fetchConsumption();

        return view;
    }

    private void generateMonthDays() {
        daysInMonth.clear();
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 1; i <= maxDay; i++) {
            calendar.set(Calendar.DAY_OF_MONTH, i);
            daysInMonth.add(sdf.format(calendar.getTime()));
        }

        txtMonth.setText(new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.getTime()));
    }

    private void fetchConsumption() {
        Ion.with(this)
                .load("GET", "http://10.0.2.2/powerhome_server/get_residence_consumption.php")
                .asJsonArray()
                .setCallback((e, result) -> {
                    if (result != null) {
                        dayToPercentage.clear();
                        for (JsonElement elem : result) {
                            JsonObject obj = elem.getAsJsonObject();
                            String date = obj.get("date").getAsString();
                            int percentage = obj.get("percentage").getAsInt();
                            dayToPercentage.put(date, percentage);
                        }

                        // Maj UI
                        gridView.setAdapter(new CalendarAdapter());
                    }
                });
    }

    private class CalendarAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return daysInMonth.size();
        }

        @Override
        public Object getItem(int position) {
            return daysInMonth.get(position);
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
            int day = Integer.parseInt(dateStr.substring(8)); // yyyy-MM-dd → dd

            cell.setText(String.valueOf(day));
            cell.setGravity(Gravity.CENTER);
            cell.setTextSize(18);
            cell.setPadding(0, 30, 0, 30);

            // Couleur de fond selon % conso
            int pct = dayToPercentage.getOrDefault(dateStr, 0);
            if (pct <= 30) {
                cell.setBackgroundColor(Color.parseColor("#A5D6A7")); // 🟢 vert clair
            } else if (pct <= 70) {
                cell.setBackgroundColor(Color.parseColor("#FFE082")); // 🟠 orange
            } else {
                cell.setBackgroundColor(Color.parseColor("#EF9A9A")); // 🔴 rouge
            }

            return cell;
        }
    }
}
