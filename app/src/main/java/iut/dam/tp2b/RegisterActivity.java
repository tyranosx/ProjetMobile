package iut.dam.tp2b;

import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etEmail, etPassword, etConfirmPassword, etPhoneNumber;
    private Spinner spinnerCountryCode;
    private Button btnRegister, btnFacebookLogin;
    private ImageButton btnBack; // Nouveau bouton retour
    private List<String> countryCodes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        spinnerCountryCode = findViewById(R.id.spinnerCountryCode);
        btnRegister = findViewById(R.id.btnRegister);
        btnFacebookLogin = findViewById(R.id.btnFacebookLogin);
        btnBack = findViewById(R.id.btnBack); // Gestion du bouton retour

        // Bouton retour pour revenir à LoginActivity
        btnBack.setOnClickListener(v -> finish());

        // Initialisation de la liste des codes pays avec le hint
        countryCodes = new ArrayList<>();
        countryCodes.add("Sélectionnez votre code pays"); // Hint
        countryCodes.add("+33 France");
        countryCodes.add("+1 USA");
        countryCodes.add("+44 UK");
        countryCodes.add("+49 Deutschland");
        countryCodes.add("+39 Italy");
        countryCodes.add("+34 Spain");
        countryCodes.add("+81 Japan");
        countryCodes.add("+82 South Korea");
        countryCodes.add("+86 China");
        countryCodes.add("+91 India");

        // Création et configuration de l'adaptateur personnalisé
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, countryCodes) {

            @Override
            public boolean isEnabled(int position) {
                return position != 0; // Désactive le hint
            }

            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(ContextCompat.getColor(getContext(), android.R.color.darker_gray)); // Hint en gris
                } else {
                    tv.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black)); // Autres éléments en noir
                }
                return view;
            }
        };

        spinnerCountryCode.setAdapter(adapter);
        spinnerCountryCode.setSelection(0); // Par défaut sur le hint

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
        int selectedPosition = spinnerCountryCode.getSelectedItemPosition();

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

        if (selectedPosition == 0) {
            Toast.makeText(this, "Veuillez sélectionner votre indicatif téléphonique", Toast.LENGTH_SHORT).show();
            return;
        }

        String countryCode = countryCodes.get(selectedPosition).split(" ")[0];
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