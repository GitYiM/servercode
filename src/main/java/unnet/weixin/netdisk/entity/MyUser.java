package unnet.weixin.netdisk.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyUser {
  @TableId(value = "id", type = IdType.AUTO)
  private long id;
  private String openid;
  private long gender;
  private String city;
  private String nickName;
  private String province;
  private String country;
  private String avatarUrl;
  public String bucket;
  private long userUsage;
  private long picUsage;
  private long videoUsage;
  private long audioUsage;
  private long docUsage;
  private long otherUsage;
}
