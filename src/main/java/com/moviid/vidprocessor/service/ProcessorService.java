package com.moviid.vidprocessor.service;

import com.moviid.vidprocessor.bean.FileSegment;
import com.moviid.vidprocessor.proxy.s3.S3VideoWriter;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class ProcessorService {

    @Autowired
    private S3VideoWriter s3VideoWriter;


    public void processSegment(FileSegment fileSegment, int totalSegments, String uniqueExecutionFolder) {
        Path tempFile = null;
        try {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(fileSegment.getFilePath());
            grabber.start();
            grabber.setTimestamp(fileSegment.getStartTimestamp());

            tempFile = Files.createTempFile("segment_", ".mp4");
            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(tempFile.toAbsolutePath().toString(), grabber.getImageWidth(), grabber.getImageHeight());

            configureRecorder(recorder, grabber);
            recorder.start();

            Frame frame;
            while ((frame = grabber.grabFrame()) != null) {
                if (grabber.getTimestamp() >= fileSegment.getEndTimestamp() && fileSegment.getSegmentNumber() != totalSegments - 1) {
                    break;
                }
                recorder.record(frame);
            }

            recorder.stop();
            recorder.release();
            grabber.stop();
            grabber.release();

            String fileKey = "Segmentation/" + uniqueExecutionFolder + "/segment_" + fileSegment.getSegmentNumber() + ".mp4";
            s3VideoWriter.writeVideoToS3("moviid" , fileKey, tempFile.toAbsolutePath().toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (tempFile != null) {
                try {
                    Files.delete(tempFile);
                } catch (IOException e) {
                    System.err.println("Warning: Failed to delete temporary file: " + tempFile.toAbsolutePath());
                    e.printStackTrace();
                }
            }
        }
    }


    private static void configureRecorder(FFmpegFrameRecorder recorder, FFmpegFrameGrabber grabber) {
        recorder.setFormat("mp4");
        recorder.setFrameRate(grabber.getFrameRate());
        recorder.setVideoBitrate(grabber.getVideoBitrate());
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
        recorder.setAudioChannels(grabber.getAudioChannels());
        recorder.setSampleRate(grabber.getSampleRate());
        recorder.setAudioBitrate(grabber.getAudioBitrate());
        recorder.setAudioCodec(grabber.getAudioCodec());
    }
}
