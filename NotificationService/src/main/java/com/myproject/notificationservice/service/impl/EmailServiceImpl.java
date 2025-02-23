package com.myproject.notificationservice.service.impl;

import com.myproject.notificationservice.dto.TransactionNotificationDTO;
import com.myproject.notificationservice.service.EmailService;
import com.myproject.notificationservice.service.strategy.EmailStrategyFactory;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Miroslav Kološnjaji
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final EmailStrategyFactory emailStrategyFactory;

    @Override
    public void sendEmail(String to, String subject, String body) throws MessagingException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);

        javaMailSender.send(message);
    }

    @Override
    public void prepareEmail(Object object) {

        try {

            Map<String, String> detailsMap = emailStrategyFactory.prepareEmailTemplate(object);

            sendEmail(detailsMap.get("email"), detailsMap.get("subject"), detailsMap.get("htmlBody"));

        } catch (MessagingException e) {
            log.error("Failed to send a email: {}", e.getMessage());
        }

    }


}
