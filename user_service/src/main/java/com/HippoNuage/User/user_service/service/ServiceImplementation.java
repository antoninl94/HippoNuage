package com.HippoNuage.User.user_service.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.HippoNuage.User.user_service.config.JWTConfig;
import com.HippoNuage.User.user_service.dto.AuthResponseDto;
import com.HippoNuage.User.user_service.dto.LoginDto;
import com.HippoNuage.User.user_service.dto.RegisterDto;
import com.HippoNuage.User.user_service.dto.UserUpdateDto;
import com.HippoNuage.User.user_service.model.Tokens;
import com.HippoNuage.User.user_service.model.User;
import com.HippoNuage.User.user_service.repository.TokenRepository;
import com.HippoNuage.User.user_service.repository.UserRepository;

@Service
public class ServiceImplementation implements UserFacade {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTConfig jwtConfig;
    private final TokenImplementation tokenImplementation;
    private final TokenRepository tokenRepository;
    private final EmailValidationService emailValidationService;

    @Autowired  // Dependency Injection -> Here We tell Spring to inject automatically a dependency
    public ServiceImplementation(UserRepository userRepository, PasswordEncoder passwordEncoder, JWTConfig jwtConfig, TokenImplementation tokenImplementation, TokenRepository tokenRepository, EmailValidationService emailValidationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtConfig = jwtConfig;
        this.tokenImplementation = tokenImplementation;
        this.tokenRepository = tokenRepository;
        this.emailValidationService = emailValidationService;
    }

    @Override
    public ResponseEntity<?> login(LoginDto loginDto) {
        // Vérifie que le champs "email" est bien complété
        if (loginDto.getEmail() == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Email is required.");
        }
        // Vérifie que le champs "password" est bien complété
        if (loginDto.getPassword() == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Password is required.");
        }
        // Vérifie que l'utilisateur existe bien dans la BDD
        Optional<User> userOptional = this.userRepository.findByEmail(loginDto.getEmail());
        if (userOptional.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Utilisateur non trouvé");
        }
        // Si le mail et le password correspondent -> Utilisateur connecté
        User user = userOptional.get();
        if (this.passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            String token = this.jwtConfig.generateToken(user);
            AuthResponseDto response = new AuthResponseDto("Bonjour Sire!", token);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Mot de passe, CHEVALIER!");
    }

    @Override
    public ResponseEntity<?> register(RegisterDto registerDto) {
        // Vérifie que le champs "email" est bien complété
        if (registerDto.getEmail() == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Email is required.");
        }
        // Vérifie que le mail n'est pas déjà existant dans la BDD
        // -> Si le mail existe déja : Erreur
        // -> Si ce n'est pas le cas l'utilisateur est créé
        if (this.userRepository.findByEmail(registerDto.getEmail()).isEmpty()) {
            User user = new User();
            user.setEmail(registerDto.getEmail());
            user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
            this.userRepository.save(user);
            String token = this.jwtConfig.generateToken(user);
            this.emailValidationService.SendValidationEmail(user);
            AuthResponseDto response = new AuthResponseDto("Chevalier adoubé! Pour Hipponuage ! Valide ton email pour accéder aux fonctionnalités !", token);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Chevalier déja existant !");
        }
    }

    @Override
    public ResponseEntity<?> update(UserUpdateDto updateDto, String token) throws Exception{
        // Vérifie qu'au moins un champs à été modifié
        if ((updateDto.getNewEmail() == null) || (updateDto.getNewPassword() == null)) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Tu dois mettre quelque chose à jour, chevalier");
        }
        // Teste la la validité du token
        boolean JwtCheck = this.jwtConfig.validateToken(token, this.userRepository);
        if (!JwtCheck) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Token non valide, sale gueux");
        }
        // Récupère l'ID utilisateur
        String userId = this.jwtConfig.extractUserId(token);
        if (userId == null) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Token Invalide");
        }
        // Trouve l'utilisateur grâce à l'ID
        Optional<User> user = this.userRepository.findById(UUID.fromString(userId));
        if (user.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Utilisateur non trouvé");
        }
        // Update les infos utilisateur
        User finaluser = user.get();
        if (!finaluser.getValidatedEmail()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Valide ton email avant d’entrer en chevalerie !");
        }
        if (!updateDto.getNewEmail().equals(finaluser.getEmail())){
            finaluser.setEmail(updateDto.getNewEmail());
            this.emailValidationService.SendValidationEmail(finaluser);
            finaluser.setValidatedEmail(false);
        }
        finaluser.setPassword(this.passwordEncoder.encode(updateDto.getNewPassword()));
        this.userRepository.save(finaluser);
        return ResponseEntity.ok("Votre profil a été modifié!");
    }

    @Override
    public ResponseEntity<?> disconnect(String token) {
        
        //Check si le token passé commence par 'Bearer tokenvalue' oudirectement 'tokenvalue'
        String TokenChecked = token.startsWith("Bearer ") ? token.substring(7) : token;
        
        //Ici on trouve l'objet token grace au hash
        Optional<Tokens> TokenEntity = this.tokenRepository.findByTokenHash(this.tokenImplementation.hashToken(TokenChecked));
       
        //Verification de l'objet retourné
        if (TokenEntity.isEmpty()) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Votre jeton n'est plus à la mode du moyen age!");
        }
        //Operation de blacklist + Sauvegarde en base de données 
        Tokens FinalTokenToBlacklist = TokenEntity.get();
        this.tokenImplementation.blacklistToken(FinalTokenToBlacklist);
        return ResponseEntity
            .status(HttpStatus.ACCEPTED)
            .body("Vous avez bien été déconnecté, Sire");
    }

    @Override
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token){
        boolean verified = emailValidationService.verifyEmailToken(token);

        if (verified) {
            return ResponseEntity.ok("Bien joué Chevalier!");
        } else {
            return ResponseEntity.badRequest().body("Tu n'es qu'un gueux.");
        }
    }

    @Override
     public ResponseEntity<?> resendEmail(String token){
        //JWT Valide ?
        boolean JwtCheck = this.jwtConfig.validateToken(token, this.userRepository);
        if (!JwtCheck) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Token non valide, sale gueux");
       }
       //ID utilisateur
       String userId = this.jwtConfig.extractUserId(token);
        if (userId == null) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Token Invalide");
        }
        Optional<User> user = this.userRepository.findById(UUID.fromString(userId));
        if (user.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Utilisateur non trouvé");
        }
        User finaluser = user.get();
        if (!finaluser.getValidatedEmail()){
            this.emailValidationService.SendValidationEmail(finaluser);
            return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body("Un email t'a été adressé, encore une fois ! Ne te fourvoie plus !");
        }
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body("Votre adresse à déjà fait ses preuves au combat !");
    }
}