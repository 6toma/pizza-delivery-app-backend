package nl.tudelft.sem.template.store.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Properties;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;

class EmailNotificationServiceTest {

    private JavaMailSender emailSender;
    private EmailNotificationService notificationService;

    @BeforeEach
    void setup() {
        emailSender = mock(JavaMailSender.class);
        notificationService = new EmailNotificationService(emailSender, "notifications@test.com");
    }

    @Test
    @SneakyThrows
    void notifyOrder() {
        var mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
        notificationService.notifyOrder("test@email.com");
        verify(emailSender, times(1)).send(mimeMessage);
        assertThat(mimeMessage.getContent()).isEqualTo("Hello World!");
    }
}