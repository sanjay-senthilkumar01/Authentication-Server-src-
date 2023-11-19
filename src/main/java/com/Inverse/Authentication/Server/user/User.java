package com.Inverse.Authentication.Server.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;
import org.springframework.boot.autoconfigure.web.WebProperties;

@Getter
@Setter
@Entity
@Table (name = "user_account")
@NoArgsConstructor
@AllArgsConstructor

public class User {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id ;
    private String firstName;
    private String lastName;
    @NaturalId(mutable = true)
    private String email;
    @NaturalId(mutable = true)
    private String username ;
    private String password;
    private String role;
    private boolean isEnabled = false;

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public  void  setisEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
}
