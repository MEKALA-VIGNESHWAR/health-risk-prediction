package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UserDTO - Data Transfer Object for User
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String id;
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Long createdDate;
    private Long updatedDate;

}
