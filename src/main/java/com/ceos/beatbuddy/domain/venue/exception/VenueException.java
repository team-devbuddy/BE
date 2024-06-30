package com.ceos.beatbuddy.domain.venue.exception;

import com.ceos.beatbuddy.global.ResponseException;

public class VenueException extends ResponseException {

    public VenueException(VenueErrorCode venueErrorCode) {
        super(venueErrorCode.getMessage(), venueErrorCode.getHttpStatus());
    }

}
