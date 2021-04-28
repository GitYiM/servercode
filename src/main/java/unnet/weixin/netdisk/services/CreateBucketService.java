package unnet.weixin.netdisk.services;

import io.minio.errors.*;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @Author ckc
 * @Description
 * @Date 2020/9/27
 */

public interface CreateBucketService {
    boolean createBucket(String bucket) throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, InvalidBucketNameException, ErrorResponseException, RegionConflictException, Exception;
}
