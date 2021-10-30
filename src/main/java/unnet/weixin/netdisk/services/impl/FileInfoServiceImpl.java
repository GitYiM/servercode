package unnet.weixin.netdisk.services.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.transaction.annotation.Transactional;
import unnet.weixin.netdisk.constants.FileOperType;
import unnet.weixin.netdisk.entity.FileInfo;
import unnet.weixin.netdisk.mapper.FileInfoMapper;
import unnet.weixin.netdisk.services.FileInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import unnet.weixin.netdisk.services.HistoryService;
import unnet.weixin.netdisk.services.OperLogService;
import unnet.weixin.netdisk.services.StorageService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static unnet.weixin.netdisk.constants.Constants.filterPath;
import static unnet.weixin.netdisk.constants.Constants.thumbnailTypes;
import static unnet.weixin.netdisk.constants.FileOperType.ADD;
import static unnet.weixin.netdisk.constants.FileOperType.DELETE;
import static unnet.weixin.netdisk.constants.FileOperType.UPDATE;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author cy
 * @since 2020-10-12
 */
@Service
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo> implements FileInfoService {

    @Resource
    private FileInfoMapper fileInfoMapper;

    @Resource
    private StorageService storageService;

    @Resource
    private HistoryService historyService;

    @Resource
    private OperLogService operLogService;

    @Override
    @Transactional
    public int addFileInfo(long fileSize, String path, String filename, String contentType, String openid) {
        LocalDateTime now = LocalDateTime.now();
        FileInfo fileInfo = new FileInfo();
        fileInfo.setCreateTime(now);
        fileInfo.setUploadTime(now);
        fileInfo.setSize(fileSize);
        fileInfo.setType(contentType);
        fileInfo.setOpenid(openid);
//        if (filename != null) {
//            if (path == null || "".equals(path)) {
//                path = filename;
//            } else {
//                path = path + "/" + filename;
//            }
//        }
        fileInfo.setFileName(filename);
        fileInfo.setPath(path);
        if (openid != null) {
            QueryWrapper<FileInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("path", path)
                    .eq("openid", openid);
            Integer num = fileInfoMapper.selectCount(wrapper);
            if (num == 0){
                int res = fileInfoMapper.insert(fileInfo);
                if(res >= 0 &&  !path.split("/")[0].equals("thumbnails")) {
                    String operRes = "成功";
                    operLogService.addLog(openid, ADD, contentType, filename, operRes, null);
                }
                return res;
            }
        }
        return 0;
    }

    /**
     *
     * @param path ; fromPath + folderName
     * @param openid
     * @return
     */
    @Override
    @Transactional
    public int createDir(String path, String openid) {
        String[] pathArray = path.split("/");
        String folderName = pathArray[pathArray.length - 1];
        return addFileInfo(0, path, folderName, "Directory", openid);
//        if (!path.contains("/")){
//            return addFileInfo(0, path, path, "Directory", openid);
//        }else {
//            String[] splits = path.split("/");
//            return addFileInfo(0, path, splits[splits.length-1], "Directory", openid);
//        }
//        StringBuilder newPath = new StringBuilder();
//        for (String split: splits) {
//            newPath.append(split);
//            addFileInfo(0, newPath.toString(), null, "Directory", openid);
//            newPath.append("/");
//        }
//        return 1;
    }

    @Override
    @Transactional
    public int deleteFiles(String path, String bucket, String openid) throws Exception {
//      删除目录下文件
        QueryWrapper<FileInfo> wrapper = new QueryWrapper<>();
        wrapper.likeRight("path", path+"/")
                .eq("openid", openid);
        storageService.removeDir(path, bucket, openid);
        fileInfoMapper.delete(wrapper);
//      删除数据库中文件记录
        wrapper = new QueryWrapper<>();
        wrapper.eq("path", path)
                .eq("openid", openid);
        FileInfo file = fileInfoMapper.selectOne(wrapper);
        int res = fileInfoMapper.delete(wrapper);
        if(res > 0) {
            String operRes = "成功";
            operLogService.addLog(openid, DELETE, file.getType(), file.getFileName(), operRes, null);
        }
        return res;
    }

    @Override
    @Transactional
    public int deleteFile(String path, String openid) {
        QueryWrapper<FileInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("path", path)
                .eq("openid", openid);
        FileInfo fileInfo = fileInfoMapper.selectOne(wrapper);
        int res = fileInfoMapper.delete(wrapper);
        if(res > 0) {
            String operRes = "成功";
            operLogService.addLog(openid, DELETE, fileInfo.getType(), fileInfo.getFileName(), operRes, null);
        }
        return res;
    }

    @Override
    public List<FileInfo> selectByPath(String path, String openid) {
        ArrayList<FileInfo> fileList = new ArrayList<>();
        QueryWrapper<FileInfo> wrapper = new QueryWrapper<>();
        if (path == null){
            path = "";
        }
        wrapper.likeRight("path", path)
                .eq("openid", openid)
                .ne("path", path)
                .orderByDesc("upload_time");
        List<FileInfo> fileInfos = fileInfoMapper.selectList(wrapper);
        System.out.println(fileInfos);
//        for(FileInfo file: fileInfos) {
//            String filePath = file.getPath();
//            String[] filePathArray = filePath.split("/");
//            if(filePathArray.length >= 2){
//                filePathArray = Arrays.copyOfRange(filePathArray, 1, filePathArray.length);
//                if(!ArrayUtils.contains(filterPath, filePathArray[0]) && filePathArray.length < 2){
//                    fileList.add(file);
//                }
//            }else {
//                if(!ArrayUtils.contains(filterPath, filePathArray[0])){
//                    fileList.add(file);
//                }
//            }
//
//
//        }
        for (FileInfo file : fileInfos) {
            String newPath = file.getPath();
//            file.setFileName(newPath);
            if ("".equals(path)) {
                if (!newPath.contains("/") && !ArrayUtils.contains(filterPath, newPath.split("/")[0])) {
//                    file.setFileName(file.getPath());
                    fileList.add(file);
                }
            } else {
                String s = null;
                String[] x = newPath.split(path);
                System.out.println(x);
                if (x.length >= 2) {
                    s = x[1];
                    if (!s.contains("/")) {
                        fileList.add(file);
                    }
                }
            }
        }
        return fileList;
    }

    @Override
    public Long  getFileSize(String filename, String userId){
        return fileInfoMapper.getFileSize(filename,userId);
    }

    @Override
    @Transactional
    public int renameFile(String newFileName, String path, String openid) {
        QueryWrapper<FileInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid", openid).eq("path", path);
        FileInfo file = fileInfoMapper.selectOne(queryWrapper);
        if(file != null) {
            UpdateWrapper<FileInfo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("path", path).eq("openid", openid).set("file_name", newFileName);
            int res = fileInfoMapper.update(null, updateWrapper);
            if(res > 0) {
                String operRes = "成功";
                operLogService.addLog(openid, UPDATE, file.getType(),file.getFileName(), operRes, newFileName);
            }
            return res;
        }
        return 0;

    }

    @Override
    public List<FileInfo> searchByKeword(String keyWord, String openid) {
        historyService.addKeyWord(openid, keyWord);
        QueryWrapper<FileInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("file_name", keyWord);
        List<FileInfo> files = fileInfoMapper.selectList(queryWrapper);
        List<FileInfo> resFiles = new ArrayList<>();
        for(FileInfo file : files) {
            if(!file.getPath().split("/")[0].equals("thumbnails")){
                resFiles.add(file);
            }
        }
        return resFiles;
    }

    @Override
    public List<FileInfo> selectByPathExactly(String path) {
        List<FileInfo> list = null;
        QueryWrapper<FileInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("path", path);
        list = fileInfoMapper.selectList(wrapper);
        return list;
    }

    @Override
    public BigDecimal checkUserUsage(String openid) {

        QueryWrapper<FileInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("openid", openid);
        wrapper.select("IFNULL(sum(size),0) as total");
        Map<String, Object> map = this.getMap(wrapper);
        BigDecimal size = (BigDecimal) map.get("total");
        return size;
    }


}