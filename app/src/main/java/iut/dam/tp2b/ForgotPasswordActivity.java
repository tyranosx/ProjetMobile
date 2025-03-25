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

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmail;
    private Button btnSend, btnBackToLogin;
    private ImageButton btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etEmail = findViewById(R.id.etEmail);
        btnSend = findViewById(R.id.btnSend);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());
        btnSend.setOnClickListener(v -> handlePasswordReset());
        btnBackToLogin.setOnClickListener(v -> navigateToLogin());
    }

    private void handlePasswordReset() {
        String email = etEmail.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Veuillez entrer une adresse e-mail valide", Toast.LENGTH_SHORT).show();
            return;
        }

        // Simulation de l'envoi du lien de réinitialisation (ajoute ici ton code d'envoi réel si besoin)
        Toast.makeText(this, "Un lien de réinitialisation a été envoyé à : " + email, Toast.LENGTH_LONG).show();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
