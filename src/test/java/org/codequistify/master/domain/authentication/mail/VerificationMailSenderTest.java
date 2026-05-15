package org.codequistify.master.domain.authentication.mail;

import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.codequistify.master.domain.authentication.domain.EmailVerificationType;
import org.codequistify.master.domain.authentication.vo.VerificationCode;
import org.codequistify.master.shared.vo.EmailAddress;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerificationMailSenderTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private TemplateEngine templateEngine;

    @Test
    void 회원가입_인증메일_요청이면_템플릿을_렌더링하고_MimeMessage를_전송한다() throws Exception {
        MimeMessage message = new MimeMessage(Session.getInstance(new Properties()));
        when(javaMailSender.createMimeMessage()).thenReturn(message);
        when(templateEngine.process(eq("email-verification"), any(Context.class))).thenReturn("<html>verify</html>");
        VerificationMailSender sender = new VerificationMailSender(javaMailSender, templateEngine);

        sender.send(new VerificationMailRequest(
                new EmailAddress("pol@example.com"),
                new VerificationCode("code-123"),
                EmailVerificationType.REGISTRATION));

        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine).process(eq("email-verification"), contextCaptor.capture());
        verify(javaMailSender).send(message);
        assertThat(message.getSubject()).isEqualTo("인증 메일 : POL 회원가입");
        assertThat(message.getRecipients(Message.RecipientType.TO)[0].toString()).isEqualTo("pol@example.com");
        assertThat(message.getFrom()[0].toString()).isEqualTo("pol.dev@gmail.com");
        assertThat(message.getContent().toString()).contains("verify");
        assertThat(contextCaptor.getValue().getVariable("authCode")).isEqualTo("code-123");
        assertThat(contextCaptor.getValue().getVariable("email")).isEqualTo("pol%40example.com");
        assertThat(contextCaptor.getValue().getVariable("type")).isEqualTo("REGISTRATION");
    }
}
