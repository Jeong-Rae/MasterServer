package org.codequistify.master.domain.authentication.mail;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.authentication.domain.EmailVerificationType;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
@RequiredArgsConstructor
public class VerificationMailSender {
    private static final String FROM = "pol.dev@gmail.com";
    private static final String TEMPLATE_NAME = "email-verification";

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    public void send(VerificationMailRequest request) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            message.addRecipients(Message.RecipientType.TO, request.to().value());
            message.setFrom(FROM);
            message.setSubject(subject(request.type()));
            message.setText(content(request), "utf-8", "html");
            javaMailSender.send(message);
        } catch (MessagingException exception) {
            throw new MailPreparationException(exception);
        }
    }

    private String content(VerificationMailRequest request) {
        Context context = new Context();
        context.setVariables(variables(request));
        return templateEngine.process(TEMPLATE_NAME, context);
    }

    private Map<String, Object> variables(VerificationMailRequest request) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("authCode", request.code().value());
        variables.put("email", request.to().urlEncoded());
        variables.put("type", request.type().name());
        return variables;
    }

    private String subject(EmailVerificationType type) {
        return switch (type) {
            case REGISTRATION -> "인증 메일 : POL 회원가입";
            case PASSWORD_RESET -> "인증 메일 : 비밀번호 초기화";
        };
    }
}
