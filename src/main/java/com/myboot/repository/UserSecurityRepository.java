package com.myboot.repository;

import com.myboot.security.UserSecurity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSecurityRepository extends CrudRepository<UserSecurity, Long> {
    Optional<UserSecurity> findByUserName(String name);
    boolean existsByUserNameAndEmail(String name, String email);
    boolean existsById(long id);
}
