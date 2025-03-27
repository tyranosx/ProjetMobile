package iut.dam.tp2b;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.koushikdutta.ion.Ion;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class HabitatFragment extends Fragment {

    private RecyclerView recyclerView;
    private ResidentAdapter residentAdapter;
    private List<Resident> residentList;

    private int habitatId = -1;
    private TextView tvTotalConsumption;

    public HabitatFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_habitat, container, false);

        tvTotalConsumption = view.findViewById(R.id.tvTotalConsumption);

        recyclerView = view.findViewById(R.id.recyclerViewResidents);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        residentList = new ArrayList<>();
        residentAdapter = new ResidentAdapter(getContext(), residentList);
        recyclerView.setAdapter(residentAdapter);

        SharedPreferences prefs = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        habitatId = prefs.getInt("habitat_id", -1);
        Log.d("HabitatFragment", "Habitat ID utilisateur connecté = " + habitatId);

        fetchResidentsFromServer();

        return view;
    }

    private void fetchResidentsFromServer() {
        String url = "http://10.0.2.2/powerhome_server/get_residents.php";

        Ion.with(this)
                .load("GET", url)
                .asJsonArray()
                .setCallback((e, result) -> {
                    if (e != null) {
                        Log.e("HabitatFragment", "Erreur de requête : " + e.getMessage());
                        return;
                    }

                    residentList.clear();
                    int totalWattage = 0;

                    for (int i = 0; i < result.size(); i++) {
                        JsonObject resJson = result.get(i).getAsJsonObject();

                        String firstname = resJson.get("firstname").getAsString();
                        String lastname = resJson.get("lastname").getAsString();
                        int etage = resJson.get("etage").getAsInt();

                        JsonArray eqArray = resJson.getAsJsonArray("equipments");
                        List<String> equipments = new ArrayList<>();
                        for (int j = 0; j < eqArray.size(); j++) {
                            JsonObject eq = eqArray.get(j).getAsJsonObject();
                            String name = eq.get("name").getAsString();
                            int watt = eq.get("wattage").getAsInt();
                            totalWattage += watt;
                            equipments.add(name);
                        }

                        residentList.add(new Resident(firstname, lastname, etage, equipments));
                    }

                    tvTotalConsumption.setText("Conso. totale : " + totalWattage + " W");
                    residentAdapter.notifyDataSetChanged();
                });
    }
}
