package unnet.weixin.netdisk.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import unnet.weixin.netdisk.constants.FileOperType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperLog {

  private long id;
  private String openid;
  private String fileName;
  private FileOperType operType;
  private LocalDateTime operTime;
  private String operRes;
  private String operDes;
}
