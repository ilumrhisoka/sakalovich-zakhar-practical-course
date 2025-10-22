package inno.project.authservice.controller;

import inno.project.authservice.model.dto.AuthenticationRequest;
import inno.project.authservice.model.dto.JwtResponse;
import inno.project.authservice.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public JwtResponse login(@Valid @RequestBody AuthenticationRequest request){
        return authenticationService.authenticate(request);
    }
}
