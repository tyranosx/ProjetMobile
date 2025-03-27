package iut.dam.tp2b;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.google.android.material.navigation.NavigationView;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        // Toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Menu listener
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        // Fragment par défaut
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new HabitatFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_habitat);
        }
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;

        int itemId = item.getItemId();

        if (itemId == R.id.nav_habitat) {
            selectedFragment = new HabitatFragment();
        } else if (itemId == R.id.nav_mon_habitat) {
            selectedFragment = new MonHabitatFragment();
        } else if (itemId == R.id.nav_notifications) {
            selectedFragment = new MesNotificationsFragment();
        } else if (itemId == R.id.nav_preferences) {
            selectedFragment = new ParametresFragment();
        } else if (itemId == R.id.nav_choisir_habitat) {
            selectedFragment = new ChoisirHabitatFragment();
        } else if (itemId == R.id.nav_ajouter_equipement) {
            selectedFragment = new AjouterEquipementFragment();
        } else if (itemId == R.id.nav_apropos) {
            showAboutDialog();
            return true;
        }

        if (selectedFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, selectedFragment)
                    .commit();
        }
        drawerLayout.closeDrawers();
        return true;
    }

    private void showAboutDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("À propos")
                .setMessage("PowerHome\nVersion 1.0\n\nApplication de gestion énergétique.")
                .setPositiveButton("OK", null)
                .show();
    }
}
