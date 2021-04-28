package unnet.weixin.netdisk.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import unnet.weixin.netdisk.entity.FileInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author cy
 * @since 2020-10-12
 */
public interface FileInfoMapper extends BaseMapper<FileInfo> {
    @Select("select size from file_info where file_name = #{filename} and openid = #{openid}")
    Long getFileSize(@Param("filename") String filename,@Param("openid") String openid);
}
