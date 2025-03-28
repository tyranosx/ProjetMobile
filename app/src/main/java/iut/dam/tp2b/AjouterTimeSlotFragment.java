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
        View view = inflater.inflate(R.layout.fragment_ajouter_time_slot, container, false);

        selectedDateTime = Calendar.getInstance();
        btnDate = view.findViewById(R.id.btnSelectDate);
        btnTime = view.findViewById(R.id.btnSelectTime);
        btnCreate = view.findViewById(R.id.btnCreateSlot);

        btnDate.setOnClickListener(v -> showDatePicker());
        btnTime.setOnClickListener(v -> showTimePicker());
        btnCreate.setOnClickListener(v -> sendSlotToServer());

        return view;
    }

    private void showDatePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dp = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, month);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    btnDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDateTime.getTime()));
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dp.show();
    }

    private void showTimePicker() {
        TimePickerDialog tp = new TimePickerDialog(
                getContext(),
                (view, hourOfDay, minute) -> {
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDateTime.set(Calendar.MINUTE, 0);
                    btnTime.setText(String.format(Locale.getDefault(), "%02d:00", hourOfDay));
                },
                12,
                0,
                true
        );
        tp.show();
    }

    private void sendSlotToServer() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String beginStr = sdf.format(selectedDateTime.getTime());

        Calendar endTime = (Calendar) selectedDateTime.clone();
        endTime.add(Calendar.HOUR_OF_DAY, 1);
        String endStr = sdf.format(endTime.getTime());

        JsonObject slotData = new JsonObject();
        slotData.addProperty("begin", beginStr);
        slotData.addProperty("end", endStr);
        slotData.addProperty("max_wattage", 2000); // fixe
        slotData.add("user_id", null); // null

        Ion.with(this)
                .load("POST", "http://10.0.2.2/powerhome_server/create_time_slot.php")
                .setHeader("Content-Type", "application/json")
                .setJsonObjectBody(slotData)
                .asJsonObject()
                .setCallback((e, result) -> {
                    if (e == null && result != null && result.get("status").getAsString().equals("success")) {
                        Toast.makeText(getContext(), "Créneau ajouté ✅", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().popBackStack(); // retour
                    } else {
                        Toast.makeText(getContext(), "Erreur lors de la création", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
