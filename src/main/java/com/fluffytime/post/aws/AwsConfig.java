package com.fluffytime.post.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsConfig {

    @Value("${aws.access-key-id}")
    private String accessKeyId;

    @Value("${aws.secret-access-key}")
    private String secretKey;

    @Value("${aws.s3.region}")
    private String region;

    @Bean
    public Region awsRegion() {
        // 주입받은 region 값을 이용해 Region 객체를 생성하여 빈으로 등록
        return Region.of(region);
    }

    @Bean
    public S3Client s3Client(Region region) {
        // 주입받은 accessKeyId와 secretKey를 이용해 AwsBasicCredentials 객체를 생성
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKeyId, secretKey);
        // S3Client를 생성하여 빈으로 등록. Region과 CredentialsProvider를 설정
        return S3Client.builder()
            .region(region)
            .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
            .build();
    }
}