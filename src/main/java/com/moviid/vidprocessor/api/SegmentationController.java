package com.moviid.vidprocessor.api;

import com.moviid.vidprocessor.bean.VideoRequest;
import com.moviid.vidprocessor.service.RunnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class SegmentationController {

    @Autowired
    private RunnerService runnerService;

    @PostMapping("/segment-video")
    public ResponseEntity<String> segmentVideo(@RequestBody VideoRequest videoRequest) {
        try {
            runnerService.runSegmentation(videoRequest.getBucket(),videoRequest.getPath());
            // Immediate response to indicate the process has started
            return ResponseEntity.accepted().body("Video segmentation process started.");
        } catch (Exception e) {
            // Handling the case where the asynchronous process couldn't be started
            return ResponseEntity.internalServerError().body("Error initiating video segmentation: " + e.getMessage());
        }
    }
}


