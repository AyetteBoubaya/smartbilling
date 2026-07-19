package com.smartbilling.smartbilling.auth.service.serviceImpl;

import com.smartbilling.smartbilling.auth.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendVerificationEmail(String toEmail, String token) {
        String link = frontendUrl + "/verify-email?token=" + token;
        String subject = "SmartBilling — Vérifiez votre adresse email";
        String body = """
                Bonjour,
                
                Merci de vous être inscrit sur SmartBilling.
                
                Cliquez sur le lien ci-dessous pour vérifier votre adresse email :
                %s
                
                Ce lien est valable 24 heures.
                
                Si vous n'avez pas créé de compte, ignorez cet email.
                
                L'équipe SmartBilling
                """.formatted(link);

        send(toEmail, subject, body);
    }

    @Override
    public void sendPasswordResetLink(String toEmail, String token) {
        String link = frontendUrl + "/reset-password?token=" + token;
        String subject = "SmartBilling — Réinitialisation de votre mot de passe";
        String body = """
                Bonjour,
                
                Vous avez demandé la réinitialisation de votre mot de passe.
                
                Cliquez sur le lien ci-dessous (valable 15 minutes) :
                %s
                
                Si vous n'avez pas fait cette demande, ignorez cet email.
                Votre mot de passe ne sera pas modifié.
                
                L'équipe SmartBilling
                """.formatted(link);

        send(toEmail, subject, body);
    }

    @Override
    public void sendPasswordResetOtp(String toEmail, String otp) {
        String subject = "SmartBilling — Code de réinitialisation";
        String body = """
                Bonjour,
                
                Votre code de réinitialisation de mot de passe est :
                
                        %s
                
                Ce code est valable 15 minutes.
                Ne le partagez avec personne.
                
                Si vous n'avez pas fait cette demande, ignorez cet email.
                
                L'équipe SmartBilling
                """.formatted(otp);

        send(toEmail, subject, body);
    }

    // ---------------------------------------------------------------
    private void send(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email envoyé à {}", to);
        } catch (Exception e) {
            log.error("Échec envoi email à {} : {}", to, e.getMessage());
            // On ne propage pas l'exception pour ne pas bloquer le flux principal
        }
    }
}