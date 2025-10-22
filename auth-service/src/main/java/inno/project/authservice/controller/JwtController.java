package inno.project.authservice.controller;

import inno.project.authservice.model.dto.JwtResponse;
import inno.project.authservice.service.AuthenticationService;
import inno.project.authservice.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jwt")
@RequiredArgsConstructor
public class JwtController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    @PostMapping("/refresh")
    public JwtResponse refresh(@RequestBody String refreshToken){
        return authenticationService.refreshToken(refreshToken);
    }

    @GetMapping("/validate")
    public ResponseEntity<Void> validate(@RequestParam("token") String accessToken){
        if(jwtService.isTokenValid(accessToken)){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
}