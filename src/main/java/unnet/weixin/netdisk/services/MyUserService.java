package unnet.weixin.netdisk.services;

import com.baomidou.mybatisplus.extension.service.IService;
import unnet.weixin.netdisk.entity.MyUser;

public interface MyUserService extends IService<MyUser> {
    MyUser selectMyUserByOpenId(String openid);

    int insertMyUser(MyUser myUser);
}
