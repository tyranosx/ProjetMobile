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

// Adaptateur personnalisé pour afficher les résidents dans un RecyclerView
public class ResidentAdapter extends RecyclerView.Adapter<ResidentAdapter.ResidentViewHolder> {

    private Context context;
    private List<Resident> residentList;

    // Constructeur
    public ResidentAdapter(Context context, List<Resident> residentList) {
        this.context = context;
        this.residentList = residentList;
    }

    // Crée la vue pour un item (appelé à la création)
    @NonNull
    @Override
    public ResidentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.resident_item, parent, false);
        return new ResidentViewHolder(view);
    }

    // Remplit les données dans la vue (appelé pour chaque item affiché)
    @Override
    public void onBindViewHolder(@NonNull ResidentViewHolder holder, int position) {
        Resident resident = residentList.get(position);

        Log.d("ResidentAdapter", "Affichage de : " + resident.getFullName());

        // Affiche le nom complet
        holder.tvName.setText(resident.getFullName());

        // Affiche le nombre d’équipements avec pluriel si nécessaire
        holder.tvEquipmentCount.setText(resident.getEquipmentCount() +
                (resident.getEquipmentCount() > 1 ? " équipements" : " équipement"));

        // Affiche l'étage
        holder.tvEtage.setText("ÉTAGE " + resident.getEtage());

        // 🔄 Réinitialise toutes les icônes à invisible (évite les bugs de recyclage)
        holder.ivAspirateur.setVisibility(View.GONE);
        holder.ivClimatiseur.setVisibility(View.GONE);
        holder.ivFer.setVisibility(View.GONE);
        holder.ivMachine.setVisibility(View.GONE);

        // 🔍 Affiche uniquement les icônes correspondant aux équipements du résident
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

    // Nombre d'items à afficher
    @Override
    public int getItemCount() {
        return residentList.size();
    }

    // Classe interne pour représenter chaque vue d’un résident
    static class ResidentViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEquipmentCount, tvEtage;
        ImageView ivAspirateur, ivClimatiseur, ivFer, ivMachine;

        public ResidentViewHolder(@NonNull View itemView) {
            super(itemView);

            // Liaison avec les vues du layout resident_item.xml
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