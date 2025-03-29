package iut.dam.tp2b;

// Représente le corps JSON envoyé lors de l’inscription d’un utilisateur
public class RegistrationRequest {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String phone;
    private String country_code;

    // Constructeur avec tous les champs nécessaires
    public RegistrationRequest(String firstname, String lastname, String email, String password, String phone, String country_code) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.country_code = country_code;
    }
}
