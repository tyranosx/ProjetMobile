package iut.dam.tp2b;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

// Fragment permettant à l'utilisateur de se déconnecter de l'application
public class DeconnexionFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Création de la vue à partir du layout XML
        View view = inflater.inflate(R.layout.fragment_deconnexion, container, false);

        // Récupération des vues : message + bouton
        TextView tvMessage = view.findViewById(R.id.tvLogoutMessage);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        // Gestion du clic sur le bouton de déconnexion
        btnLogout.setOnClickListener(v -> {
            // 🔐 Suppression des données de session stockées localement
            SharedPreferences prefs = requireActivity().getSharedPreferences("user_session", getContext().MODE_PRIVATE);
            prefs.edit().clear().apply(); // ✅ efface la session

            // Redirige vers l'écran de connexion (en vidant la backstack)
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // évite le retour en arrière
            startActivity(intent);
        });

        return view;
    }
}