package com.learnig_spring_boot.curso_api.user.repository;

import com.learnig_spring_boot.curso_api.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>{
    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);
}
