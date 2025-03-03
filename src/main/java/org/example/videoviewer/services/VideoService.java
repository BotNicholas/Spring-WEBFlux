package org.example.videoviewer.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Service
public class VideoService {
    private static final String VIDEO_FILE = "C:/Users/Nicholas/Documents/%s.mp4";

//    @Autowired
//    private ResourceLoader resourceLoader;

    public Mono<Resource> getVideo(String filename) throws IOException {
        var video = new FileSystemResource(String.format(VIDEO_FILE, filename));
//        System.out.println("+\n\t\t" + video.contentLength() + "\n");

        return Mono.fromSupplier(() -> video);







        //        return Mono.fromSupplier(() -> resourceLoader.getResource(VIDEO_FILE));
    }
}
