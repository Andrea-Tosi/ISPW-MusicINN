package org.musicinn.musicinn.util;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;

public class EmailSender {
    private static final Dotenv DOTENV = Dotenv.load();
    private static final String SENDER_EMAIL = DOTENV.get("EMAIL_APP");
    private static final String API_KEY = DOTENV.get("SENDGRIP_API_KEY");

    public void sendEmail(String to, String subject, String body) {
        Email sender = new Email(SENDER_EMAIL);
        Email recipient = new Email(to);
        Content content = new Content("text/plain", "Il codice per confermare il tuo indirizzo email è:\n" + body);
        Mail mail = new Mail(sender, subject, recipient, content);

        SendGrid sg = new SendGrid(API_KEY);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
