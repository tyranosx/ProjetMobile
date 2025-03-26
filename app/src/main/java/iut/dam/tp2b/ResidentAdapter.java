package iut.dam.tp2b;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ResidentAdapter extends RecyclerView.Adapter<ResidentAdapter.ResidentViewHolder> {

    private Context context;
    private List<Resident> residentList;

    public ResidentAdapter(Context context, List<Resident> residentList) {
        this.context = context;
        this.residentList = residentList;
    }

    @NonNull
    @Override
    public ResidentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.resident_item, parent, false);
        return new ResidentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResidentViewHolder holder, int position) {
        Resident resident = residentList.get(position);

        Log.d("ResidentAdapter", "Affichage de : " + resident.getFullName());

        holder.tvName.setText(resident.getFullName());
        holder.tvEquipmentCount.setText(resident.getEquipmentCount() + (resident.getEquipmentCount() > 1 ? " équipements" : " équipement"));
        holder.tvEtage.setText("ÉTAGE " + resident.getEtage());

        // Réinitialise les icônes à chaque fois
        holder.ivAspirateur.setVisibility(View.GONE);
        holder.ivClimatiseur.setVisibility(View.GONE);
        holder.ivFer.setVisibility(View.GONE);
        holder.ivMachine.setVisibility(View.GONE);

        // Active uniquement les équipements possédés
        for (String equip : resident.getEquipments()) {
            switch (equip.toLowerCase()) {
                case "aspirateur":
                    holder.ivAspirateur.setVisibility(View.VISIBLE);
                    break;
                case "climatiseur":
                    holder.ivClimatiseur.setVisibility(View.VISIBLE);
                    break;
                case "fer":
                    holder.ivFer.setVisibility(View.VISIBLE);
                    break;
                case "machine_a_laver":
                    holder.ivMachine.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return residentList.size();
    }

    static class ResidentViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEquipmentCount, tvEtage;
        ImageView ivAspirateur, ivClimatiseur, ivFer, ivMachine;

        public ResidentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvResidentName);
            tvEquipmentCount = itemView.findViewById(R.id.tvEquipmentCount);
            tvEtage = itemView.findViewById(R.id.tvEtageBadge);
            ivAspirateur = itemView.findViewById(R.id.ivAspirateur);
            ivClimatiseur = itemView.findViewById(R.id.ivClimatiseur);
            ivFer = itemView.findViewById(R.id.ivFer);
            ivMachine = itemView.findViewById(R.id.ivMachine);
        }
    }
}
