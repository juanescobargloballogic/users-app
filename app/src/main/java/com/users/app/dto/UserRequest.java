package com.users.app.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import java.util.List;

public class UserRequest {

    private String name;

    @NotBlank
    @Pattern(
        regexp = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$",
        message = "Invalid email format. Expected format: aaaaaaa@domain.tld"
    )
    private String email;

    @NotBlank
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=(?:.*\\d.*){2})[a-zA-Z\\d]{8,12}$",
        message = "Password must have one uppercase, two digits, and be 8-12 characters"
    )
    private String password;

    private List<PhoneDTO> phones;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<PhoneDTO> getPhones() {
        return phones;
    }

    public void setPhones(List<PhoneDTO> phones) {
        this.phones = phones;
    }
}