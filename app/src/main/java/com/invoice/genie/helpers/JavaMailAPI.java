package com.invoice.genie.helpers;

import android.os.AsyncTask;

import com.invoice.genie.model.cart.Invoice;
import com.invoice.genie.model.cart.Items;

import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class JavaMailAPI extends AsyncTask<Void, Void, Void> {

    private String email;
    private String subject;
    private String message;
    boolean share;
    private final String shareEmail;
    private final String order_id;

    Invoice mailInvoice;

    private final String senderEmail = "invoice.genie@gmail.com";
    private final String senderPassword = "G@n#2807";

    public JavaMailAPI(Invoice mailInvoice, String order_id, boolean share, String shareEmail) {
        this.mailInvoice = mailInvoice;
        this.order_id = order_id;
        this.share = share;
        this.shareEmail = shareEmail;
        assign();
    }

    private void assign(){
        if(!share){
            this.email = mailInvoice.getCustomer_email();
            this.subject = "Your Order is placed Successfully & Order ID is "+ this.order_id;
        }
        if(share){
            this.email = this.shareEmail;
            this.subject = "INVOICE COPY! "+mailInvoice.getCustomer_email()+" shared the below Invoice with you! "+ this.order_id;
        }

        List<Items> itemsList = mailInvoice.getItems();

        StringBuilder itemText = new StringBuilder();
        String space = "<br><br>";
        String table = "<table  border=\"1\" style=\"width:100%\">\n";

        itemText.append("<html>");
        itemText.append("<head>");
        itemText.append("</head>");
        itemText.append("<body>");

        itemText.append("<h2 align=\"center\">")
                .append("ORDER PLACED SUCCESSFULLY in ")
                .append(mailInvoice.getCompany_name())
                .append(".</h2>")
                .append(space);

        itemText.append(table);
        itemText.append("<tr>");
        itemText.append("<th> Ordered By </th>");
        itemText.append("<th> Ordered On </th>");
        itemText.append("<th> Ordered At </th>");
        itemText.append("<th> Total CAD </th>");
        itemText.append("</tr>");

        itemText.append("<tr width=\"50%\" >\n")
                .append("<td width=\"20%\"  align=\"center\"\n>").append(mailInvoice.getCustomer_name()).append("</td>\n")
                .append("<td width=\"20%\"  align=\"center\"\n>").append(mailInvoice.getOrder_date()).append("</td>\n")
                .append("<td width=\"20%\"  align=\"center\"\n>").append(mailInvoice.getOrder_time()).append("</td>\n")
                .append("<td width=\"20%\"  align=\"center\"\n>").append("CAD ").append(mailInvoice.getOrder_total()).append("</td>\n")
                .append("</tr>");

        itemText.append("</table>");
        itemText.append(space);
        itemText.append(space);

        itemText.append(table);
        itemText.append("<tr>");
        itemText.append("<th> Product Image </th>");
        itemText.append("<th> Product Name </th>");
        itemText.append("<th> Quantity </th>");
        itemText.append("<th> Package </th>");
        itemText.append("</tr>");

        for(int j=0; j<itemsList.size(); j++){
            String imageURL = itemsList.get(j).getUrl();
            itemText.append("<tr width=\"50%\" >\n")
                    .append("<td width=\"20%\"  align=\"center\"\n>").append("<img src=").append(imageURL).append(" height=100 width=100></img>").append("</td>\n")
                    .append("<td width=\"20%\"  align=\"center\"\n>").append(itemsList.get(j).getProduct_name()).append("</td>\n")
                    .append("<td width=\"20%\"  align=\"center\"\n>").append(itemsList.get(j).getProduct_quantity()).append("</td>\n")
                    .append("<td width=\"20%\"  align=\"center\"\n>").append("CAD ").append(itemsList.get(j).getProduct_price()).append(" - ").append(itemsList.get(j).getUnit_type()).append("</td>\n")
                    .append("</tr>");
        }

        this.message = itemText.toString();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        MimeMessage mimeMessage = new MimeMessage(session);
        try {
            mimeMessage.setFrom(new InternetAddress(senderEmail));
            mimeMessage.addRecipients(Message.RecipientType.TO, String.valueOf(new InternetAddress(email)));
            mimeMessage.setSubject(subject);
            mimeMessage.setContent(message,"text/html");
            Transport.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
