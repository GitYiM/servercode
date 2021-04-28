package unnet.weixin.netdisk.entity;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * @Author ckc
 * @Description
 * @Date 2020/9/27
 */

@Data
@ConfigurationProperties(prefix = "wechat")
@Component
public class WechatInfo {

    private String agentID;

    private String secretID;

    private String corpID;

    private String tokenUrl;

    private String userInfoUrl;

    private String userDetailUrl;

    private String departmentListUrl;

    private String ticket;

    private String jsapiTicket;

    private String redirectUrl;

    private String uploadUrl;

    private String sendUrl;

    private String appId;

    private String secret;

    private String code2sessionUrl;
}
