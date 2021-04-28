package unnet.weixin.netdisk.services;

import org.apache.ibatis.annotations.Select;
import unnet.weixin.netdisk.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author cy
 * @since 2020-09-27
 */
public interface UserInfoService extends IService<UserInfo> {

    UserInfo selectUserByUserId(String userId);

    int insertUserInfo(UserInfo userInfo);

//    Long countUsage (String userId);
//
//    void updateUserUsage (String userId,Long filesize);
//
//    Long  selectUserUsage (String userId);
}
