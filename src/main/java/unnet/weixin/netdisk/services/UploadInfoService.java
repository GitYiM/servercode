package unnet.weixin.netdisk.services;

import unnet.weixin.netdisk.entity.UploadInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author cy
 * @since 2020-10-20
 */
public interface UploadInfoService extends IService<UploadInfo> {

    /**
     * 增加一条文件上传记录
     * @param uploadInfo
     * @return
     */
    UploadInfo addUploadInfo(UploadInfo uploadInfo);

    /**
     * 删除文件上传记录
     * @param ids
     * @return
     */
    int deleteById(List<Long> ids, String userId);

    /**
     * 根据userId查询文件上传记录
     * @param userId
     * @return
     */
    List<UploadInfo> selectByUserId(String userId);
}
