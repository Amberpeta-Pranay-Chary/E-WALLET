package com.example.service;

import com.example.dto.CreateTransactionRrquest;
import com.example.model.Transaction;
import com.example.model.TransactionStatus;
import com.example.repository.TransactionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    RestTemplate restTemplate=new RestTemplate();

    public  static final String TRANSACTION_CREATED="transaction_created";
    public static final String WALLET_UPDATED_TOPIC="wallet_updated";
    public static final String WALLET_UPDATED_SUCCESS_STATUS="SUCCESS";
    public static final String WALLET_UPDATED_FAILED_STATUS="FAILED";
    public static final String TRANSACTION_COMPLETED_TOPIC="transaction_completed";
    private ObjectMapper objectMapper=new ObjectMapper();
    public String transact(CreateTransactionRrquest request) throws JsonProcessingException {
        Transaction transaction=Transaction.builder()
                .senderId(request.getSender())
                .recieverId(request.getReceiver())
                .externalId(UUID.randomUUID().toString())
                .reason(request.getReason())
                .transactionStatus(TransactionStatus.PENDING)
                .amount(request.getAmount()).build();
        transactionRepository.save(transaction);
        JSONObject obj=new JSONObject();
        obj.put("senderId",transaction.getSenderId());
        obj.put("receiverId",transaction.getRecieverId());
        obj.put("amount",transaction.getAmount());
        //we also neeed to pass the transaction Id because , wallet service should perform the transaction to a particular transaction so that while waller service publishin the message the transaction record will the transactions status to Successful fro pending
        //Note :we are passing the Transaction externalId not the exact transaction Id, because its better to pass the UUID its also an Unique to every transaction that takes place
        obj.put("transactionId",transaction.getExternalId());
        kafkaTemplate.send(TRANSACTION_CREATED,objectMapper.writeValueAsString(obj));

        return transaction.getExternalId();
    }
    @KafkaListener(topics={WALLET_UPDATED_TOPIC},groupId = "jdbl123")
    public void updateTransaction(String msg) throws ParseException, JsonProcessingException {
        JSONObject obj= (JSONObject) new JSONParser().parse(msg);
        String externalTransactionId=(String)obj.get("transactionId");
        String walletUpdateStatus=(String)obj.get("status");
        String receiverId=(String) obj.get("receiverWalletId");
        String senderId=(String)obj.get("senderWalletId");
        Long amount=(Long) obj.get("amount");
        TransactionStatus transactionStatus;
        if(walletUpdateStatus.equals(WALLET_UPDATED_FAILED_STATUS))
        {
            transactionStatus=TransactionStatus.FAILED;
            transactionRepository.updateTransaction(externalTransactionId,TransactionStatus.FAILED);
        }
        else
        {
            transactionStatus=TransactionStatus.SUCCESSFUL;
            transactionRepository.updateTransaction(externalTransactionId,TransactionStatus.SUCCESSFUL);
        }
        //Getting the Emails of Sender and reciever from user services with phone numbers
        JSONObject senderObj=this.restTemplate.getForObject("http://localhost:8080/user/phone/"+senderId,JSONObject.class);
        JSONObject recieverObj=this.restTemplate.getForObject("http://localhost:8080/user/phone/"+receiverId,JSONObject.class);
        String senderEmail=(senderObj==null)?null:(String)senderObj.get("email");
        String recieverEmail=(recieverObj==null)?null:(String)recieverObj.get("email");
        obj=new JSONObject();
        obj.put("transactionId",externalTransactionId);
        obj.put("transactionStatus",transactionStatus.toString());
        obj.put("amount",amount);
        obj.put("senderEmail",senderEmail);
        obj.put("receiverEmail",recieverEmail);
        obj.put("senderPhone",senderId);
        obj.put("receiverPhone",receiverId);
        //Producing the Event in the Transaction_completed topic so notification service can consume.
        kafkaTemplate.send(TRANSACTION_COMPLETED_TOPIC,objectMapper.writeValueAsString(obj));
    }
}
