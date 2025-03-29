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

// Adapter personnalisé pour afficher un objet Habitat dans une ListView ou Spinner
public class HabitatAdapter extends ArrayAdapter<Habitat> {

    private final Context context;
    private final int resource; // ID du layout XML à utiliser pour chaque item
    private final List<Habitat> habitatList;

    // Constructeur
    public HabitatAdapter(Context context, int resource, List<Habitat> habitatList) {
        super(context, resource, habitatList);
        this.context = context;
        this.resource = resource;
        this.habitatList = habitatList;
    }

    // Retourne la vue affichée pour chaque élément de la liste
    @Override
    public @NonNull View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Réutilise une vue si possible, sinon on "inflate" un nouveau layout
        View layout = convertView != null ? convertView : LayoutInflater.from(context).inflate(resource, parent, false);

        // Récupération des éléments de la vue
        TextView tvResidentName = layout.findViewById(R.id.tvResidentName);
        TextView tvAppliances = layout.findViewById(R.id.tvAppliances);
        TextView tvFloor = layout.findViewById(R.id.tvFloor);
        TextView tvArea = layout.findViewById(R.id.tvArea);
        LinearLayout iconContainer = layout.findViewById(R.id.iconContainer); // Conteneur pour les icônes

        // Données de l'habitat courant
        Habitat habitat = habitatList.get(position);

        // Affichage des infos texte
        tvResidentName.setText(habitat.getResidentName());
        tvAppliances.setText(context.getString(R.string.appliances_text, habitat.getAppliances()));
        tvFloor.setText(context.getString(R.string.floor_text, habitat.getFloor()));
        tvArea.setText(context.getString(R.string.area_text, habitat.getArea()));

        // Affichage dynamique des icônes des équipements
        iconContainer.removeAllViews(); // Nettoie avant de recréer
        for (int iconRes : habitat.getEquipmentIcons()) {
            ImageView icon = new ImageView(context);
            icon.setImageResource(iconRes);

            // Définition de la taille et marges des icônes
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(50, 50);
            params.setMargins(5, 0, 5, 0);
            icon.setLayoutParams(params);

            iconContainer.addView(icon); // Ajout dans le conteneur
        }

        return layout;
    }
}