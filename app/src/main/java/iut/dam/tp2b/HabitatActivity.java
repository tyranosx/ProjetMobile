package iut.dam.tp2b;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;

public class HabitatActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habitat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new HabitatsFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_habitats);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null; // Ajout de la déclaration
        int id = item.getItemId();

        if (id == R.id.nav_habitats) {
            fragment = new HabitatsFragment();
        } else if (id == R.id.nav_mon_habitat) {
            fragment = new MonHabitatFragment();
        } else if (id == R.id.nav_mes_requetes) {
            fragment = new MesRequetesFragment();
        } else if (id == R.id.nav_parametres) {
            fragment = new ParametresFragment();
        } else if (id == R.id.nav_deconnexion) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment).commit();
        }

        drawerLayout.closeDrawers();
        return true;
    }
}
