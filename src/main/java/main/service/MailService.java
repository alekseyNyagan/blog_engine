package main.service;

import jakarta.mail.MessagingException;

public interface MailService {
    void sendRestoreEmail(String email, String domainName, String hash) throws MessagingException;
}
