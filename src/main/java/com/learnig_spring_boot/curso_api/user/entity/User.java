package com.learnig_spring_boot.curso_api.user.entity;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "users")
public class User {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length=100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    protected User() {}

    public User(String name, String email){
        this.name = name;
        this.email = email;
    }

    @PrePersist
    protected void OnCreate(){
        this.createdAt = Instant.now();
    }

    public Long getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    public Instant getCreatedAt(){
        return createdAt;
    }

    public Instant getDeletedAt(){
        return deletedAt;
    }

    /**
     * Marks the user as deleted.
     * */
    public void softDelete(){
        this.deletedAt = Instant.now();
    }

    /**
     * Updates the user's editable information
     *
     * @param name the new user name
     * @param email the new email address
     * */
    public void updateDetails(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
