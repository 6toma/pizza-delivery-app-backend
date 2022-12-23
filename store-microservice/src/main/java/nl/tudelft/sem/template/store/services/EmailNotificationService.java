package nl.tudelft.sem.template.store.services;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import javax.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Service for sending email notifications, like when an order is placed.
 */
@Service
public class EmailNotificationService {

    private final String senderEmailAddress;
    private final JavaMailSender mailSender;

    /**
     * Creates a new EmailNotificationService.
     *
     * @param mailSender         The mail sender
     * @param senderEmailAddress The email address of the sender
     */
    public EmailNotificationService(@Autowired JavaMailSender mailSender,
                                    @Value("${spring.mail.username}") String senderEmailAddress
    ) {
        this.mailSender = mailSender;
        this.senderEmailAddress = senderEmailAddress;
    }

    /**
     * Sends an email notification indicating that an order has been placed to a specific recipient.
     *
     * @param emailAddress THe recipient email address
     * @throws IOException        Any errors reading the HTML template
     * @throws MessagingException Any errors with building and sending the mail message
     */
    public void notifyOrder(String emailAddress) throws IOException, MessagingException {
        var message = mailSender.createMimeMessage();
        var helper = new MimeMessageHelper(message, false);
        helper.setFrom(senderEmailAddress);
        helper.setTo(emailAddress);
        helper.setText(readOrderNotificationTemplate(), true);
        helper.setSubject("An order has been placed");

        mailSender.send(message);
    }

    /**
     * Sends an email notification indicating that an order has been cancelled.
     *
     * @param emailAddress THe recipient email address
     * @throws IOException        Any errors reading the HTML template
     * @throws MessagingException Any errors with building and sending the mail message
     */
    public void notifyOrderRemove(String emailAddress) throws IOException, MessagingException {
        var message = mailSender.createMimeMessage();
        var helper = new MimeMessageHelper(message, false);
        helper.setFrom(senderEmailAddress);
        helper.setTo(emailAddress);
        helper.setText(readOrderNotificationTemplateCancelOrder(), true);
        helper.setSubject("An order was cancelled");
        mailSender.send(message);
    }

    private String readOrderNotificationTemplate() throws IOException {
        try (var inputStream
                 = EmailNotificationService.class.getResourceAsStream("/order_notification.html")) {
            return new String(Objects.requireNonNull(inputStream).readAllBytes(), StandardCharsets.UTF_8);
        }
    }
    private String readOrderNotificationTemplateCancelOrder() throws IOException {
        try (var inputStream
                 = EmailNotificationService.class.getResourceAsStream("/order_notification_cancel.html")) {
            return new String(Objects.requireNonNull(inputStream).readAllBytes(), StandardCharsets.UTF_8);
        }
    }

}
