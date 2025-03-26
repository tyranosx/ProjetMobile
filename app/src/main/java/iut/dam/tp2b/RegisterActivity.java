package iut.dam.tp2b;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.koushikdutta.ion.Ion;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etEmail, etPassword, etConfirmPassword, etPhoneNumber;
    private Spinner spinnerCountryCode;
    private Button btnRegister, btnFacebookLogin;
    private ImageButton btnBack;
    private List<String> countryCodes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Liaison avec les éléments XML
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        spinnerCountryCode = findViewById(R.id.spinnerCountryCode);
        btnRegister = findViewById(R.id.btnRegister);
        btnFacebookLogin = findViewById(R.id.btnFacebookLogin);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish()); // Retour

        // Initialisation spinner pays
        countryCodes = new ArrayList<>();
        countryCodes.add("Sélectionnez votre code pays");
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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, countryCodes) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor(ContextCompat.getColor(getContext(),
                        position == 0 ? android.R.color.darker_gray : android.R.color.black));
                return view;
            }
        };

        spinnerCountryCode.setAdapter(adapter);
        spinnerCountryCode.setSelection(0);

        btnRegister.setOnClickListener(v -> handleRegister());
        btnFacebookLogin.setOnClickListener(v -> Toast.makeText(this, "Connexion Facebook pas encore dispo", Toast.LENGTH_SHORT).show());
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
            showToast("Veuillez renseigner votre prénom et nom");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Format email invalide");
            return;
        }
        if (!isValidPassword(password)) {
            showToast("Mot de passe trop faible (8+ caractères, majuscule, chiffre, caractère spécial)");
            return;
        }
        if (!password.equals(confirmPassword)) {
            showToast("Les mots de passe ne correspondent pas");
            return;
        }
        if (phoneNumber.isEmpty()) {
            showToast("Numéro de téléphone requis");
            return;
        }
        if (selectedPosition == 0) {
            showToast("Veuillez choisir un indicatif pays");
            return;
        }

        String countryCode = countryCodes.get(selectedPosition).split(" ")[0];
        sendRegistrationToServer(firstName, lastName, email, password, phoneNumber, countryCode);
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[0-9].*") &&
                password.matches(".*[!@#$%^&*(),.?\":{}|<>].*");
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private void sendRegistrationToServer(String firstName, String lastName, String email, String password, String phoneNumber, String countryCode) {
        String apiUrl = "http://192.168.13.94/powerhome_server/register.php";

        Log.d("RegisterActivity", "Appel API en cours...");

        RegistrationRequest request = new RegistrationRequest(firstName, lastName, email, password, phoneNumber, countryCode);

        Ion.with(this)
                .load("POST", apiUrl)
                .setHeader("Content-Type", "application/json")
                .setJsonPojoBody(request)
                .asJsonObject()
                .setCallback((e, result) -> {
                    if (e != null) {
                        Log.e("RegisterActivity", "Erreur réseau : " + e.getMessage());
                        showToast("Erreur réseau : " + e.getMessage());
                        return;
                    }

                    Log.d("RegisterActivity", "Réponse JSON : " + result);

                    if (result != null && "success".equals(result.get("status").getAsString())) {
                        showToast("✅ Inscription réussie !");
                        finish();
                    } else {
                        String message = result != null && result.has("message") ? result.get("message").getAsString() : "Erreur inconnue";
                        showToast("⚠️ " + message);
                    }
                });
    }

}