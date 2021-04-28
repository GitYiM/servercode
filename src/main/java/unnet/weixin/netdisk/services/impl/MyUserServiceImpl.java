package unnet.weixin.netdisk.services.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import unnet.weixin.netdisk.entity.MyUser;
import unnet.weixin.netdisk.mapper.MyUserMapper;
import unnet.weixin.netdisk.services.MyUserService;

import javax.annotation.Resource;


@Service
public class MyUserServiceImpl extends ServiceImpl<MyUserMapper, MyUser> implements MyUserService {

    @Resource
    MyUserMapper myUserMapper;

    @Override
    public MyUser selectMyUserByOpenId(String openid) {
        QueryWrapper<MyUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid", openid);
        return myUserMapper.selectOne(queryWrapper);
    }

    @Override
    @Transactional(isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public int insertMyUser(MyUser myUser) {
        return myUserMapper.insert(myUser);
    }
}
