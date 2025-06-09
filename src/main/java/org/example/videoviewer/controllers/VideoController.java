package org.example.videoviewer.controllers;

import org.example.videoviewer.models.File;
import org.example.videoviewer.services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Base64;

@CrossOrigin(origins = "*")
@RestController
public class VideoController {
    @Autowired
    VideoService videoService;

    //todo: must be removed
    @GetMapping(value = "/default/video/{fileName}", produces = "video/mp4")
    public Mono<Resource> defaultVideo(@PathVariable("fileName") String fileName, @RequestHeader("Range") String range) throws IOException {
        System.out.println("Playing video in range " + range);
        return videoService.getVideo("AWS\\Associate\\AWS Certified Developer - Associate\\" + fileName + ".mp4");
    }

    @GetMapping(value = "/video/play", produces = "video/mp4")
    public Mono<Resource> playVideo(@RequestParam("video") String videoPath, @RequestHeader("Range") String range) throws IOException {
        System.out.println("Playing video in range " + range);
        System.out.println("Requested video: " + new String(Base64.getDecoder().decode(videoPath)));
        try {
            return videoService.getVideo(new String(Base64.getDecoder().decode(videoPath)));
        } catch (Exception e) {
            if (e.getMessage().contains("Illegal base64 character")) {
                throw new RuntimeException("Illegal base64 encoding");
            }
            throw new RuntimeException(e);
        }
    }
}
