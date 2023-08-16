package com.example.Service;

import com.example.Model.User;
import com.example.Repositories.UserCacheReporsitory;
import com.example.Repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import netscape.javascript.JSObject;
import org.apache.kafka.common.protocol.types.Field;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    //Topic Name;
    private static final String USER_CREATED_TOPIC="user_created";

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserCacheReporsitory userCacheReporsitory;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    //Used to convert Json Object into string
    private ObjectMapper objectMapper=new ObjectMapper();
    public void create(User user) throws JsonProcessingException {
        userRepository.save(user);
        JSONObject userObj=new JSONObject();
        userObj.put("email",user.getEmail());
        userObj.put("phone",user.getPhone_number());
        userObj.put("name",user.getName());
        kafkaTemplate.send(USER_CREATED_TOPIC,objectMapper.writeValueAsString(userObj));
    }

    public User get(Integer userId) throws Exception {
        User user=userCacheReporsitory.get(userId);
        if(user!=null)
        {
            return user;
        }
        user= userRepository.findById(userId).orElseThrow(()->new Exception());
        userCacheReporsitory.set(user);
        return user;
    }
    public User getByPhone(String phone) throws Exception {
        //Assignment to implement the cache
        return userRepository.getByPhone(phone);
    }
}
