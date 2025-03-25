package iut.dam.tp2b;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
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

public class EditProfileActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etEmail, etPhoneNumber, etNewPassword, etConfirmPassword;
    private Spinner spinnerCountryCode;
    private Button btnSaveChanges, btnCancel;
    private ImageButton btnBack;
    private List<String> countryCodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        spinnerCountryCode = findViewById(R.id.spinnerCountryCode);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnCancel = findViewById(R.id.btnCancel);
        btnBack = findViewById(R.id.btnBack); // Bouton retour

        // Gestion du bouton retour
        btnBack.setOnClickListener(v -> finish());
        // Initialisation du Spinner avec hint
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
                return position != 0; // Désactive le hint
            }

            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(ContextCompat.getColor(getContext(), android.R.color.darker_gray));
                } else {
                    tv.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
                }
                return view;
            }
        };

        spinnerCountryCode.setAdapter(adapter);
        spinnerCountryCode.setSelection(0); // Par défaut sur le hint

        btnSaveChanges.setOnClickListener(v -> handleSaveChanges());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void handleSaveChanges() {
        Toast.makeText(this, "Profil mis à jour avec succès!", Toast.LENGTH_LONG).show();
        finish();
    }
}