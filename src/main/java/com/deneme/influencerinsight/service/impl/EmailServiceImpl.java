package com.deneme.influencerinsight.service.impl;

import com.deneme.influencerinsight.exception.EmailSendException;
import com.deneme.influencerinsight.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Value("${app.email.verification-base-url}")
    private String verificationBaseUrl;

    @Override
    public void sendVerificationEmail(String to, String token) {
        String subject = "E-posta Doğrulama";
        String verificationUrl = verificationBaseUrl + "?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
        String content = """
                <p>Merhaba,</p>
                <p>Hesabınızı doğrulamak için aşağıdaki bağlantıya tıklayın:</p>
                <p><a href="%s">E-postamı Doğrula</a></p>
                <p>Bu e-postayı siz göndermediyseniz dikkate almayınız.</p>
                """.formatted(verificationUrl);

        sendHtmlEmail(to, subject, content);
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailSendException("E-posta gönderimi başarısız", e);
        }
    }
}
