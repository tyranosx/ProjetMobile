package iut.dam.tp2b;

import android.content.Intent;
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

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private ImageButton btnTogglePassword;
    private Button btnLogin, btnRegister, btnFacebookLogin;
    private TextView tvForgotPassword;

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        btnTogglePassword.setOnClickListener(v -> togglePasswordVisibility());

        btnLogin.setOnClickListener(v -> handleLogin());
        btnRegister.setOnClickListener(v -> handleRegister());
        btnFacebookLogin.setOnClickListener(v -> handleFacebookLogin());
        tvForgotPassword.setOnClickListener(v -> handleForgotPassword());
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

        if (!isValidPassword(password)) {
            Toast.makeText(this, "Le mot de passe doit contenir au moins 8 caractères, une majuscule, un chiffre et un caractère spécial", Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(this, "Connexion réussie!", Toast.LENGTH_SHORT).show();
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[0-9].*") &&
                password.matches(".*[!@#$%^&*(),.?\":{}|<>].*");
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