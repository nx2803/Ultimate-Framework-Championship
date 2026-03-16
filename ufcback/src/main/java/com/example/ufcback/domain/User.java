package com.example.ufcback.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users", schema = "ufc")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role; // 예: ROLE_ADMIN
}
