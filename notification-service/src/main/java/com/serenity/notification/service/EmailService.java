package com.serenity.notification.service;

import com.serenity.notification.entity.EmailTemplate;
import com.serenity.notification.entity.enums.NotificationChannel;
import com.serenity.notification.entity.enums.NotificationStatus;
import com.serenity.notification.entity.enums.NotificationType;
import com.serenity.notification.entity.enums.RecipientType;
import com.serenity.notification.repository.EmailTemplateRepository;
import com.serenity.notification.repository.SmtpConfigurationRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Properties;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SmtpConfigurationRepository smtpConfigurationRepository;
    private final EmailTemplateRepository emailTemplateRepository;
    private final NotificationLogService notificationLogService;

    /**
     * Send welcome email to a new member.
     */
    public void sendWelcomeEmail(String to, String prenom, String numeroMembre) {
        log.info("Sending welcome email to: {} for member: {}", to, numeroMembre);
        try {
            Map<String, String> variables = Map.of(
                    "prenom", prenom != null ? prenom : "",
                    "numeroMembre", numeroMembre != null ? numeroMembre : ""
            );
            sendTemplatedEmail(to, "welcome", variables);
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", to, e);
            notificationLogService.logFailure(
                    NotificationType.EMAIL, null, RecipientType.MEMBRE,
                    NotificationChannel.EMAIL, "Bienvenue sur Serenity",
                    "Welcome email to: " + to, e.getMessage()
            );
        }
    }

    /**
     * Send a simple email.
     */
    public void sendEmail(String to, String subject, String body) {
        log.info("Sending email to: {} with subject: {}", to, subject);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);

            notificationLogService.logSuccess(
                    NotificationType.EMAIL, null, RecipientType.MEMBRE,
                    NotificationChannel.EMAIL, subject, body, to
            );
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
            notificationLogService.logFailure(
                    NotificationType.EMAIL, null, RecipientType.MEMBRE,
                    NotificationChannel.EMAIL, subject, body, e.getMessage()
            );
        }
    }

    /**
     * Send an HTML email.
     */
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        log.info("Sending HTML email to: {} with subject: {}", to, subject);
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(mimeMessage);

            notificationLogService.logSuccess(
                    NotificationType.EMAIL, null, RecipientType.MEMBRE,
                    NotificationChannel.EMAIL, subject, htmlBody, to
            );
            log.info("HTML email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send HTML email to: {}", to, e);
            notificationLogService.logFailure(
                    NotificationType.EMAIL, null, RecipientType.MEMBRE,
                    NotificationChannel.EMAIL, subject, htmlBody, e.getMessage()
            );
        }
    }

    /**
     * Send a templated email using the email_templates table.
     * Replaces variables in the template with the provided values.
     */
    public void sendTemplatedEmail(String to, String templateName, Map<String, String> variables) {
        log.info("Sending templated email to: {} using template: {}", to, templateName);
        try {
            EmailTemplate template = emailTemplateRepository.findByNomAndActifTrue(templateName)
                    .orElseThrow(() -> new RuntimeException("Email template not found: " + templateName));

            String subject = replaceVariables(template.getSujet(), variables);
            String body = replaceVariables(template.getCorps(), variables);

            sendHtmlEmail(to, subject, body);
        } catch (Exception e) {
            log.error("Failed to send templated email to: {} with template: {}", to, templateName, e);
            notificationLogService.logFailure(
                    NotificationType.EMAIL, null, RecipientType.MEMBRE,
                    NotificationChannel.EMAIL, "Template: " + templateName,
                    "Templated email to: " + to, e.getMessage()
            );
        }
    }

    /**
     * Send email to a specific recipient with logging against a known recipient ID.
     */
    public void sendEmailToRecipient(String to, String subject, String body, UUID recipientId, RecipientType recipientType) {
        log.info("Sending email to: {} (recipientId: {})", to, recipientId);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);

            notificationLogService.logSuccess(
                    NotificationType.EMAIL, recipientId, recipientType,
                    NotificationChannel.EMAIL, subject, body, to
            );
            log.info("Email sent successfully to: {} for recipient: {}", to, recipientId);
        } catch (Exception e) {
            log.error("Failed to send email to: {} for recipient: {}", to, recipientId, e);
            notificationLogService.logFailure(
                    NotificationType.EMAIL, recipientId, recipientType,
                    NotificationChannel.EMAIL, subject, body, e.getMessage()
            );
        }
    }

    /**
     * Send HTML email to a specific recipient with logging against a known recipient ID.
     */
    public void sendHtmlEmailToRecipient(String to, String subject, String htmlBody, UUID recipientId, RecipientType recipientType) {
        log.info("Sending HTML email to: {} (recipientId: {})", to, recipientId);
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(mimeMessage);

            notificationLogService.logSuccess(
                    NotificationType.EMAIL, recipientId, recipientType,
                    NotificationChannel.EMAIL, subject, htmlBody, to
            );
            log.info("HTML email sent successfully to: {} for recipient: {}", to, recipientId);
        } catch (MessagingException e) {
            log.error("Failed to send HTML email to: {} for recipient: {}", to, recipientId, e);
            notificationLogService.logFailure(
                    NotificationType.EMAIL, recipientId, recipientType,
                    NotificationChannel.EMAIL, subject, htmlBody, e.getMessage()
            );
        }
    }

    /**
     * Send templated email to a specific recipient with logging against a known recipient ID.
     */
    public void sendTemplatedEmailToRecipient(String to, String templateName, Map<String, String> variables,
                                               UUID recipientId, RecipientType recipientType) {
        log.info("Sending templated email to: {} using template: {} (recipientId: {})", to, templateName, recipientId);
        try {
            EmailTemplate template = emailTemplateRepository.findByNomAndActifTrue(templateName)
                    .orElseThrow(() -> new RuntimeException("Email template not found: " + templateName));

            String subject = replaceVariables(template.getSujet(), variables);
            String body = replaceVariables(template.getCorps(), variables);

            sendHtmlEmailToRecipient(to, subject, body, recipientId, recipientType);
        } catch (Exception e) {
            log.error("Failed to send templated email to: {} with template: {} for recipient: {}", to, templateName, recipientId, e);
            notificationLogService.logFailure(
                    NotificationType.EMAIL, recipientId, recipientType,
                    NotificationChannel.EMAIL, "Template: " + templateName,
                    "Templated email to: " + to, e.getMessage()
            );
        }
    }

    /**
     * Replace {{variable}} placeholders in the template with actual values.
     */
    private String replaceVariables(String template, Map<String, String> variables) {
        if (template == null || variables == null) {
            return template;
        }
        String result = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue() != null ? entry.getValue() : "");
        }
        return result;
    }
}
