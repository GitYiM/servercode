package unnet.weixin.netdisk.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.mapstruct.Mapper;
import unnet.weixin.netdisk.entity.UserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author cy
 * @since 2020-09-27
 */
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {
//
//    /**
//     * 统计文件大小
//     * @param userId
//     * @return
//     */
//    @Select("select sum(size) from file_info where userid = #{userId}")
//    Long countUsage(@Param("userId") String userId);
//
//    /**
//     * 更新usage
//     * @param userId
//     * @param filesize
//     */
//    @Update("update user_info set user_usage=#{filesize} where userid = #{userId}")
//    void updateUserUsage(@Param("userId") String userId,@Param("filesize") Long filesize);
//
//    /**
//     * 获取usage
//     * @param userId
//     * @return
//     */
//    @Select("select user_usage from user_info where userid = #{userId}")
//    Long selectUserUsage(@Param("userId") String userId);
}
