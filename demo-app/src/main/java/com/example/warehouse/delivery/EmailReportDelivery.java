package com.example.warehouse.delivery;

import com.example.warehouse.Report;
import com.example.warehouse.export.ExportType;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.util.Properties;

import static java.lang.System.getenv;

public class EmailReportDelivery extends AbstractReportDelivery {

    private static final String SMTP_HOST = getenv()
        .getOrDefault("SMTP_HOST", "localhost");

    private static final String SMTP_PORT = getenv()
        .getOrDefault("SMTP_PORT", "2500");


    private final InternetAddress fromAddress = new InternetAddress("demo@example.com");
    private final InternetAddress toAddress;

    private final Properties prop;

    public EmailReportDelivery(String toAddress) throws AddressException {
        super("Email-based report delivery");
        this.toAddress = new InternetAddress(toAddress);

        prop = new Properties();
        prop.put("mail.smtp.host", SMTP_HOST);
        prop.put("mail.smtp.port", SMTP_PORT);
    }

    @Override
    protected void doDeliver(Report.Type reportType, ExportType exportType, byte[] bytes) throws ReportDeliveryException {
        try {
            MimeBodyPart msgBodyPart = new MimeBodyPart();
            msgBodyPart.setContent(String.format("Please the attached %s report.", exportType), "text/plain");

            MimeBodyPart attachmentBodyPart = new MimeBodyPart();
            attachmentBodyPart.setDataHandler(new DataHandler(new ByteArrayDataSource(bytes, exportType.getMimeType())));
            attachmentBodyPart.setFileName(String.format("export.%s", exportType.getFileExtension()));

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(msgBodyPart);
            multipart.addBodyPart(attachmentBodyPart);

            Session session = Session.getDefaultInstance(prop);

            Message message = new MimeMessage(session);
            message.setFrom(fromAddress);
            message.setRecipients(Message.RecipientType.TO, new InternetAddress[]{toAddress});
            message.setSubject(String.format("%s delivery", reportType.getDisplayName()));
            message.setContent(multipart);

            Transport.send(message);
        } catch (MessagingException ex) {
            throw new ReportDeliveryException("Problem while delivering report export.", ex);
        }
    }
}
