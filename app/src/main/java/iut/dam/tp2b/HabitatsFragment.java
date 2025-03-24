package iut.dam.tp2b;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HabitatsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_habitats, container, false);

        TextView tvUserInfo = view.findViewById(R.id.tvUserInfo);
        ListView listView = view.findViewById(R.id.listView);

        // Simuler un utilisateur connecté
        String email = "test@gmail.com";  // Remplace par la récupération des infos si besoin
        tvUserInfo.setText(getString(R.string.welcome_message, email));

        // Liste des habitats avec équipements
        List<Habitat> habitatList = new ArrayList<>();
        habitatList.add(new Habitat("Gaëtan Leclair", 1, 50.0, 4, Arrays.asList(R.drawable.ic_aspirateur, R.drawable.ic_machine_a_laver)));
        habitatList.add(new Habitat("Cédric Boudet", 1, 30.0, 1, Collections.singletonList(R.drawable.ic_fer_a_repasser)));
        habitatList.add(new Habitat("Gaylord Thibodeaux", 2, 40.0, 2, Arrays.asList(R.drawable.ic_climatiseur, R.drawable.ic_machine_a_laver)));
        habitatList.add(new Habitat("Adam Jacquinot", 3, 60.0, 3, Arrays.asList(R.drawable.ic_aspirateur, R.drawable.ic_fer_a_repasser)));
        habitatList.add(new Habitat("Abel Fresnel", 3, 35.0, 1, Collections.singletonList(R.drawable.ic_machine_a_laver)));

        // Associer l'adaptateur
        HabitatAdapter adapter = new HabitatAdapter(requireContext(), R.layout.item_habitat, habitatList);
        listView.setAdapter(adapter);

        return view;
    }
}
