package iut.dam.tp2b;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.*;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import android.widget.Toast;

// Activité principale qui gère la navigation entre les fragments via un Drawer
public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private static final int PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 🔧 Mise en place de la toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 📂 Mise en place du Drawer (menu latéral)
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        // 🔀 Lien entre le Drawer et la Toolbar (hamburger menu)
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // 🎯 Gestion du clic sur les éléments du menu
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        // 🧱 Fragment par défaut affiché au lancement
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new HabitatFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_habitat);
        }

        // 🔔 Demande la permission pour les notifications (Android 13+)
        askNotificationPermission();
    }

    // 🔄 Appelé quand l'utilisateur sélectionne un item du menu
    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;

        int itemId = item.getItemId();

        // 🎯 On associe chaque item du menu à un fragment
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
            showAboutDialog(); // 🧾 boîte de dialogue "À propos"
            return true;
        } else if (itemId == R.id.nav_deconnexion) {
            selectedFragment = new DeconnexionFragment();
        } else if (itemId == R.id.nav_mes_engagements) {
            selectedFragment = new MesEngagementsFragment();
        } else if (itemId == R.id.nav_ajouter_time_slot) {
            selectedFragment = new AjouterTimeSlotFragment();
        } else if (itemId == R.id.nav_ajouter_usage) {
            selectedFragment = new AjouterUsageFragment();
        } else if (itemId == R.id.nav_calendrier) {
            selectedFragment = new CalendrierFragment();
        }

        // 🔁 Remplacement du fragment courant par le nouveau
        if (selectedFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, selectedFragment)
                    .commit();
        }

        // Ferme le drawer après sélection
        drawerLayout.closeDrawers();
        return true;
    }

    // 📄 Affiche une boîte de dialogue "À propos"
    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("À propos")
                .setMessage("PowerHome\nVersion 1.0\n\nApplication de gestion énergétique.")
                .setPositiveButton("OK", null)
                .show();
    }

    // 📲 Demande la permission pour les notifications (uniquement Android 13+)
    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        PERMISSION_REQUEST_CODE
                );
            }
        }
    }

    // ✅ Réponse utilisateur à la demande de permission
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Notif", "✅ Permission notifications accordée");
            } else {
                Toast.makeText(this, "🔕 Notifications désactivées", Toast.LENGTH_SHORT).show();
            }
        }
    }
}