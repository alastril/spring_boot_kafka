package com.myboot.web.controllers;

import com.myboot.exceptions.CustomUserIsCreatedException;
import com.myboot.exceptions.CustomUserNotFoundException;
import com.myboot.security.AuthenticationService;
import com.myboot.security.UserService;
import com.myboot.security.dto.JwtAuthenticationResponseDTO;
import com.myboot.security.dto.SignInRequestDTO;
import com.myboot.security.dto.SignUpRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class SecurityController {
    final AuthenticationService authenticationService;
    final UserService userService;

    @PostMapping(value = "/sign_up")
    public JwtAuthenticationResponseDTO signUp(@RequestBody SignUpRequestDTO request) throws CustomUserIsCreatedException {
        return authenticationService.signUp(request);
    }

    @PostMapping("/sign_in")
    public JwtAuthenticationResponseDTO signIn(@RequestBody SignInRequestDTO request) throws CustomUserNotFoundException {
        return authenticationService.signIn(request);
    }

    @DeleteMapping("/delete_user/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) throws CustomUserNotFoundException {
        userService.delete(id);
        return new ResponseEntity<>("Deleted", HttpStatus.OK);
    }
}