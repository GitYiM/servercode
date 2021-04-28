package unnet.weixin.netdisk.utils;


import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import unnet.weixin.netdisk.entity.LoginStatusInfo;

import javax.annotation.Resource;
import java.io.Serializable;

@Component
public class UserCheck {


    @Resource
    private RedisTemplate<String, Serializable> serializableRedisTemplate;

    public LoginStatusInfo checkUserSession (String userSession) throws Exception {
        LoginStatusInfo curInfo = (LoginStatusInfo) serializableRedisTemplate.opsForValue().get(userSession);
        System.out.println(curInfo);
        if(curInfo.getOpenId() == null) {
            throw new Exception("用户不存在");
        }
        return curInfo;

    }

}
