package unnet.weixin.netdisk.services.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
import unnet.weixin.netdisk.entity.UploadInfo;
import unnet.weixin.netdisk.mapper.UploadInfoMapper;
import unnet.weixin.netdisk.services.UploadInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author cy
 * @since 2020-10-20
 */
@Service
public class UploadInfoServiceImpl extends ServiceImpl<UploadInfoMapper, UploadInfo> implements UploadInfoService {

    @Resource
    private UploadInfoMapper uploadInfoMapper;

    @Override
    @Transactional
    public UploadInfo addUploadInfo(UploadInfo uploadInfo) {
        boolean save = save(uploadInfo);
        if (save){
            return uploadInfo;
        }
        return null;
    }

    @Override
    @Transactional
    public int deleteById(List<Long> ids, String userId) {
        QueryWrapper<UploadInfo> wrapper = new QueryWrapper<>();
        wrapper.in("id", ids)
                .eq("userid", userId);
        return uploadInfoMapper.delete(wrapper);
    }

    @Override
    @Transactional
    public List<UploadInfo> selectByUserId(String userId) {
        QueryWrapper<UploadInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("userid", userId).orderByDesc("create_time");
        return uploadInfoMapper.selectList(wrapper);
    }

}
