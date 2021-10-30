package unnet.weixin.netdisk.controller;

import com.alibaba.fastjson.JSONObject;
import io.minio.ObjectStat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import unnet.weixin.netdisk.base.BaseController;
import unnet.weixin.netdisk.constants.Constants;
import unnet.weixin.netdisk.entity.*;
import unnet.weixin.netdisk.constants.NetStatus;
import unnet.weixin.netdisk.services.*;
import unnet.weixin.netdisk.services.impl.CreateBucketServiceImpl;
import unnet.weixin.netdisk.utils.HttpsUtils;
import unnet.weixin.netdisk.utils.TokenSingleton;
import unnet.weixin.netdisk.utils.UserCheck;

import javax.annotation.Resource;
import javax.annotation.Resources;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Author ckc
 * @Description
 * @Date 2020/9/27
 */

@CrossOrigin(maxAge = 3600, allowCredentials = "true")
@RestController
@ResponseBody
@RequestMapping("/storage")
public class StorageController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageController.class);

    @Resource
    private StorageService embeddedStorageService;
    @Resource
    private CreateBucketServiceImpl createBucketServiceImpl;
    @Resource
    private FileInfoService fileInfoService;
    @Resource
    private WechatInfo wechatInfo;
    @Resource
    private UploadInfoService uploadInfoService;
    @Resource
    private UserInfoService userInfoService;
    @Resource
    private OperLogService operLogService;

    @Resource
    private HistoryService historyService;

    @Resource
    private RedisTemplate<String, Serializable> serializableRedisTemplate;

    @Resource
    private UserCheck userCheck;

