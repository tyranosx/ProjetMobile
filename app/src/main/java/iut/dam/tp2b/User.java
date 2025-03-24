package iut.dam.tp2b;

import java.util.Date;

public class User {

    public int id;
    public String firstName;
    public String lastName;
    public String email;
    public String password;
    public String token;
    public Date expiredAt;
    public Habitat habitat;

    public User() {
    }

    public User(int id, String firstName, String lastName, String email, String password, String token, Date expiredAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.token = token;
        this.expiredAt = expiredAt;
    }
}
