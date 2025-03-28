package iut.dam.tp2b;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.koushikdutta.ion.Ion;
import android.net.Uri;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private ImageButton btnTogglePassword;
    private Button btnLogin, btnRegister, btnFacebookLogin, btnEditProfile;
    private TextView tvForgotPassword;

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🔐 Si utilisateur déjà connecté -> skip Login
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        if (userId != -1) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etEmail.setHint("E-mail");
        etEmail.setHintTextColor(getResources().getColor(android.R.color.darker_gray));
        etEmail.setTextColor(getResources().getColor(android.R.color.black));
        etEmail.setBackground(ContextCompat.getDrawable(this, R.drawable.input_background_white));

        etPassword = findViewById(R.id.etPassword);
        etPassword.setHint("Mot de passe (8 caractères minimum, 1 majuscule, 1 chiffre, 1 caractère spécial)");
        etPassword.setHintTextColor(getResources().getColor(android.R.color.darker_gray));
        etPassword.setTextColor(getResources().getColor(android.R.color.black));
        etPassword.setBackground(ContextCompat.getDrawable(this, R.drawable.input_background_white));
        etPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock_lock, 0, 0, 0);

        btnTogglePassword = findViewById(R.id.btnTogglePassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnFacebookLogin = findViewById(R.id.btnFacebookLogin);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        btnTogglePassword.setOnClickListener(v -> togglePasswordVisibility());

        btnLogin.setOnClickListener(v -> handleLogin());
        btnRegister.setOnClickListener(v -> handleRegister());
        btnFacebookLogin.setOnClickListener(v -> handleFacebookLogin());
        tvForgotPassword.setOnClickListener(v -> handleForgotPassword());
        btnEditProfile.setOnClickListener(v -> handleEditProfile());
    }

    private void handleEditProfile() {
        Intent intent = new Intent(LoginActivity.this, EditProfileActivity.class);
        startActivity(intent);
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            btnTogglePassword.setImageResource(R.drawable.ic_eye_closed);
        } else {
            etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            btnTogglePassword.setImageResource(R.drawable.ic_eye_open);
        }
        etPassword.setSelection(etPassword.getText().length());
        isPasswordVisible = !isPasswordVisible;
    }

    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Veuillez respecter le format de l'e-mail", Toast.LENGTH_SHORT).show();
            return;
        }

        String apiUrl = "http://10.0.2.2/powerhome_server/login.php"
                + "?email=" + Uri.encode(email)
                + "&password=" + Uri.encode(password);

        Ion.with(this)
                .load("GET", apiUrl)
                .asJsonObject()
                .setCallback((e, result) -> {
                    if (e != null) {
                        Toast.makeText(this, "Erreur réseau : " + e.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (result != null && result.has("status")) {
                        String status = result.get("status").getAsString();

                        if ("success".equals(status)) {
                            String token = result.get("token").getAsString();
                            String expire = result.get("expired_at").getAsString();
                            int userId = result.has("user_id") && !result.get("user_id").isJsonNull()
                                    ? result.get("user_id").getAsInt()
                                    : -1;

                            if (userId == -1) {
                                Toast.makeText(this, "Erreur : utilisateur non trouvé", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            int habitatId = result.has("habitat_id") && !result.get("habitat_id").isJsonNull()
                                    ? result.get("habitat_id").getAsInt()
                                    : -1;

                            SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt("user_id", userId); // ✅ sauvegarde
                            editor.putString("token", token);
                            editor.putString("expired_at", expire);
                            editor.putInt("habitat_id", habitatId);
                            editor.apply();

                            Toast.makeText(this, "✅ Connexion réussie !", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("token", token);
                            startActivity(intent);
                            finish();
                        } else {
                            String msg = result.has("message") ? result.get("message").getAsString() : "Erreur inconnue";
                            Toast.makeText(this, "❌ " + msg, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(this, "❗ Erreur de réponse du serveur", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void handleFacebookLogin() {
        Toast.makeText(this, "Connexion via Facebook en cours...", Toast.LENGTH_SHORT).show();
        // Ici tu peux intégrer ton SDK Facebook ou rediriger vers ton flow d'authentification
    }

    private void handleForgotPassword() {
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }
}