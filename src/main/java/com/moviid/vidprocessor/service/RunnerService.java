package com.moviid.vidprocessor.service;


import com.moviid.vidprocessor.proxy.s3.S3VideoReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Component
public class RunnerService {

    @Autowired
    private S3VideoReader s3VideoReader;

    @Autowired
    private SegmentorService segmentorService;

    @Async
    public void runSegmentation(String s3Bucket, String s3Key) {
        long startTime = System.currentTimeMillis();

        try {
            // Download the video file from S3
            byte[] videoData = s3VideoReader.readVideoFromS3(s3Bucket, s3Key);
            if (videoData == null) {
                System.err.println("Failed to download video from S3.");
                return;
            }

            // Save the video file to a temporary location
            File videoFile = saveVideoFile(videoData, s3Key);

            ExecutorService executor = Executors.newFixedThreadPool(10);
            segmentorService.splitVideo(videoFile, executor);
            executor.shutdown();
            while (!executor.isTerminated()) {
                // Wait for termination
            }

            // Optionally, clean up the temporary file
            videoFile.delete();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            long endTime = System.currentTimeMillis();
            System.out.println("Total Execution Time: " + (endTime - startTime) + " milliseconds");
        }
    }

    private File saveVideoFile(byte[] videoData, String fileName) throws IOException {
        String fileExtension = ".tmp"; // Default extension if none is found

        // Extract the file extension if present
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            fileExtension = fileName.substring(lastDotIndex); // Includes the dot
        }

        // Ensure the prefix is valid by removing any invalid characters
        String validPrefix = fileName.replaceAll("[^a-zA-Z0-9_\\-]", "");
        if (validPrefix.length() > 3) {
            validPrefix = validPrefix.substring(0, 3); // Limit prefix length to 3 for safety
        } else if (validPrefix.isEmpty()) {
            validPrefix = "vid"; // Fallback prefix if sanitization leaves it empty
        }

        Path tempFile = Files.createTempFile(validPrefix, fileExtension);
        Files.write(tempFile, videoData);
        return tempFile.toFile();
    }

}
