package unnet.weixin.netdisk.services.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import unnet.weixin.netdisk.constants.FileOperType;
import unnet.weixin.netdisk.entity.OperLog;
import unnet.weixin.netdisk.mapper.OperLogMapper;
import unnet.weixin.netdisk.services.OperLogService;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static unnet.weixin.netdisk.constants.FileOperType.*;


@Service
public class OperLogServiceImpl extends ServiceImpl<OperLogMapper, OperLog> implements OperLogService {
    @Resource
    OperLogMapper operLogMapper;

    @Override
    public List<OperLog> getAllLog(String openid, int dayCount) {
        LocalDateTime startDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).plusDays(-3);
        List<OperLog> list = null;
        if(openid != null) {
            QueryWrapper<OperLog> wrapper = new QueryWrapper<>();
            wrapper.eq("openid", openid).orderByDesc("oper_time").ge("oper_time", startDay);
            list = operLogMapper.selectList(wrapper);
        }
        return list;
    }

    @Override
    public int addLog(String openid, FileOperType operType, String fileType, String filename, String operRes, String newFileName) {
        String operDes = "";
        String operFileType = fileType.equalsIgnoreCase("Directory")? "文件夹": "文件";
        if(openid != null) {
            switch (operType) {
                case ADD:
                    operDes = "新增"+operFileType+":"+filename;
                    break;
                case DELETE:
                    operDes = "删除"+operFileType+":"+filename;
                    break;
                case UPDATE:
                    operDes = "重命名"+operFileType+":"+filename+"->"+ newFileName;
                    break;
                default:
            }
        }
        OperLog operLog = new OperLog();
        operLog.setFileName(filename);
        operLog.setOpenid(openid);
        operLog.setOperDes(operDes);
        operLog.setOperType(operType);
        operLog.setOperRes(operRes);
        operLog.setOperTime(LocalDateTime.now());
        return operLogMapper.insert(operLog);
    }
}
