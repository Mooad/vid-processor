package com.moviid.vidprocessor.service;

import com.moviid.vidprocessor.bean.FileSegment;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;
import java.util.concurrent.ExecutorService;

@Component
public class SegmentorService {


    @Autowired ProcessorService processorService;
    static {
        // Set FFmpeg log level to suppress warnings and info messages
        avutil.av_log_set_level(avutil.AV_LOG_ERROR);
    }

    public void splitVideo(File videoFile, ExecutorService executor) {
        String filePath = videoFile.getPath();
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(filePath)) {
            grabber.start();

            // Calculate total duration in seconds
            long totalDurationSeconds = grabber.getLengthInTime() / 1000000; // Convert microseconds to seconds

            // Use ParamService to calculate the number of segments based on video duration
            int totalSegments = ParamService.calculateNumberOfSegments(totalDurationSeconds);

            long totalDurationMicros = grabber.getLengthInTime();
            long segmentDurationMicros = totalDurationMicros / totalSegments;

            // Generate a unique ID for this segmentation task
            String uniqueFolderName = "segment_" + new Date().getTime();

            for (int i = 0; i < totalSegments; i++) {
                final long startTimestamp = i * segmentDurationMicros;
                final long endTimestamp = (i + 1 < totalSegments) ? (i + 1) * segmentDurationMicros : totalDurationMicros;

                FileSegment segment = new FileSegment(filePath, i, startTimestamp, endTimestamp);

                // Pass the uniqueFolderName along with each segment to the processing method
                executor.submit(() -> processorService.processSegment(segment, totalSegments, uniqueFolderName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
