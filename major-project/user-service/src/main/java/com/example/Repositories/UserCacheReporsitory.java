package com.example.Repositories;

import com.example.Configs.Constants;
import com.example.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;
@Repository
public class UserCacheReporsitory {
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    public User get(Integer userId){
        Object result = redisTemplate.opsForValue().get(getKey(userId));
        return (result == null) ? null :  (User) result;
    }

    public void set(User user){
        redisTemplate.opsForValue().set(getKey(user.getId()), user,Constants.USER_CACHE_EXPIRE, TimeUnit.SECONDS);
    }

    private String getKey(Integer userId){
        return Constants.USER_CACHE_PREFIX + userId;
    }
}
