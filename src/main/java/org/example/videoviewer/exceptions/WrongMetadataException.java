package org.example.videoviewer.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class WrongMetadataException extends BaseException {
    public WrongMetadataException() {
        super(Constants.METADATA_EXCEPTION_ERROR_MESSAGE);
    }

    public WrongMetadataException(final String reason) {
        super(String.format(Constants.Templates.METADATA_EXCEPTION_TEMPLATE, reason));
    }
}
