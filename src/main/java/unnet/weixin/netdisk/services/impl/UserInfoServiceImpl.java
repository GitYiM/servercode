package unnet.weixin.netdisk.services.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import unnet.weixin.netdisk.entity.UserInfo;
import unnet.weixin.netdisk.mapper.UserInfoMapper;
import unnet.weixin.netdisk.services.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author cy
 * @since 2020-09-27
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    public UserInfo selectUserByUserId(String userId) {
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("userid", userId);
        return userInfoMapper.selectOne(wrapper);
    }

    @Override
    @Transactional(isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public int insertUserInfo(UserInfo userInfo) {
        return userInfoMapper.insert(userInfo);
    }


//    @Override
//    public Long countUsage(String userId){
//        return userInfoMapper.countUsage(userId);
//    }
//
//    @Override
//    public void updateUserUsage(String userId,Long filesize){
//        userInfoMapper.updateUserUsage(userId,filesize);
//    }
//
//    @Override
//    public Long selectUserUsage(String userId){
//        return userInfoMapper.selectUserUsage(userId);
//    }

}
