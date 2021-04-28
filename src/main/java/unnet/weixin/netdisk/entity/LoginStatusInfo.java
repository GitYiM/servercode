package unnet.weixin.netdisk.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginStatusInfo implements Serializable {
    private String openId;
    private String sessionKey;
}
