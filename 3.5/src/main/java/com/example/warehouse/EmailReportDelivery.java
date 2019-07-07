package com.example.warehouse;

import com.example.warehouse.export.ExportType;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.util.Properties;

public class EmailReportDelivery implements ReportDelivery {

    private final InternetAddress fromAddress = new InternetAddress("demo@example.com");
    private final InternetAddress toAddress;

    private final Properties prop;

    public EmailReportDelivery(String toAddress) throws AddressException {
        this.toAddress = new InternetAddress(toAddress);

        prop = new Properties();
        prop.put("mail.smtp.port", "2500");
    }

    @Override
    public String getName() {
        return "Email-based report delivery";
    }

    @Override
    public void deliver(Report.Type reportType, ExportType exportType, byte[] bytes) throws ReportDeliveryException {
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
