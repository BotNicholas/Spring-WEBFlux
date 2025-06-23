package org.example.videoviewer.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class FileNotFoundException extends BaseException {
    public FileNotFoundException() {
        super(String.format(Constants.Templates.FILE_NOT_FOUND_ERROR_TEMPLATE, "Unknown"));
    }

    public FileNotFoundException(final String fileName) {
        super(String.format(Constants.Templates.FILE_NOT_FOUND_ERROR_TEMPLATE, fileName));
    }
}
