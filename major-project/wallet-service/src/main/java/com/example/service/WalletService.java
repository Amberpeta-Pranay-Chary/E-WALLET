package com.example.service;

import com.example.model.Wallet;
import com.example.repositories.WalletRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class WalletService {
    public static final String USER_CREATION_TOPIC="user_created";
    public static final String TRANSACTION_CREATED_TOPIC="transaction_created";

    //After updating the transaction wallet produces whther the transaction got failed or success , so that transaction
    public static final String WALLET_UPDATED_TOPIC="wallet_updated";
    ObjectMapper objectMapper=new ObjectMapper();
    @Value("${wallet.initial.balance}")
    Long balance;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    @Autowired
    WalletRepository walletRepository;


    //Kafka Listener will act as a Watcher and if any msg will is there to consume it will consume from the specified topic

    //This is User
    @KafkaListener(topics = {USER_CREATION_TOPIC},groupId = "jdbl50") //Need to pass the GroupId. In console if we don't specify group name its fine kafka will generate but in spring we need to specify the Group Id.
    public void createWallet(String msg) throws ParseException {
        //THis msg is consumed from KafkaListener.
        //Now we need to convert the msg from string into Json object and will get all the related data from Jason Object
        JSONObject obj=(JSONObject) new JSONParser().parse(msg);
        //Now will take the userId from the msg and assign it to the wallet Id.
        String walletId=(String)obj.get("phone");
        //Now we can create the wallet object and store it in db.
        Wallet wallet =Wallet.builder().
                walletId(walletId).
                balance(this.balance).
                build();
        walletRepository.save(wallet);

        //TODO:Publis an event of wallet creation
    }
    //THis is User Transaction Flow
    @KafkaListener(topics = {TRANSACTION_CREATED_TOPIC},groupId = "jdbl50")
    public void updateWallet(String msg) throws ParseException, JsonProcessingException {

            //THis msg is consumed from KafkaListener.
            //Now we need to convert the msg from string into Json object and will get all the related data from Jason Object
            JSONObject obj = (JSONObject) new JSONParser().parse(msg);
            //Now will take the userId from the msg and assign it to the wallet Id.
            String receiverWalletId = (String) obj.get("receiverId");
            String senderWalletId = (String) obj.get("senderId");
            String transactionId = (String) obj.get("transactionId");
            Long amount = (Long) obj.get("amount");

            try{
            Wallet senderWallet = walletRepository.findByWalletId(senderWalletId);
            Wallet receiverWallet = walletRepository.findByWalletId(receiverWalletId);
            if (senderWallet == null || receiverWallet == null || senderWallet.getBalance() < amount) {
                obj=init(receiverWalletId,senderWalletId,amount,transactionId,"FAILED");
                obj.put("senderWalletBalance", (senderWallet == null) ? 0 : senderWallet.getBalance());
                kafkaTemplate.send(WALLET_UPDATED_TOPIC, objectMapper.writeValueAsString(obj));
                return;
            }
            walletRepository.updateWallet(senderWalletId,-amount);//Updating the sender Wallet by deducting the amount
            walletRepository.updateWallet(receiverWalletId,amount);//Updating the reciever Wallet by deducting the amount
                obj=init(receiverWalletId,senderWalletId,amount,transactionId,"SUCCESS");
                //Now will produce the success msg to the topic
                kafkaTemplate.send(WALLET_UPDATED_TOPIC, objectMapper.writeValueAsString(obj));

        }
        catch(Exception e)
            {
                obj=init(receiverWalletId,senderWalletId,amount,transactionId,"FAILED");
                obj.put("errorMsg",e.getMessage());
                kafkaTemplate.send(WALLET_UPDATED_TOPIC,objectMapper.writeValueAsString(obj));
            }
    }
    public JSONObject init(String receiverId,String senderId,Long amount,String transactionId,String status)
    {
        JSONObject obj = new JSONObject();
        obj.put("transactionId", transactionId);
        obj.put("senderWalletId", senderId);
        obj.put("receiverWalletId", receiverId);
        obj.put("amount", amount);
        obj.put("status",status);
        return obj;
    }

}
