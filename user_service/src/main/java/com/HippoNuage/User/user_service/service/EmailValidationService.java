package com.HippoNuage.User.user_service.service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.HippoNuage.User.user_service.model.EmailValidationToken;
import com.HippoNuage.User.user_service.model.User;
import com.HippoNuage.User.user_service.repository.EmailValidationRepository;
import com.HippoNuage.User.user_service.repository.UserRepository;

@Service
public class EmailValidationService{

    private final EmailValidationRepository emailValidationRepository;
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    @Value("${app.url}") // base URL front ou back pour le lien de validation
    private String appUrl;
    
    @Autowired
    public EmailValidationService(EmailValidationRepository emailValidationRepository, JavaMailSender mailSender, UserRepository userRepository){
        this.emailValidationRepository = emailValidationRepository;
        this.mailSender = mailSender;
        this.userRepository = userRepository;
    }

    public void SendValidationEmail(User user){
        String token = UUID.randomUUID().toString();
        EmailValidationToken validationtoken = new EmailValidationToken();
        validationtoken.setUser(user);
        validationtoken.setToken(token);
        validationtoken.setUsed(false);
        this.emailValidationRepository.save(validationtoken);

        String validationLink = appUrl + "user/verify-email?token=" + token;
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setFrom("contact-hippoland@hipponuage.com");
        mailMessage.setSubject("Pensez à vérifier votre e-mail");
        mailMessage.setText("Merci d'avoir rejoins HippoNuage ! Pour valider" +
         " ton adresse et porter le haume de nos contrées, cliques sur ce lien : " + validationLink);
        
        this.mailSender.send(mailMessage);
    }

    public boolean verifyEmailToken(String token) {
    Optional<EmailValidationToken> optionalToken = emailValidationRepository.findByToken(token);
    // Token introuvable
    if (optionalToken.isEmpty()) {
        return false;
    }

    EmailValidationToken validationToken = optionalToken.get();
    // Token non valide
    if (validationToken.getUsed()) {
        return false;
    }

    if (validationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
        return false;
    }

    // Token ok -> On valide donc l'email utilisateur et marque le token comme utilisé
    User user = validationToken.getUser();
    user.setValidatedEmail(true);
    validationToken.setUsed(true);

    // On persiste les données
    emailValidationRepository.save(validationToken);
    userRepository.save(user);
    

    return true;
}
}