//    @ApiOperation("文件上传")
//    @RequestMapping(value = "/UploadImages", method = RequestMethod.POST)
//    public RestResult<?> uploadFiles(MultipartFile files, String path, String userId, String filename, @RequestHeader("userSession") String userSession) {
//        System.out.println(files);
//        System.out.println(path);
//        System.out.println(userId);
//        System.out.println(filename);
//        System.out.println(userSession);
//        LoginStatusInfo curInfo = (LoginStatusInfo) serializableRedisTemplate.opsForValue().get(userSession);
//        String openId = curInfo.getOpenId();
//        String uploadfilename;
//        if (filename == null || filename.equals("")) {
//            uploadfilename = files.getOriginalFilename();
//        } else {
//            uploadfilename = filename;
//        }
////        if (!JudgeImage.checkImage(file)) {
////            return new ResponseEntity<>("You can upload only image file!", HttpStatus.INTERNAL_SERVER_ERROR);
////        }
//        InputStream bais = null;
//        try {
//            bais = files.getInputStream();
//            long fileSize = files.getSize();
//            if (userId != null && !"".equals(userId)) {
//                embeddedStorageService.put(bais, (String) session.getAttribute("bucket"), fileSize, path, uploadfilename, files.getContentType(), userId);
////                embeddedStorageService.put(bais, (String) session.getAttribute("bucket"), fileSize, path, file.getOriginalFilename(), file.getContentType(), "ChenYuanRuoMeng");
//                UploadInfo uploadInfo = new UploadInfo();
//                uploadInfo.setCreateTime(LocalDateTime.now());
//                uploadInfo.setFileName(uploadfilename);
//                uploadInfo.setFileSize(fileSize);
//                uploadInfo.setUserid(userId);
//                uploadInfo = uploadInfoService.addUploadInfo(uploadInfo);
//                if (uploadInfo != null) {
//                    return new RestResult<>(0, files.getOriginalFilename() + ": 上传成功", uploadInfo);
//                }
//            }
//            return new RestResult<>(-1, files.getOriginalFilename() + ": 上传失败");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new RestResult<>(-1, files.getOriginalFilename() + ": 上传失败，服务器异常");
//        } finally {
//            if (null != bais) {
//                try {
//                    bais.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
    @ApiOperation("文件上传")
    @RequestMapping(value = "/UploadImages", method = RequestMethod.POST)
    public RestResult<?> uploadFiles(@RequestParam("file") MultipartFile files, String path, String filename, @RequestHeader("userSession") String userSession) {
//      后端无法判断sessionKey的有效性，只有前端有api可以判断
        LoginStatusInfo curInfo = null;
        try {
            curInfo = userCheck.checkUserSession(userSession);
        } catch (Exception e) {
            return new RestResult<>(-1, e.getMessage());
        }
        String openid = curInfo.getOpenId();
        String bucket = openid.toLowerCase();

        String uploadfilename;
        if (filename == null || filename.equals("")) {
            uploadfilename = files.getOriginalFilename();
        } else {
            uploadfilename = filename;
        }
//        if (!JudgeImage.checkImage(file)) {
//            return new ResponseEntity<>("You can upload only image file!", HttpStatus.INTERNAL_SERVER_ERROR);
//        }

        InputStream bais = null;
        try {
            bais = files.getInputStream();
//          getSize单位为字节
            long fileSize = files.getSize();
            if (openid != null && !"".equals(openid)) {
                embeddedStorageService.put(bais, bucket, fileSize, path, uploadfilename, files.getContentType(), openid);
//                embeddedStorageService.put(bais, (String) session.getAttribute("bucket"), fileSize, path, file.getOriginalFilename(), file.getContentType(), "ChenYuanRuoMeng");
                UploadInfo uploadInfo = new UploadInfo();
                uploadInfo.setCreateTime(LocalDateTime.now());
                uploadInfo.setFileName(uploadfilename);
                uploadInfo.setFileSize(fileSize);
                uploadInfo.setOpenid(openid);
                uploadInfo = uploadInfoService.addUploadInfo(uploadInfo);
                if (uploadInfo != null) {
                    return new RestResult<>(0, files.getOriginalFilename() + ": 上传成功", uploadInfo);
                }
            }
            return new RestResult<>(-1, files.getOriginalFilename() + ": 上传失败");
        } catch (Exception e) {
            e.printStackTrace();
            return new RestResult<>(-1, files.getOriginalFilename() + ": 上传失败，服务器异常");
        } finally {
            if (null != bais) {
                try {
                    bais.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @ApiOperation("文件列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public RestResult<?> listBucket(String path,@RequestHeader("userSession") String userSession) {
        logger.warn(path);
        LoginStatusInfo curInfo = null;
        try {
            curInfo = userCheck.checkUserSession(userSession);
        } catch (Exception e) {
            return new RestResult<>(-1, e.getMessage());
        }
        String openid = curInfo.getOpenId();

        if (openid != null && !"".equals(openid)) {
            List<FileInfo> list = fileInfoService.selectByPath(path, openid);
            for(FileInfo item: list) {
                if(ArrayUtils.contains(Constants.thumbnailTypes, item.getType())){
                    item.setThumbnail(selfPreview("thumbnails", item.getPath().replace("/",""), openid));
                }
            }
            logger.warn("文件列表:  " + list);
            return new RestResult<>(0, "查询成功", list);
        }
        return new RestResult<>(-1, "未找到该用户的文件信息");
    }

    @ApiOperation("或许分享的文件")
    @GetMapping("/getSharedFileList")
    public RestResult<?> getSharedFileList(String path, @RequestHeader("userSession") String userSession) {
        LoginStatusInfo curInfo = null;
        try {
            curInfo = userCheck.checkUserSession(userSession);
        } catch (Exception e) {
            logger.warn("catch");
            return new RestResult<>(-1, e.getMessage());
        }
        String openid = curInfo.getOpenId();
        List<FileInfo> list = null;
        if(openid != null) {
            list = fileInfoService.selectByPathExactly(path);
            for(FileInfo item: list) {
                if(ArrayUtils.contains(Constants.thumbnailTypes, item.getType())){
                    item.setThumbnail(selfPreview("thumbnails", item.getPath().replace("/",""), openid));
                }
            }
            return new RestResult<>(0, "查询成功", list);
        }
        return new RestResult<>(-1, "未找到该用户的文件信息");

    }
    @ApiOperation("文件预览")
    @GetMapping(value = "/preview")
    public RestResult<?> preview(String path, String filename, @RequestHeader("userSession") String userSession) {
        LoginStatusInfo curInfo = null;
        try {
            curInfo = userCheck.checkUserSession(userSession);
        } catch (Exception e) {
            return new RestResult<>(-1, e.getMessage());
        }
        String openid = curInfo.getOpenId();
        String bucket = openid.toLowerCase();
        return new RestResult<>(0, "查询成功", embeddedStorageService.preview(bucket, path, filename));
    }

    public String selfPreview(String path, String filename, String userOpenid) {
        String openid = userOpenid;
        String bucket = openid.toLowerCase();
        return embeddedStorageService.preview(bucket, path, filename);
    }

    @ApiOperation("创建目录")
    @RequestMapping(value = "/createDir", method = RequestMethod.POST)
    public ResponseEntity<String> createDir(String path, @RequestHeader("userSession") String userSession) {

        logger.warn("warn"+path);
        LoginStatusInfo curInfo = null;
        try {
            curInfo = userCheck.checkUserSession(userSession);
        } catch (Exception e) {
            return new ResponseEntity<>(NetStatus.CREATEDIR_ERR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        String openid = curInfo.getOpenId();
        String bucket = openid.toLowerCase();
        try {
            if (openid != null && !"".equals(openid)) {
                embeddedStorageService.createdir(path, bucket, openid);
//                embeddedStorageService.createdir(path, "chenyuanruomeng", "ChenYuanRuoMeng");
                return new ResponseEntity<>(NetStatus.CREATEDIR_SUC, HttpStatus.OK);
            }
            return new ResponseEntity<>(NetStatus.CREATEDIR_ERR, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(NetStatus.CREATEDIR_ERR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @ApiOperation("文件下载")
    @RequestMapping(value = "/downloadImages", method = RequestMethod.GET)
    public ResponseEntity<org.springframework.core.io.Resource> downloadImages(String filename, String path) {

        try {
            InputStream inputStream = embeddedStorageService.get(filename, (String) session.getAttribute("bucket"), path);

            InputStreamResource resource = new InputStreamResource(inputStream);

            ObjectStat stat = embeddedStorageService.stat(filename, (String) session.getAttribute("bucket"), path);

            String attachment = URLEncoder.encode(filename.substring(filename.lastIndexOf("/") + 1), "UTF-8");

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment;filename=" + attachment)
                    .contentLength(stat.length())
                    .contentType(MediaType.valueOf(stat.contentType()))
                    .body(resource);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

    }

    @ApiOperation("文件删除")
    @RequestMapping(value = "/DeleteImages", method = RequestMethod.POST)
    public ResponseEntity<String> deleteFiles(String filename, String path, @RequestHeader("userSession") String userSession) {
        logger.warn("warn"+path);
        LoginStatusInfo curInfo = null;
        try {
            curInfo = userCheck.checkUserSession(userSession);
        } catch (Exception e) {
            return new ResponseEntity<>(NetStatus.CREATEDIR_ERR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        String openid = curInfo.getOpenId();
        String bucket = openid.toLowerCase();
        try {
            logger.warn("执行文件删除***");
            if (openid != null && !"".equals(openid)) {
                embeddedStorageService.delete(filename, bucket, path, openid);
//                embeddedStorageService.delete(filename, "chenyuanruomeng", path, "ChenYuanRuoMeng");
                return new ResponseEntity<>(filename + NetStatus.DELETE_SUC, HttpStatus.OK);
            }
            return new ResponseEntity<>(NetStatus.DELETE_ERR, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(NetStatus.DELETE_ERR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation("文件重命名")
    @RequestMapping(value = "/fileRename", method = RequestMethod.POST)
    public ResponseEntity<String> fileRename(String newFileName, String path, @RequestHeader("userSession") String userSession) {
        LoginStatusInfo curInfo = null;
        try {
            curInfo = userCheck.checkUserSession(userSession);
        } catch (Exception e) {
            return new ResponseEntity<>(NetStatus.CREATEDIR_ERR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        String openid = curInfo.getOpenId();
        try {
            logger.warn("执行文件重命名");
            if (openid != null && !"".equals(openid)) {
                fileInfoService.renameFile(newFileName, path, openid);
                return new ResponseEntity<>(newFileName+NetStatus.RENAME_SUC, HttpStatus.OK);
            }
            return new ResponseEntity<>(NetStatus.RENAME_ERR, HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(NetStatus.RENAME_ERR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation("文件搜索")
    @RequestMapping(value = "/searchFile", method = RequestMethod.GET)
    public RestResult<?> searchFile(String keyWord, @RequestHeader("userSession") String userSession) {
        LoginStatusInfo curInfo = null;
        try {
            curInfo = userCheck.checkUserSession(userSession);
        } catch (Exception e) {
            return new RestResult<>(-1, e.getMessage());
        }
        String openid = curInfo.getOpenId();
        if (openid != null && !"".equals(openid)) {
            List<FileInfo> list = fileInfoService.searchByKeword(keyWord, openid);
            for(FileInfo item: list) {
                if(ArrayUtils.contains(Constants.thumbnailTypes, item.getType())){
                    item.setThumbnail(selfPreview("thumbnails", item.getPath().replace("/",""), openid));
                }
            }
            logger.warn("文件列表:  " + list);
            return new RestResult<>(0, "查询成功", list);
        }
        return new RestResult<>(-1, "未找到该用户的文件信息");
    }

    @ApiOperation("历史搜索关键词")
    @GetMapping("/getHistoryKeyword")
    public RestResult<?> getHistoryKeyWord(int limitCount ,@RequestHeader("userSession") String userSession) {
        LoginStatusInfo curInfo = null;
        try {
            curInfo = userCheck.checkUserSession(userSession);
        } catch (Exception e) {
            return new RestResult<>(-1, e.getMessage());
        }
        String openid = curInfo.getOpenId();
        if (openid != null && !"".equals(openid)) {
            List<HistoryKeyword> list = historyService.getKeyWordList(openid, limitCount);
            return new RestResult<>(0, "获取关键词列表成功", list);
        }
        return new RestResult<>(-1, "未找到该用户的文件信息");

    }

    @ApiOperation("清空关键词")
    @GetMapping("/clearKeyWords")
    public RestResult<?> clearAllKeyWords(@RequestHeader("userSession") String userSession) {
        LoginStatusInfo curInfo = null;
        try {
            curInfo = userCheck.checkUserSession(userSession);
        } catch (Exception e) {
            return new RestResult<>(-1, e.getMessage());
        }
        String openid = curInfo.getOpenId();
        if(openid != null && !"".equals(openid)) {
           int count = historyService.deleteAllKeyWords(openid);
           if(count > 0){
               return new RestResult<>(0, "清空关键词成功");
           }
        }
        return new RestResult<>(-1, "未找到该用户的文件信息");
    }
//    @ApiOperation("文件预览")
//    @RequestMapping(value = "/previewImages", method = RequestMethod.POST)
//    public String previewFiles(String filename, String path) {
//        return embeddedStorageService.preview(filename, (String) session.getAttribute("bucket"), path);
//    }

//    @ApiOperation("创建目录")
//    @RequestMapping(value = "/createDir", method = RequestMethod.POST)
//    public ResponseEntity<String> createDir(String path, String userId) {
//        try {
//            if (userId != null && !"".equals(userId)) {
//                embeddedStorageService.createdir(path, (String) session.getAttribute("bucket"), userId);
////                embeddedStorageService.createdir(path, "chenyuanruomeng", "ChenYuanRuoMeng");
//                return new ResponseEntity<>(NetStatus.CREATEDIR_SUC, HttpStatus.OK);
//            }
//            return new ResponseEntity<>(NetStatus.CREATEDIR_ERR, HttpStatus.INTERNAL_SERVER_ERROR);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ResponseEntity<>(NetStatus.CREATEDIR_ERR, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @ApiOperation("创建桶")
    @RequestMapping(value = "/createBucket", method = RequestMethod.POST)
    public ResponseEntity<String> createBucket(String bucket) {
        boolean res;
        try {
            res = createBucketServiceImpl.createBucket(bucket);
            if (res) {
                return new ResponseEntity<>(NetStatus.CREATEBUCKET_SUC, HttpStatus.OK);
            } else return new ResponseEntity<>(NetStatus.CREATEBUCKET_EXIST, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(NetStatus.CREATEBUCKET_ERR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @ApiOperation("文件列表")
//    @RequestMapping(value = "/list", method = RequestMethod.POST)
//    public RestResult<?> listBucket(String path, String userId) {
//        logger.warn(path);
//        logger.warn(userId);
//        if (userId != null && !"".equals(userId)) {
//            List<FileInfo> list = fileInfoService.selectByPath(path, userId);
//            logger.warn("文件列表:  " + list);
//            return new RestResult<>(0, "查询成功", list);
//        }
//        return new RestResult<>(-1, "未找到该用户的文件信息");
//    }

    @ApiOperation("目录删除")
    // @RequestMapping(value = "/deleteDir", method = RequestMethod.DELETE)
    @RequestMapping(value = "/deleteDir", method = RequestMethod.POST)
    public ResponseEntity<String> deleteDir(String path, @RequestHeader("userSession") String userSession) {
        logger.warn("warn"+path);
        LoginStatusInfo curInfo = null;
        try {
            curInfo = userCheck.checkUserSession(userSession);
        } catch (Exception e) {
            return new ResponseEntity<>(NetStatus.CREATEDIR_ERR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        String openid = curInfo.getOpenId();
        String bucket = openid.toLowerCase();
        try {
            logger.warn("执行文件夹删除***");
            if (openid != null && !"".equals(openid)) {
                fileInfoService.deleteFiles(path, bucket, openid);
                return new ResponseEntity<>(NetStatus.DELETEDIR_SUC, HttpStatus.OK);
            }
            return new ResponseEntity<>(NetStatus.DELETEDIR_ERR, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(NetStatus.DELETEDIR_ERR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 通过消息推送下载文件
     *
     * @param filename
     * @param path
     * @param userId
     * @return
     */
    @ApiOperation("文件下载-消息推送模式")
    @PostMapping("/downloadFile")
    public RestResult<?> DownloadFile(@RequestParam("filename") String filename, @RequestParam(value = "path", required = false) String path, @RequestParam("userId") String userId) {
        TokenSingleton instance = TokenSingleton.getInstance();
        String access_token = instance.getAccessToken(wechatInfo);
//        InputStream stream = embeddedStorageService.get(filename, "chenyuanruomeng", path);
        InputStream stream = embeddedStorageService.get(filename, (String) session.getAttribute("bucket"), path);
        String fileResult = HttpsUtils.sendFile(filename, "media", wechatInfo.getUploadUrl() + access_token + "&type=file&debug=1", stream, ContentType.APPLICATION_OCTET_STREAM);
        JSONObject result = JSONObject.parseObject(fileResult);
        logger.warn("上传临时素材----" + result);
        if (result != null && "ok".equals(result.get("errmsg"))) {
            String media_id = (String) result.get("media_id");
            Map<String, Object> map = new HashMap<>();
            map.put("touser", userId);
            map.put("agentid", wechatInfo.getAgentID());
            map.put("msgtype", "file");
            Map<Object, Object> fileMap = new HashMap<>();
            fileMap.put("media_id", media_id);
            map.put("file", fileMap);
            String send = HttpsUtils.sendByHttp(map, wechatInfo.getSendUrl() + access_token);
            logger.warn("消息推送----" + send);
            JSONObject sendState = JSONObject.parseObject(send);
            if (sendState != null && "ok".equals(sendState.get("errmsg"))) {
                return new RestResult<>(0, filename + ": 推送成功");
            } else if (sendState != null) {
                return new RestResult<>(-1, filename + ": " + sendState.get("errmsg").toString().split(",")[0]);
            }
        } else if (result != null) {
            return new RestResult<>(-1, filename + ": " + result.get("errmsg").toString().split(",")[0]);
        }
        return new RestResult<>(-1, filename + ": 未找到");
    }


    /**
     * 待完善+++数据库的表需要加新的统计属性
     */
    @ApiOperation("统计已用空间大小")
    @RequestMapping(value = "/Usage", method = RequestMethod.GET)
    public RestResult<?> Usage(@RequestHeader("userSession") String userSession) {
        LoginStatusInfo curInfo = null;
        try {
            curInfo = userCheck.checkUserSession(userSession);
        } catch (Exception e) {
            return new RestResult<>(-1, e.getMessage());
        }
        String openid = curInfo.getOpenId();
        BigDecimal aLong = fileInfoService.checkUserUsage(openid);
        if (aLong == null) {
            return new RestResult<>(0, "统计失败");
        }
        return new RestResult<>(0, "统计成功", aLong);
    }

    @ApiOperation("获取操作日志")
    @GetMapping("/getAllLogs")
    public RestResult<?> getAllLogs(int dayCount ,@RequestHeader("userSession") String userSession) {
        LoginStatusInfo curInfo = null;
        try {
            curInfo = userCheck.checkUserSession(userSession);
        } catch (Exception e) {
            return new RestResult<>(-1, e.getMessage());
        }
        String openid = curInfo.getOpenId();

        List<OperLog> list = new ArrayList<>();
        if(openid != null) {
            list = operLogService.getAllLog(openid, dayCount);
            return new RestResult<>(0, "查询成功", list);
        }
        return new RestResult<>(-1, "未找到该用户的文件信息");
    }



    @ApiOperation("获取文档预览地址")
    @GetMapping("/documentPreview")
    public RestResult<?> getDocumentPreviewUrl(String path, @RequestHeader("userSession") String userSession) {
        LoginStatusInfo curInfo = null;
        try {
            curInfo = userCheck.checkUserSession(userSession);
        } catch (Exception e) {
            return new RestResult<>(-1, e.getMessage());
        }
        String openid = curInfo.getOpenId();
        String bucket = openid.toLowerCase();
        if(openid != null) {
            return new RestResult<>(0, "查询成功", embeddedStorageService.preview(bucket, null, path));
        }
        return new RestResult<>(-1, "未找到该用户的文件信息");
    }

}
