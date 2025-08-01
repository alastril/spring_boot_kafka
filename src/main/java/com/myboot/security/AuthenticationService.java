package com.myboot.security;

import com.myboot.exceptions.CustomUserIsCreatedException;
import com.myboot.exceptions.CustomUserNotFoundException;
import com.myboot.security.dto.JwtAuthenticationResponseDTO;
import com.myboot.security.dto.SignInRequestDTO;
import com.myboot.security.dto.SignUpRequestDTO;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Data
@RequiredArgsConstructor
@Service
public class AuthenticationService {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationResponseDTO signUp(SignUpRequestDTO request) throws CustomUserIsCreatedException {

        UserSecurity user = UserSecurity.builder()
                .userName(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        userService.create(user);
        String jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponseDTO(jwt);
    }

    public JwtAuthenticationResponseDTO signIn(SignInRequestDTO request) throws CustomUserNotFoundException {
        //check if user signed up
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));

        return new JwtAuthenticationResponseDTO(
                jwtService.generateToken(
                        userService.getUserByName(request.getUsername())
                ));
    }
}
