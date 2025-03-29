package iut.dam.tp2b;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

// Activité permettant à l'utilisateur de demander une réinitialisation de mot de passe
public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmail;
    private Button btnSend, btnBackToLogin;
    private ImageButton btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // 🔎 Récupération des vues
        etEmail = findViewById(R.id.etEmail);
        btnSend = findViewById(R.id.btnSend);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);
        btnBack = findViewById(R.id.btnBack);

        // ⬅️ Bouton de retour (flèche) : ferme l’activité
        btnBack.setOnClickListener(v -> finish());

        // 📤 Envoi du lien de réinitialisation (simulation ici)
        btnSend.setOnClickListener(v -> handlePasswordReset());

        // 🔁 Retour vers l'écran de connexion
        btnBackToLogin.setOnClickListener(v -> navigateToLogin());
    }

    // Gère la demande de réinitialisation
    private void handlePasswordReset() {
        String email = etEmail.getText().toString().trim();

        // Vérifie que l’email est valide
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Veuillez entrer une adresse e-mail valide", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Un lien de réinitialisation a été envoyé à : " + email, Toast.LENGTH_LONG).show();
    }

    // Redirige vers l'écran de login
    private void navigateToLogin() {
        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Termine cette activité pour éviter retour arrière
    }
}
