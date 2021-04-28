package unnet.weixin.netdisk.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryKeyword {

  private long id;
  private String openid;
  private String keyWord;
  private LocalDateTime createTime;

}
