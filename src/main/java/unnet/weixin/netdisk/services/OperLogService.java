package unnet.weixin.netdisk.services;

import com.baomidou.mybatisplus.extension.service.IService;
import unnet.weixin.netdisk.constants.FileOperType;
import unnet.weixin.netdisk.entity.OperLog;

import java.util.List;


public interface OperLogService extends IService<OperLog> {
    List<OperLog> getAllLog(String openid, int dayCount);

    int addLog (String openid, FileOperType operType, String fileType,String filename, String operRes, String newFileName);

}
