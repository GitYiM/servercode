package unnet.weixin.netdisk.services;

import unnet.weixin.netdisk.entity.FileInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author cy
 * @since 2020-10-12
 */
public interface FileInfoService extends IService<FileInfo> {

    /**
     * 新增文件信息
     * @param fileSize
     * @param path
     * @param filename
     * @param contentType
     * @return
     */
    int addFileInfo(long fileSize, String path, String filename, String contentType, String openid);

    /**
     * 创建目录
     * @param path
     * @param userId
     * @return
     */
    int createDir(String path, String userId);

    /**
     * 删除文件夹及文件
     * @param path
     * @return
     */
    int deleteFiles(String path, String bucket, String userId) throws Exception;

    /**
     * 删除单个文件
     * @param path
     * @return
     */
    int deleteFile(String path, String userId);

    /**
     * 根据路径查找文件
     * @param path
     * @return
     */
    List<FileInfo> selectByPath(String path, String userId);

    /**
     * 获取文件的大小
     * @param filename
     * @param userId
     * @return
     */
    Long getFileSize(String filename, String userId);

    /**
     * 修改文件名称
     * @param newFileName
     * @param path
     */
    int renameFile(String newFileName, String path, String openid);

    /**
     * 模糊查询文件
     * @param keyWord
     * @param openid
     */
    List<FileInfo> searchByKeword(String keyWord, String openid);
}
