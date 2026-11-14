package com.learnig_spring_boot.curso_api.user.repository;

import com.learnig_spring_boot.curso_api.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>{
    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    Optional<User> findByIdAndDeletedAtIsNull(Long id);

    Page<User> findAllByDeletedAtIsNull(Pageable pageable);

    boolean existsByEmailAndDeletedAtIsNull(String email);

    boolean existsByEmailAndIdNotAndDeletedAtIsNull(String email, Long id);
}
