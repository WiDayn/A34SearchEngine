package cn.edu.hhu.a34searchengine.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Slf4j
public class MinioConfig
{
    @Value("${minio.endpoint}")
    private String endPoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("#{'${minio.buckets}'.split(' *, *')}")
    private List<String> buckets;

    @Bean
    public MinioClient getMinioClient()
    {
        return MinioClient.builder()
                .endpoint(endPoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    //每次项目启动都会执行一次,中途bucket被删除不会再自动创建
    @PostConstruct
    protected void initializeBuckets() throws Exception
    {
        MinioClient minioClient=MinioClient.builder()
                .endpoint(endPoint)
                .credentials(accessKey, secretKey)
                .build();
        for(String bucketName : buckets)
        {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found)
            {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.warn("Bucket '" + bucketName + "' didn't exist! Just created. ");
            }
            else
            {
                log.info("Bucket '" + bucketName + "' exists.");
            }
        }
    }
}
