package iut.dam.tp2b;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

public class HabitatAdapter extends ArrayAdapter<Habitat> {

    private final Context context;  // Remplacé Activity par Context
    private final int resource;
    private final List<Habitat> habitatList;

    public HabitatAdapter(Context context, int resource, List<Habitat> habitatList) {
        super(context, resource, habitatList);
        this.context = context;
        this.resource = resource;
        this.habitatList = habitatList;
    }

    @Override
    public @NonNull View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View layout = convertView != null ? convertView : LayoutInflater.from(context).inflate(resource, parent, false);

        TextView tvResidentName = layout.findViewById(R.id.tvResidentName);
        TextView tvAppliances = layout.findViewById(R.id.tvAppliances);
        TextView tvFloor = layout.findViewById(R.id.tvFloor);
        TextView tvArea = layout.findViewById(R.id.tvArea);
        LinearLayout iconContainer = layout.findViewById(R.id.iconContainer);

        Habitat habitat = habitatList.get(position);

        tvResidentName.setText(habitat.getResidentName());
        tvAppliances.setText(context.getString(R.string.appliances_text, habitat.getAppliances()));
        tvFloor.setText(context.getString(R.string.floor_text, habitat.getFloor()));
        tvArea.setText(context.getString(R.string.area_text, habitat.getArea()));

        // Ajout dynamique des icônes
        iconContainer.removeAllViews();
        for (int iconRes : habitat.getEquipmentIcons()) {
            ImageView icon = new ImageView(context);
            icon.setImageResource(iconRes);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(50, 50);
            params.setMargins(5, 0, 5, 0);
            icon.setLayoutParams(params);
            iconContainer.addView(icon);
        }

        return layout;
    }
}
