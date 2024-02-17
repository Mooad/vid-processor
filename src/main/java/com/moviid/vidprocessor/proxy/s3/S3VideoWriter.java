package com.moviid.vidprocessor.proxy.s3;

import com.moviid.vidprocessor.config.AwsProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.nio.file.Paths;
@Component
public class S3VideoWriter {

    private AwsProperties awsProperties;

    @Autowired
    public S3VideoWriter(AwsProperties awsProperties) {
        this.awsProperties = awsProperties;
    }

    public void writeVideoToS3(String bucketName, String fileKey, String filePath) {
        S3Client s3 = S3Client.builder()
                .region(Region.of(awsProperties.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(
                        awsProperties.getAccessKeyId(), awsProperties.getSecretAccessKey())))
                .build();

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();

            s3.putObject(putObjectRequest, RequestBody.fromFile(Paths.get(filePath)));
            System.out.println("File uploaded successfully.");
        } catch (S3Exception e) {
            e.printStackTrace();
        }
    }
}