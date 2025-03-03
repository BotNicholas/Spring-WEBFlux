package org.example.videoviewer.services;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class FFMpegImageExtractor {
    public byte[] getFirstFrameFromVideo(String videoPath, String tmpImagePath) throws IOException, InterruptedException {
        Runtime rt = Runtime.getRuntime();
        var pr = rt.exec("ffmpeg -i \"" + videoPath + "\" -frames:v 1 \"" + tmpImagePath + "/frame%03d.png\"");
        pr.waitFor();
        var imageBytes = Files.readAllBytes(Paths.get(tmpImagePath + "/frame001.png"));
        Files.delete(Paths.get(tmpImagePath + "/frame001.png"));
        return imageBytes;
    }
}
