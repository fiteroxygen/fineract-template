package org.apache.fineract.infrastructure.configuration.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformException;

public class ExternalServiceForbiddenException extends AbstractPlatformException {

    public ExternalServiceForbiddenException(final String url) {
        super("error.msg.url.forbidden", "URL " + url + " not allowed");
    }
}
