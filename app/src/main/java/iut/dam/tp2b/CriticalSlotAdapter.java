package iut.dam.tp2b;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CriticalSlotAdapter extends RecyclerView.Adapter<CriticalSlotAdapter.SlotViewHolder> {

    private Context context;
    private List<CriticalSlot> slotList;
    private OnEngageClickListener engageClickListener;
    private boolean showCancelButton = false;

    public interface OnEngageClickListener {
        void onEngage(CriticalSlot slot);
    }

    public CriticalSlotAdapter(Context context, List<CriticalSlot> slotList,
                               OnEngageClickListener listener, boolean showCancelButton) {
        this.context = context;
        this.slotList = slotList;
        this.engageClickListener = listener;
        this.showCancelButton = showCancelButton;
    }

    @NonNull
    @Override
    public SlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_critical_slot, parent, false);
        return new SlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlotViewHolder holder, int position) {
        CriticalSlot slot = slotList.get(position);

        holder.tvDate.setText(slot.getDate());
        holder.tvHour.setText(slot.getHourRange());
        holder.tvConso.setText("Max : " + slot.getMaxWattage() + "W - Actuel : " + slot.getCurrentWattage() + "W");

        // 🔥 Badge critique avec animation si imminent (<15min)
        if ((float) slot.getCurrentWattage() / slot.getMaxWattage() >= 0.95f) {
            holder.tvBadge.setVisibility(View.VISIBLE);
            holder.tvBadge.setText("🔥 Critique");

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                Date slotDateTime = sdf.parse(slot.getDate() + " " + slot.getHourRange().split(" - ")[0]);

                if (slotDateTime != null && slotDateTime.getTime() - System.currentTimeMillis() < 15 * 60 * 1000) {
                    Animation pulse = AnimationUtils.loadAnimation(context, R.anim.pulse);
                    holder.tvBadge.startAnimation(pulse);
                } else {
                    holder.tvBadge.clearAnimation();
                }
            } catch (Exception e) {
                holder.tvBadge.clearAnimation();
            }
        } else {
            holder.tvBadge.setVisibility(View.GONE);
            holder.tvBadge.clearAnimation();
        }


        // 📍 Mode lecture ou annulation
        if (slot.isEngaged()) {
            if (showCancelButton) {
                holder.btnEngager.setText("❌ Annuler");
                holder.btnEngager.setEnabled(true);
                holder.btnEngager.setAlpha(1f);
            } else {
                holder.btnEngager.setText("✅ Engagé");
                holder.btnEngager.setEnabled(false);
                holder.btnEngager.setAlpha(0.6f);

                // 💥 Petite animation cool
                holder.btnEngager.setScaleX(0.8f);
                holder.btnEngager.setScaleY(0.8f);
                holder.btnEngager.setAlpha(0f);
                holder.btnEngager.animate()
                        .alpha(0.6f)
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(350)
                        .setInterpolator(new OvershootInterpolator())
                        .start();
            }
        } else {
            holder.btnEngager.setText("Je m'engage");
            holder.btnEngager.setEnabled(!showCancelButton); // désactivé si en mode lecture
            holder.btnEngager.setAlpha(showCancelButton ? 0f : 1f);
            holder.btnEngager.setScaleX(1f);
            holder.btnEngager.setScaleY(1f);
        }

        // 🎯 Gérer clic selon mode
        holder.btnEngager.setOnClickListener(v -> {
            if (slot.isEngaged() && showCancelButton) {
                // 💬 Confirmation annulation
                new androidx.appcompat.app.AlertDialog.Builder(context)
                        .setTitle("Annuler l'engagement ?")
                        .setMessage("Souhaitez-vous vraiment vous désengager de ce créneau critique ?")
                        .setPositiveButton("Oui", (dialog, which) -> {
                            if (engageClickListener != null) {
                                engageClickListener.onEngage(slot);
                            }
                        })
                        .setNegativeButton("Non", null)
                        .show();
            } else if (!slot.isEngaged() && !showCancelButton) {
                // Engagement normal
                if (engageClickListener != null) {
                    engageClickListener.onEngage(slot);
                }
            }
        });

        // 🎬 Animation slide in
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom);
        holder.itemView.startAnimation(anim);
    }

    @Override
    public int getItemCount() {
        return slotList.size();
    }

    static class SlotViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvHour, tvConso, tvBadge;
        Button btnEngager;

        public SlotViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvSlotDate);
            tvHour = itemView.findViewById(R.id.tvSlotHour);
            tvConso = itemView.findViewById(R.id.tvSlotConso);
            tvBadge = itemView.findViewById(R.id.tvBadgeCritique);
            btnEngager = itemView.findViewById(R.id.btnEngager);
        }
    }
}