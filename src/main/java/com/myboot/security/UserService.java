package com.myboot.security;

import com.myboot.exceptions.CustomUserIsCreatedException;
import com.myboot.exceptions.CustomUserNotFoundException;
import com.myboot.repository.UserSecurityRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger LOGGER = LogManager.getLogger(UserService.class);
    private final UserSecurityRepository userSecurityRepository;

    public UserSecurity create(UserSecurity userSecurity) throws CustomUserIsCreatedException {
        if (userSecurityRepository.existsByUserNameAndEmail(userSecurity.getUsername(), userSecurity.getEmail())) {
            throw new CustomUserIsCreatedException("User already exists!");
        }
        return userSecurityRepository.save(userSecurity);
    }

    public UserSecurity getUserByName(String userName) throws CustomUserNotFoundException {
        return userSecurityRepository.findByUserName(userName).
                orElseThrow(() -> new CustomUserNotFoundException("User not found!"));
    }

    public void delete(Long id) throws CustomUserNotFoundException {
        if (!userSecurityRepository.existsById(id)) {
            throw new CustomUserNotFoundException("User not found!");
        }
        userSecurityRepository.deleteById(id);
    }

    public UserDetailsService getUserDetailsService(){
        return username -> {
            try {
                return getUserByName(username);
            } catch (CustomUserNotFoundException e) {
                LOGGER.error(e.getMessage());
                return null;
            }
        };
    }
}
