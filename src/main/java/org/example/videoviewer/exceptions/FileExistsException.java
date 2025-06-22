package org.example.videoviewer.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class FileExistsException extends BaseException {
    public FileExistsException() {
        super(String.format(Constants.Templates.FILE_ALREADY_EXISTS_ERROR_TEMPLATE, "Unknown"));
    }

    public FileExistsException(final String fileName) {
        super(String.format(Constants.Templates.FILE_ALREADY_EXISTS_ERROR_TEMPLATE, fileName));
    }
}
