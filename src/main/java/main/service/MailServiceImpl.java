package main.service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service
public class MailServiceImpl implements MailService {
    private static final String RESTORE_PASSWORD_TEXT_CHARSET = "UTF-8";
    private static final String RESTORE_PASSWORD_TEXT_SUBTYPE = "html";
    private static final String RESTORE_PASSWORD_MESSAGE_PATTERN = """
            <HTML><body> Для восстановления пароля перейдите по ссылке <a href="http://{0}/login/change-password/{1}">
            /login/change-password/{2}</a></body></HTML>
            """;

    private final JavaMailSender javaMailSender;

    @Autowired
    public MailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendRestoreEmail(String email, String domainName, String hash) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.addRecipients(Message.RecipientType.TO, email);
        message.setText(
                MessageFormat.format(RESTORE_PASSWORD_MESSAGE_PATTERN, domainName, hash, hash),
                RESTORE_PASSWORD_TEXT_CHARSET,
                RESTORE_PASSWORD_TEXT_SUBTYPE);
        javaMailSender.send(message);
    }
}
