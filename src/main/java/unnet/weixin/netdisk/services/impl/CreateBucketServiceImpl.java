package unnet.weixin.netdisk.services.impl;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unnet.weixin.netdisk.entity.MinioInfo;
import unnet.weixin.netdisk.services.CreateBucketService;
import java.io.IOException;

/**
 * @Author ckc
 * @Description
 * @Date 2020/9/27
 */

@Service
public class CreateBucketServiceImpl implements CreateBucketService {
    @Autowired
    MinioInfo minioInfo;
    @Override
    public boolean createBucket(String bucket) throws IOException, Exception {
        boolean res=false;
        MinioClient client = new MinioClient(minioInfo.getEndpoint(), minioInfo.getAccessKey(), minioInfo.getSecretKey());
        try {
            if(client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())){
                res = false;
            }else {
                client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                res = true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }
}
