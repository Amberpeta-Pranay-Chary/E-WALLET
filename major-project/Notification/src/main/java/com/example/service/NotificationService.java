package com.example.service;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.print.DocFlavor;
import java.util.logging.Logger;

@Service
public class NotificationService {
    //private static final Logger logger =(Logger) LoggerFactory.getLogger(NotificationService.class);
    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    SimpleMailMessage simpleMailMessage;
    public static final String TRANSACTION_COMPLETED_TOPIC="transaction_completed";
    @KafkaListener(topics={TRANSACTION_COMPLETED_TOPIC},groupId = "jbdl123")
    public void notify(String msg) throws ParseException {
        JSONObject obj= (JSONObject) new JSONParser().parse(msg);
        String externalTransactionId=(String)obj.get("transactionId");
        String transactionStatus=(String)obj.get("transactionStatus");
        Long amount=(Long)obj.get("amount");
        String senderEmail=(String)obj.get("senderEmail");
        String receiverEmail=(String)obj.get("receiverEmail");
        String senderPhone=(String)obj.get("senderPhone");
        String receiverPhone=(String)obj.get("receiverPhone");

        String senderMessage=getSenderMessage(transactionStatus,externalTransactionId,amount);
        String receiverMessage=getReceiverMessage(transactionStatus,amount,senderEmail);
        if(senderMessage!=null&&senderMessage.length()>0)
        {
            simpleMailMessage.setTo(senderEmail);
            simpleMailMessage.setFrom("e.wallet.jbdl50.majorproject@gmail.com");
            simpleMailMessage.setText(senderMessage);
            simpleMailMessage.setSubject("E-WALLET TRANSACTION UPDATE");
            javaMailSender.send(simpleMailMessage);
        }
        if(receiverMessage!=null&&receiverMessage.length()>0)
        {
            simpleMailMessage.setTo(receiverEmail);
            simpleMailMessage.setSubject("E-WALLET TRANSACTION UPDATE");
            simpleMailMessage.setFrom("e.wallet.jbdl50.majorproject@gmail.com");
            simpleMailMessage.setText(receiverMessage);
            javaMailSender.send(simpleMailMessage);
        }
        //logger.info("Cool the notification sent");

    }
    private String getSenderMessage(String transactionStatus,String externalTransactionId,Long amount)
    {
        String msg="";
        if(transactionStatus.equals("FAILED"))
        {
            msg="Hi! "+"Your transaction of amount "+amount+" , transaction id="+externalTransactionId+" has failed";
        }
        else
        {
            msg="Hi! "+"Your account has been debited with amount "+amount+" ,transaction id="+externalTransactionId;
        }
        return msg;
    }
    private String getReceiverMessage(String transactionStatus,Long amount,String senderEmail)
    {
        String msg="";
        if(transactionStatus.equals("SUCCESSFUL"))
        {
            msg="Hi! Your account has been credited with amount "+amount+" for the transaction done by the user "+senderEmail;
        }
        return msg;
    }


}
