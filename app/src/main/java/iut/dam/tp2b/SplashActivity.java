package iut.dam.tp2b;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

// 🎬 Écran de lancement affiché au démarrage de l’application
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); // 🔗 layout avec logo/animation (activity_splash.xml)

        // 🕒 Attente de 3 secondes avant de passer à LoginActivity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // ✅ Empêche de revenir à l'écran splash avec le bouton "retour"
        }, 3000); // 3000 ms = 3 secondes
    }
}