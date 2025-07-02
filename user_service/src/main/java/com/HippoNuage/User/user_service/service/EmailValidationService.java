package com.HippoNuage.User.user_service.service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.HippoNuage.User.user_service.model.EmailValidationToken;
import com.HippoNuage.User.user_service.model.User;
import com.HippoNuage.User.user_service.repository.EmailValidationRepository;
import com.HippoNuage.User.user_service.repository.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

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

    public void SendValidationEmail(User user) throws MessagingException {
        String token = UUID.randomUUID().toString();
        EmailValidationToken validationtoken = new EmailValidationToken();
        validationtoken.setUser(user);
        validationtoken.setToken(token);
        validationtoken.setUsed(false);
        this.emailValidationRepository.save(validationtoken);

        String validationLink = appUrl + "user/verify-email?token=" + token;

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(user.getEmail());
        helper.setFrom("contact-hippoland@hipponuage.com");
        helper.setSubject("Pensez à vérifier votre e-mail");

        String htmlContent = "<html>" +
            "<body>" +
            "<p>Merci d'avoir rejoint HippoNuage !</p>" +
            "<p>Pour valider ton adresse et porter le haume de nos contrées, clique sur ce lien :</p>" +
            "<a href='" + validationLink + "'>Valider mon email</a>" +
            "</body>" +
            "</html>";

        helper.setText(htmlContent, true); // true indique que c'est du HTML
        this.mailSender.send(message);
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