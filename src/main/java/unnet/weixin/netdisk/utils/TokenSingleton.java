package unnet.weixin.netdisk.utils;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import unnet.weixin.netdisk.entity.WechatInfo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class TokenSingleton {
	
	private static final Logger logger = LoggerFactory.getLogger(TokenSingleton.class);
	
	private Map<String, String> map = new HashMap<>();
	
	private static TokenSingleton single = null;
	
	private TokenSingleton() {}
	
	// 静态工厂方法
    public static TokenSingleton getInstance() {
        if (single == null) {
        	logger.info("TokenSingleton为空，现在新建一个tokenSingleton");
        	synchronized (TokenSingleton.class){
        	    if (single == null){
                    single = new TokenSingleton();
                }
            }
        }
        return single;
    }
    public String getAccessToken(WechatInfo wechatInfo) {
        String time = map.get("time");
        String accessToken = map.get("access_token");
        Long nowDate = new Date().getTime();
//        token时效1小时
        if (accessToken != null && time != null && nowDate - Long.parseLong(time) < 3600 * 1000) {
        	logger.info("accessToken"+"存在，且没有超时");
        } else {
        	logger.info("accessToken"+"超时，或者不存在，重新获取");
        	JSONObject json  = JSONObject.parseObject(HttpsUtils.sendGetRequest(wechatInfo.getTokenUrl() +
					wechatInfo.getCorpID() + "&corpsecret=" + wechatInfo.getSecretID(), null));
            if("0".equals(json.getString("errcode")) && json.containsKey("access_token")) {
            	accessToken = json.getString("access_token");
            	
            	map.put("access_token", accessToken);
            	map.put("time", nowDate.toString());
            } else {
				return null;
			}
        }
        return accessToken;
    }
}
