package iut.dam.tp2b;

import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etEmail, etPassword, etConfirmPassword, etPhoneNumber;
    private Spinner spinnerCountryCode;
    private Button btnRegister, btnFacebookLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFirstName = findViewById(R.id.etFirstName);
        etFirstName.setTextColor(getResources().getColor(android.R.color.black));

        etLastName = findViewById(R.id.etLastName);
        etLastName.setTextColor(getResources().getColor(android.R.color.black));

        etEmail = findViewById(R.id.etEmail);
        etEmail.setTextColor(getResources().getColor(android.R.color.black));

        etPassword = findViewById(R.id.etPassword);
        etPassword.setTextColor(getResources().getColor(android.R.color.black));

        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etConfirmPassword.setTextColor(getResources().getColor(android.R.color.black));

        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etPhoneNumber.setTextColor(getResources().getColor(android.R.color.black));

        spinnerCountryCode = findViewById(R.id.spinnerCountryCode);
        btnRegister = findViewById(R.id.btnRegister);
        btnFacebookLogin = findViewById(R.id.btnFacebookLogin);

        // Remplir le spinner avec les indicatifs téléphoniques
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.country_codes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountryCode.setAdapter(adapter);

        btnRegister.setOnClickListener(v -> handleRegister());
        btnFacebookLogin.setOnClickListener(v -> handleFacebookLogin());
    }

    private void handleRegister() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String countryCode = spinnerCountryCode.getSelectedItem().toString().split(" ")[0];

        if (firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(this, "Veuillez renseigner votre prénom et votre nom", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Veuillez respecter le format de l'e-mail", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidPassword(password)) {
            Toast.makeText(this, "Le mot de passe doit contenir au moins 8 caractères, une majuscule, un chiffre et un caractère spécial", Toast.LENGTH_LONG).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Veuillez renseigner votre numéro de téléphone", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Inscription réussie!\nNuméro: " + countryCode + " " + phoneNumber, Toast.LENGTH_LONG).show();
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[0-9].*") &&
                password.matches(".*[!@#$%^&*(),.?\":{}|<>].*");
    }

    private void handleFacebookLogin() {
        Toast.makeText(this, "Connexion via Facebook en cours...", Toast.LENGTH_SHORT).show();
    }
}