package com.bapunmalik.voting_system.models;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String fatherName;
    private String sex;
    private String dob;
    private String houseNumber;
    private String villageName;
    private String postOffice;
    private String policeStation;
    private String district;
    private String state;
    private String mobileNumber;
    @Column(unique = true)
    private String email;
    private String password;
    @Column(unique = true,name = "voterId")
    private String voterId;
 
    @Column(unique=true)
    private Long aadhar;
    @Column(nullable = false)
    private boolean approved=false;

    private boolean voted=false;
    private String role;
    // Getters and Setters

    private String photoFileName; // Add this
    private String signatureFileName; // Add this
}
