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

public class EquipmentAdapter extends RecyclerView.Adapter<EquipmentAdapter.EquipmentViewHolder> {

    private Context context;
    private List<Equipment> equipmentList;
    private Runnable onSelectionChanged; // Callback pour MAJ total wattage

    public EquipmentAdapter(Context context, List<Equipment> equipmentList, Runnable onSelectionChanged) {
        this.context = context;
        this.equipmentList = equipmentList;
        this.onSelectionChanged = onSelectionChanged;
    }

    @NonNull
    @Override
    public EquipmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.equipment_item, parent, false);
        return new EquipmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EquipmentViewHolder holder, int position) {
        Equipment equipment = equipmentList.get(position);

        holder.tvEqName.setText(equipment.getName());
        holder.tvEqRef.setText("Réf : " + equipment.getReference());
        holder.tvWattage.setText(equipment.getWattage() + " W");

        holder.cbEquipment.setText(equipment.getName());
        holder.cbEquipment.setChecked(equipment.isSelected());

        holder.cbEquipment.setOnCheckedChangeListener(null); // évite les callbacks fantômes
        holder.cbEquipment.setChecked(equipment.isSelected());

        holder.cbEquipment.setOnCheckedChangeListener((buttonView, isChecked) -> {
            equipment.setSelected(isChecked);
            if (onSelectionChanged != null) onSelectionChanged.run();
        });
    }

    @Override
    public int getItemCount() {
        return equipmentList.size();
    }

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
