package com.handloom.marketplace.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterForm {

    @NotBlank(message = "Please enter your name")
    @Size(max = 100, message = "Name is too long")
    private String name;

    @NotBlank(message = "Please enter your email")
    @Email(message = "Enter a valid email address")
    private String email;

    @NotBlank(message = "Please choose a password")
    @Size(min = 6, max = 72, message = "Password must be 6 to 72 characters")
    private String password;

    @Pattern(regexp = "^$|^[0-9+\\-\\s]{7,15}$", message = "Enter a valid phone number")
    private String phone;

    @Size(max = 500, message = "Address is too long")
    private String address;

    private String role = "CUSTOMER";

    @Size(max = 100, message = "Region is too long")
    private String region;

    @Size(max = 2000, message = "Story is too long (max 2000 characters)")
    private String bio;

    // <accessors>

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
