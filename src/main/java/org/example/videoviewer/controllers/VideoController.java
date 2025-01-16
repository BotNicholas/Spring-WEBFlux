package org.example.videoviewer.controllers;

import org.example.videoviewer.services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class VideoController {
    @Autowired
    VideoService videoService;

    @GetMapping(value = "/default/video/{fileName}", produces = "video/mp4")
    public Mono<Resource> defaultVideo(@PathVariable("fileName") String fileName, @RequestHeader("Range") String range) {
        System.out.println("Playing video in range " + range);
        return videoService.getVideo(fileName);
    }
}
