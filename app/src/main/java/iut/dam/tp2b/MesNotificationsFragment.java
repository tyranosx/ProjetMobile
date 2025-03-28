package iut.dam.tp2b;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.animation.AnimationUtils;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.google.gson.*;
import com.koushikdutta.ion.Ion;

import java.text.SimpleDateFormat;
import java.util.*;

public class MesNotificationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private CriticalSlotAdapter adapter;
    private List<CriticalSlot> criticalSlotList = new ArrayList<>();
    private SharedPreferences prefs;
    private static final String PREF_ENGAGED_SLOTS = "engaged_slots";
    private TextView tvUpcomingEngagements;
    private LinearLayout layoutBanner;
    private Button btnVoirMesEngagements;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mes_notifications, container, false);

        prefs = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);

        recyclerView = view.findViewById(R.id.recyclerCriticalSlots);
        tvUpcomingEngagements = view.findViewById(R.id.tvUpcomingEngagements);
        layoutBanner = view.findViewById(R.id.layoutBanner);
        btnVoirMesEngagements = view.findViewById(R.id.btnVoirMesEngagements);
        btnVoirMesEngagements.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new MesEngagementsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CriticalSlotAdapter(getContext(), criticalSlotList, this::handleEngagementToggle, false);
        recyclerView.setAdapter(adapter);

        fetchCriticalSlots();
        return view;
    }

    private void handleEngagementToggle(CriticalSlot slot) {
        int userId = prefs.getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(getContext(), "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        if (slot.isEngaged()) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Annuler l'engagement ?")
                    .setMessage("Souhaitez-vous vous désengager de ce créneau critique ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        JsonObject data = new JsonObject();
                        data.addProperty("user_id", userId);
                        data.addProperty("time_slot_id", slot.getId());

                        Ion.with(this)
                                .load("POST", "http://10.0.2.2/powerhome_server/disengage.php")
                                .setHeader("Content-Type", "application/json")
                                .setJsonObjectBody(data)
                                .asJsonObject()
                                .setCallback((e, result) -> {
                                    if (e == null && result != null && result.get("status").getAsString().equals("success")) {
                                        slot.setEngaged(false);
                                        cancelNotification(slot);
                                        adapter.notifyDataSetChanged();

                                        Set<String> engagedSet = new HashSet<>(prefs.getStringSet(PREF_ENGAGED_SLOTS, new HashSet<>()));
                                        engagedSet.remove(String.valueOf(slot.getId()));
                                        prefs.edit().putStringSet(PREF_ENGAGED_SLOTS, engagedSet).apply();

                                        updateUpcomingEngagementsBanner();
                                        Toast.makeText(getContext(), "❌ Désengagé avec succès", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    })
                    .setNegativeButton("Non", null)
                    .show();

        } else {
            JsonObject data = new JsonObject();
            data.addProperty("user_id", userId);
            data.addProperty("time_slot_id", slot.getId());

            Ion.with(this)
                    .load("POST", "http://10.0.2.2/powerhome_server/engage.php")
                    .setHeader("Content-Type", "application/json")
                    .setJsonObjectBody(data)
                    .asJsonObject()
                    .setCallback((e, result) -> {
                        if (e == null && result != null && result.get("status").getAsString().equals("success")) {
                            slot.setEngaged(true);
                            scheduleNotification(slot);
                            adapter.notifyDataSetChanged();

                            Set<String> engagedSet = new HashSet<>(prefs.getStringSet(PREF_ENGAGED_SLOTS, new HashSet<>()));
                            engagedSet.add(String.valueOf(slot.getId()));
                            prefs.edit().putStringSet(PREF_ENGAGED_SLOTS, engagedSet).apply();

                            updateUpcomingEngagementsBanner();
                            Toast.makeText(getContext(), "✅ Engagement confirmé", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void fetchCriticalSlots() {
        String url = "http://10.0.2.2/powerhome_server/get_critical_slots.php";
        Set<String> engagedIds = prefs.getStringSet(PREF_ENGAGED_SLOTS, new HashSet<>());

        Ion.with(this)
                .load("GET", url)
                .asJsonObject()
                .setCallback((e, result) -> {
                    if (e != null || result == null) {
                        Toast.makeText(getContext(), "Erreur réseau", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (result.get("status").getAsString().equals("success")) {
                        JsonArray slots = result.getAsJsonArray("critical_slots");
                        criticalSlotList.clear();

                        for (JsonElement element : slots) {
                            JsonObject slot = element.getAsJsonObject();
                            int id = slot.get("id").getAsInt();

                            boolean isEngaged = slot.has("is_engaged") && slot.get("is_engaged").getAsBoolean();
                            isEngaged = isEngaged || engagedIds.contains(String.valueOf(id));

                            CriticalSlot c = new CriticalSlot(
                                    id,
                                    slot.get("date").getAsString(),
                                    slot.get("hour_range").getAsString(),
                                    slot.get("current_wattage").getAsInt(),
                                    slot.get("max_wattage").getAsInt()
                            );
                            c.setEngaged(isEngaged);

                            if (isEngaged) scheduleNotification(c);
                            criticalSlotList.add(c);
                        }

                        // 🔽 Trie les créneaux par ordre chronologique
                        Collections.sort(criticalSlotList, (a, b) -> {
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                                Date d1 = sdf.parse(a.getDate() + " " + a.getHourRange().split(" - ")[0]);
                                Date d2 = sdf.parse(b.getDate() + " " + b.getHourRange().split(" - ")[0]);
                                return d1.compareTo(d2);
                            } catch (Exception ex) {
                                return 0;
                            }
                        });

                        adapter.notifyDataSetChanged();
                        updateUpcomingEngagementsBanner();
                    }
                });
    }

    private void updateUpcomingEngagementsBanner() {
        int count = 0;
        Calendar now = Calendar.getInstance();
        CriticalSlot nextEngagement = null;
        Date soonest = null;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        for (CriticalSlot slot : criticalSlotList) {
            if (slot.isEngaged()) {
                try {
                    Date d = sdf.parse(slot.getDate() + " " + slot.getHourRange().split(" - ")[0]);
                    if (d != null && d.after(now.getTime())) {
                        count++;
                        if (soonest == null || d.before(soonest)) {
                            soonest = d;
                            nextEngagement = slot;
                        }
                    }
                } catch (Exception e) {
                    Log.e("Banner", "Erreur parsing date", e);
                }
            }
        }

        if (count > 0 && nextEngagement != null) {
            layoutBanner.setVisibility(View.VISIBLE);
            layoutBanner.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_top));
            tvUpcomingEngagements.setText("⚡ Prochain engagement : " + nextEngagement.getHourRange() + " le " + nextEngagement.getDate());

            prefs.edit()
                    .putString("next_slot_date", nextEngagement.getDate())
                    .putString("next_slot_hour", nextEngagement.getHourRange())
                    .apply();

        } else {
            layoutBanner.setVisibility(View.GONE);
        }
    }

    private void scheduleNotification(CriticalSlot slot) {
        try {
            String[] hourParts = slot.getHourRange().split(" - ");
            String dateTimeStr = slot.getDate() + " " + hourParts[0];

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date slotTime = sdf.parse(dateTimeStr);
            if (slotTime == null) return;

            long triggerTime = slotTime.getTime() - 10 * 60 * 1000;
            if (triggerTime < System.currentTimeMillis()) {
                triggerTime = System.currentTimeMillis() + 1000;
            }

            Intent intent = new Intent(getContext(), NotificationReceiver.class);
            intent.putExtra("slot_date", slot.getDate());
            intent.putExtra("slot_hour", slot.getHourRange());

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    getContext(),
                    slot.getId(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);

        } catch (Exception e) {
            Log.e("NotifSchedule", "Erreur : " + e.getMessage());
        }
    }

    private void cancelNotification(CriticalSlot slot) {
        Intent intent = new Intent(getContext(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(),
                slot.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
