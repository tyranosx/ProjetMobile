package iut.dam.tp2b;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Adapter pour afficher une liste d'équipements avec checkbox dans un RecyclerView
public class EquipmentAdapter extends RecyclerView.Adapter<EquipmentAdapter.EquipmentViewHolder> {

    private Context context;
    private List<Equipment> equipmentList;
    private Runnable onSelectionChanged; // Callback déclenché à chaque sélection/déselection

    // Constructeur
    public EquipmentAdapter(Context context, List<Equipment> equipmentList, Runnable onSelectionChanged) {
        this.context = context;
        this.equipmentList = equipmentList;
        this.onSelectionChanged = onSelectionChanged;
    }

    @NonNull
    @Override
    public EquipmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate de l'item XML (equipment_item.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.equipment_item, parent, false);
        return new EquipmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EquipmentViewHolder holder, int position) {
        Equipment equipment = equipmentList.get(position);

        // Affiche les infos de l'équipement
        holder.tvEqName.setText(equipment.getName());
        holder.tvEqRef.setText("Réf : " + equipment.getReference());
        holder.tvWattage.setText(equipment.getWattage() + " W");

        // Configure la checkbox
        holder.cbEquipment.setText(equipment.getName());

        // Supprime tout ancien listener pour éviter les effets de bord
        holder.cbEquipment.setOnCheckedChangeListener(null);

        // Définit l’état de la checkbox selon le modèle
        holder.cbEquipment.setChecked(equipment.isSelected());

        // Listener sur la checkbox : met à jour le modèle et notifie le parent via callback
        holder.cbEquipment.setOnCheckedChangeListener((buttonView, isChecked) -> {
            equipment.setSelected(isChecked);
            if (onSelectionChanged != null) onSelectionChanged.run(); // MAJ UI externe (ex: wattage total)
        });
    }

    @Override
    public int getItemCount() {
        return equipmentList.size(); // Nombre total d’éléments dans la liste
    }

    // ViewHolder = référence aux vues d'un item (optimisation RecyclerView)
    static class EquipmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvEqName, tvEqRef, tvWattage;
        CheckBox cbEquipment;

        public EquipmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEqName = itemView.findViewById(R.id.tvEqName);
            tvEqRef = itemView.findViewById(R.id.tvEqRef);
            tvWattage = itemView.findViewById(R.id.tvWattage);
            cbEquipment = itemView.findViewById(R.id.cbEquipment);
        }
    }
}