package iut.dam.tp2b;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.gson.*;
import com.koushikdutta.ion.Ion;

import java.text.SimpleDateFormat;
import java.util.*;

public class ParametresFragment extends Fragment {

    private TextView txtSolde;
    private LinearLayout layoutHistorique;
    private SharedPreferences prefs;

    private static class EcoCoin {
        int amount;
        String reason;
        String createdAt;

        public EcoCoin(int amount, String reason, String createdAt) {
            this.amount = amount;
            this.reason = reason;
            this.createdAt = createdAt;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parametres, container, false);

        txtSolde = view.findViewById(R.id.txtSoldeEcoCoins);
        layoutHistorique = view.findViewById(R.id.layoutHistoriqueEcoCoins);

        prefs = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId != -1) {
            fetchEcoCoins(userId);
        } else {
            txtSolde.setText("Utilisateur non identifié");
        }

        return view;
    }

    private void fetchEcoCoins(int userId) {
        String url = "http://10.0.2.2/powerhome_server/get_my_ecocoins.php?user_id=" + userId;

        Ion.with(this)
                .load("GET", url)
                .asJsonObject()
                .setCallback((e, result) -> {
                    if (result != null && result.has("solde") && result.has("transactions")) {
                        int solde = result.get("solde").getAsInt();
                        txtSolde.setText("💰 Solde éco-coins : " + solde);

                        layoutHistorique.removeAllViews();
                        JsonArray transactions = result.get("transactions").getAsJsonArray();
                        for (JsonElement elem : transactions) {
                            JsonObject obj = elem.getAsJsonObject();
                            int amount = obj.get("amount").getAsInt();
                            String reason = obj.get("reason").isJsonNull() ? "Pas de raison précisée" : obj.get("reason").getAsString();
                            String createdAt = obj.get("created_at").getAsString();
                            ajouterLigneHistorique(new EcoCoin(amount, reason, createdAt));
                        }
                    } else {
                        txtSolde.setText("Impossible de charger le solde");
                        Toast.makeText(getContext(), "Erreur lors de la récupération des éco-coins", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void ajouterLigneHistorique(EcoCoin ecoCoin) {
        TextView tv = new TextView(getContext());
        String prefix = ecoCoin.amount >= 0 ? "🟢 +" : "🔴 ";
        tv.setText(prefix + ecoCoin.amount + " eco-coin(s)\n📝 " + ecoCoin.reason + "\n📅 " + ecoCoin.createdAt);
        tv.setTextSize(14);
        tv.setPadding(20, 20, 20, 20);
        tv.setBackgroundResource(R.drawable.bg_white_rounded);
        tv.setTextColor(getResources().getColor(android.R.color.black));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 10, 0, 10);
        tv.setLayoutParams(params);
        layoutHistorique.addView(tv);
    }
}
