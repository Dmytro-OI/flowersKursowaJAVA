package logging;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.*;

public class LoggerConfig {
    private static final Logger logger = Logger.getLogger(LoggerConfig.class.getName());
    private static final String EMAIL_FROM = "dmytro.oi22@gmail.com";
    private static final String EMAIL_PASSWORD = System.getenv("EMAIL_PASSWORD");
    private static final String EMAIL_TO = "dmytro.hrynchshyn@gmail.com";
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;

    public static synchronized Logger getLogger() {
        if (logger.getHandlers().length == 0) {
            try {
                FileHandler fileHandler = new FileHandler("flower_app.log", 10 * 1024 * 1024, 5, true);
                fileHandler.setFormatter(new SimpleFormatter());
                logger.addHandler(fileHandler);

                logger.setLevel(Level.ALL);
            } catch (IOException e) {
                System.err.println("Помилка ініціалізації логера: " + e.getMessage());
            }
        }
        return logger;
    }

    public static void sendCriticalErrorEmail(String errorMessage) {
        logger.severe("Критична помилка: " + errorMessage);

        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(EMAIL_TO));
            message.setSubject("Критична помилка в додатку");
            message.setText("Сталася критична помилка:\n\n" + errorMessage);

            Transport.send(message);
            System.out.println("Критична помилка надіслана на e-mail.");
        } catch (MessagingException e) {
            logger.severe("Не вдалося надіслати e-mail: " + e.getMessage());
        }
    }
}