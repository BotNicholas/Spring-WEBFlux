package org.example.videoviewer.services;

import org.example.videoviewer.models.File;
import org.example.videoviewer.models.FileType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Service
public class VideoService {
//    private static final String VIDEO_FILE = "C:/Users/Nicholas/Documents/%s.mp4";
//
////    @Autowired
////    private ResourceLoader resourceLoader;
//
//    public Mono<Resource> getVideo(String filename) throws IOException {
//        var video = new FileSystemResource(String.format(VIDEO_FILE, filename));
////        System.out.println("+\n\t\t" + video.contentLength() + "\n");
//
//        return Mono.fromSupplier(() -> video);
//
//
//
//
//
//
//
//        //        return Mono.fromSupplier(() -> resourceLoader.getResource(VIDEO_FILE));
//    }

@Value("${home.dir}")
private String homeDir;

public Mono<Resource> getVideo(String videoPath) throws IOException {
        var video = new FileSystemResource(getPath(videoPath));
        return Mono.fromSupplier(() -> video);
}

    private String getPath(String path) {
        if (!homeDir.substring(homeDir.length() - 1).equals("/")) {
            return homeDir + "/" + path.replaceFirst("/", "");
        }

        return homeDir + path.replaceFirst("/", "");
    }
}
