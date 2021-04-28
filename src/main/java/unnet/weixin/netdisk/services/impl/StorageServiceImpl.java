package unnet.weixin.netdisk.services.impl;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMethod;
import unnet.weixin.netdisk.entity.MinioInfo;
import unnet.weixin.netdisk.services.FileInfoService;
import unnet.weixin.netdisk.services.StorageService;
import unnet.weixin.netdisk.services.UserInfoService;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


/**
 * @Author ckc
 * @Description
 * @Date 2020/9/27
 */

@Service
public class StorageServiceImpl implements StorageService {

    @Resource
    MinioInfo minioInfo;

    @Resource
    private FileInfoService fileInfoService;

    @Resource
    private UserInfoService userInfoService;

    @Override
    public String getMethod() {
        return null;
    }

    /**
     * 文件上传
     */
    @Override
    @Transactional
    public void put(InputStream stream, String bucket, long fileSize, String path, String filename, String contentType, String openid) throws Exception {
        fileInfoService.addFileInfo(fileSize, path, filename, contentType, openid);
//        Long size = fileSize+userInfoService.selectUserUsage(userId);
//        userInfoService.updateUserUsage(userId,size);
        MinioClient client = initialClient();
        boolean isExist = client.bucketExists(bucket);
        System.out.println("添加fileInfo");
        if (!isExist) {
            System.out.println("桶不存在，需要创建桶");
            client.makeBucket(bucket);
        }
        assert false;
        if (path == null || path.equals("")) {
            client.putObject(PutObjectArgs.builder().bucket(bucket).object(filename).contentType(contentType).stream(
                    stream, fileSize, -1)
                    .build());
        } else {
            client.putObject(PutObjectArgs.builder().bucket(bucket).object(path).contentType(contentType).stream(
                    stream, fileSize, -1)
                    .build());
        }
    }

    /**
     * 文件下载
     */
    @Override
    public InputStream get(String filename, String bucket, String path) {
        MinioClient client;
        InputStream is;
        try {
            client = initialClient();
            if (path == null || path.equals("")) {
                is = client.getObject(GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(filename)
                        .build());
            } else {
                is = client.getObject(GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(path + "/" + filename)
                        .build());
            }
            return is;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 图片预览
     */
    @Override
    public String preview(String bucket, String path, String filename) {
        MinioClient client;
        String previewUrl="";
        try {
            client = initialClient();
            String object = (path == null || path.equals("")) ? filename: path+"/"+filename;
            previewUrl = client.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucket)
                    .object(object)
                    .expiry(24 * 60 * 60)
                    .build()
            );
        }catch (ErrorResponseException e) {
            e.printStackTrace();
        } catch (InsufficientDataException e) {
            e.printStackTrace();
        } catch (InternalException e) {
            e.printStackTrace();
        } catch (InvalidBucketNameException e) {
            e.printStackTrace();
        } catch (InvalidExpiresRangeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (XmlParserException e) {
            e.printStackTrace();
        } catch (ServerException e) {
            e.printStackTrace();
        }
        return previewUrl;
    }

    /**
     * 文件删除
     */
    @Override
    @Transactional
    public void delete(String filename, String bucket, String path, String openid) throws Exception {
        MinioClient client;
        client = initialClient();
        if (path == null || path.equals("")) {
         //   Long size = userInfoService.selectUserUsage(userId)-fileInfoService.getFileSize(filename,userId);
            fileInfoService.deleteFile(filename, openid);
          //  userInfoService.updateUserUsage(userId,size);
            client.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(filename).build());
        } else {
          //  Long size = userInfoService.selectUserUsage(userId)-fileInfoService.getFileSize(path + "/" + filename,userId);
           fileInfoService.deleteFile(path, openid);
         //   userInfoService.updateUserUsage(userId,size);
            client.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(path).build());
//            createdir(path, bucket, null);
        }
    }

    @Override
    public ObjectStat stat(String filename, String bucket, String path) {
        MinioClient client;
        InputStream is;
        try {
            client = initialClient();
            return client.statObject(StatObjectArgs.builder().bucket(bucket).object(path + "/" + filename).build());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文件预览
     */
//    @Override
//    public String preview(String filename, String bucket, String path) {
//        InputStream is = null;
//        byte[] data = null;
//        ByteArrayOutputStream os = new ByteArrayOutputStream();
//        try {
//            is = get(filename, bucket, path);
//            byte[] buffer = new byte[1024];
//            //每次读取的字符串长度，如果为-1，代表全部读取完毕
//            int len = 0;
//            while ((len = is.read(buffer)) != -1) {
//                os.write(buffer, 0, len);
//            }
//            is.close();
//            data = os.toByteArray();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {createdir
//                os.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return Base64.getEncoder().encodeToString(data);
//    }

    /**
     * 初始化客户端
     */
    private MinioClient initialClient() {
        MinioClient client = new MinioClient(minioInfo.getEndpoint(), minioInfo.getAccessKey(), minioInfo.getSecretKey());
        return client;
    }

    /**
     * 获取桶内文件类型
     */
    @Override
    public String getContentType(String filename, String bucket) {
        MinioClient client;
        try {
            client = initialClient();
            ObjectStat objectStat = client.statObject(bucket, filename);
            return objectStat.contentType();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    @Transactional
    public void createdir(String path, String bucket, String openid) throws Exception {
        MinioClient client;
        fileInfoService.createDir(path, openid);
        client = initialClient();
        client.putObject(
                PutObjectArgs.builder().bucket(bucket).object(path + "/").stream(
                        new ByteArrayInputStream(new byte[]{}), 0, -1)
                        .build());
    }


    @Override
    public Iterable<Result<Item>> list(String path, String bucket) {
        MinioClient client;
        Iterable<Result<Item>> results = null;
        try {
            client = initialClient();
            if (path == null || path.equals("")) {
                results = client.listObjects(
                        ListObjectsArgs.builder().bucket(bucket).prefix("").build());
            } else {
                results = client.listObjects(
                        ListObjectsArgs.builder().bucket(bucket).prefix(path + "/").build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    @Override
    @Transactional
    public void removeDir(String path, String bucket, String openid) throws Exception {
        int x = 0;
        int index = 0;
        char[] arr = path.toCharArray();
        for (; x < path.length(); x++) {
            if (String.valueOf(arr[x]).equals("/"))
                index = x;
        }
        String st = String.valueOf(arr, 0, index);
        MinioClient client;
        client = initialClient();
        Iterable<Result<Item>> results = list(path, bucket);
        if (results != null) {
            for (Result<Item> result : results) {
                String filename = URLDecoder.decode(result.get().objectName(), "UTF-8");
                if (result.get().isDir()) {
                    removeDir(filename.substring(0, filename.length() - 1), bucket, openid);
                } else {
                    client.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(filename).build());
                }
            }
        }
        client.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(path).build());
//        if (!"".equals(st)) {
//            createdir(st, bucket, null);
//        }
    }
}
