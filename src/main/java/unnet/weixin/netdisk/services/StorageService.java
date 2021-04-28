package unnet.weixin.netdisk.services;

import io.minio.ObjectStat;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @Author ckc
 * @Description
 * @Date 2020/9/27
 */

public interface StorageService {
    String getMethod();

    void put(InputStream stream, String bucket, long fileSize, String path, String filename, String contentType, String userId) throws Exception;

    InputStream get(String filename, String bucket, String path);


    void delete(String filename, String bucket, String path, String userId) throws Exception;

    ObjectStat stat(String filename, String bucket, String path);


    String getContentType(String filename, String bucket);

//    String preview(String filename, String bucket, String path);
    String preview(String bucket, String path, String filename);

    void createdir(String path, String bucket, String userId) throws Exception;

    Iterable<Result<Item>> list(String path, String bucket) throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, InvalidBucketNameException, ErrorResponseException;

    void removeDir(String path, String bucket, String userId) throws Exception;
}
