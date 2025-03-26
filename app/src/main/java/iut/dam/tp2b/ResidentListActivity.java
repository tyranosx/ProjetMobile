package iut.dam.tp2b;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.koushikdutta.ion.Ion;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

public class ResidentListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ResidentAdapter residentAdapter;
    private List<Resident> residentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resident_list);

        recyclerView = findViewById(R.id.recyclerViewResidents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        residentList = new ArrayList<>();
        residentAdapter = new ResidentAdapter(this, residentList);
        recyclerView.setAdapter(residentAdapter);

        fetchResidentsFromServer();
    }

    private void fetchResidentsFromServer() {
        String url = "http://192.168.13.94/powerhome_server/get_residents.php";

        Ion.with(this)
                .load("GET", url)
                .asJsonArray()
                .setCallback((e, result) -> {
                    if (e != null) {
                        Log.e("ResidentList", "Erreur de requête : " + e.getMessage());
                        return;
                    }

                    residentList.clear();

                    for (int i = 0; i < result.size(); i++) {
                        JsonObject resJson = result.get(i).getAsJsonObject();

                        String firstname = resJson.get("firstname").getAsString();
                        String lastname = resJson.get("lastname").getAsString();
                        int etage = resJson.get("etage").getAsInt();

                        JsonArray eqArray = resJson.getAsJsonArray("equipments");
                        List<String> equipments = new ArrayList<>();
                        for (int j = 0; j < eqArray.size(); j++) {
                            equipments.add(eqArray.get(j).getAsString());
                        }

                        residentList.add(new Resident(firstname, lastname, etage, equipments));
                    }

                    residentAdapter.notifyDataSetChanged();
                });
    }
}