package com.Inverse.Authentication.Server.registration;

import org.hibernate.annotations.NaturalId;

public record RegistrationRequest(
         String firstName,
         String lastName,
         String email,
         String username,
         String password,
         String role) {
}
