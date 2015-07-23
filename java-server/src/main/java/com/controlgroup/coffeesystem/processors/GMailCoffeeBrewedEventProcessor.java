package com.controlgroup.coffeesystem.processors;

import com.controlgroup.coffeesystem.configuration.PropertyFetcher;
import com.controlgroup.coffeesystem.events.CoffeeBrewedEvent;
import com.controlgroup.coffeesystem.generators.interfaces.EmailGenerator;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Created by timmattison on 12/29/14.
 */
public class GMailCoffeeBrewedEventProcessor {
    public static final String HEADER = "Gmail";
    public static final String PASSWORD_NAME = "password";
    public static final String RECIPIENT_NAME = "recipient";
    public static final String SOURCE_NAME = "source";
    private final Logger logger = LoggerFactory.getLogger(GMailCoffeeBrewedEventProcessor.class);
    private final PropertyFetcher propertyFetcher;
    private final EmailGenerator emailGenerator;

    @Inject
    protected GMailCoffeeBrewedEventProcessor(PropertyFetcher propertyFetcher, EmailGenerator emailGenerator) {
        this.propertyFetcher = propertyFetcher;
        this.emailGenerator = emailGenerator;
    }

    @Subscribe
    public synchronized void coffeeBrewedEvent(CoffeeBrewedEvent coffeeBrewedEvent) throws MessagingException {
        Properties mailServerProperties = new Properties();
        mailServerProperties.put("mail.smtp.port", "587");
        mailServerProperties.put("mail.smtp.auth", "true");
        mailServerProperties.put("mail.smtp.starttls.enable", "true");

        // TODO: Session is final and cannot be mocked by Mockito
        Session session = Session.getInstance(mailServerProperties, null);
        MimeMessage mimeMessage = new MimeMessage(session);
        mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(propertyFetcher.getValue(HEADER, RECIPIENT_NAME)));
        mimeMessage.setSubject(emailGenerator.generateSubject(coffeeBrewedEvent));
        mimeMessage.setContent(emailGenerator.generateBody(coffeeBrewedEvent), "text/html");

        Transport transport = session.getTransport("smtp");

        transport.connect("smtp.gmail.com", propertyFetcher.getValue(HEADER, SOURCE_NAME), propertyFetcher.getValue(HEADER, PASSWORD_NAME));
        transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
        transport.close();

        logger.info("E-mail sent to mailing list");
    }
}
