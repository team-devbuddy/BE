package com.ceos.beatbuddy.global;

import com.ceos.beatbuddy.domain.archive.exception.ArchiveErrorCode;
import com.ceos.beatbuddy.domain.heartbeat.exception.HeartbeatErrorCode;
import com.ceos.beatbuddy.domain.member.exception.MemberErrorCode;
import com.ceos.beatbuddy.domain.member.exception.MemberGenreErrorCode;
import com.ceos.beatbuddy.domain.member.exception.MemberMoodErrorCode;
import com.ceos.beatbuddy.domain.search.exception.SearchErrorCode;
import com.ceos.beatbuddy.domain.vector.exception.VectorErrorCode;
import com.ceos.beatbuddy.domain.venue.exception.VenueErrorCode;
import com.ceos.beatbuddy.domain.venue.exception.VenueGenreErrorCode;
import com.ceos.beatbuddy.domain.venue.exception.VenueMoodErrorCode;
import com.ceos.beatbuddy.global.config.oauth.exception.OauthErrorCode;

public class CustomException extends ResponseException {

    public CustomException(MemberErrorCode memberErrorCode) {
        super(memberErrorCode.getMessage(), memberErrorCode.getHttpStatus());
    }

    public CustomException(MemberMoodErrorCode memberMoodErrorCode) {
        super(memberMoodErrorCode.getMessage(), memberMoodErrorCode.getHttpStatus());
    }

    public CustomException(MemberGenreErrorCode memberMoodErrorCode) {
        super(memberMoodErrorCode.getMessage(), memberMoodErrorCode.getHttpStatus());
    }

    public CustomException(VenueErrorCode venueErrorCode) {
        super(venueErrorCode.getMessage(), venueErrorCode.getHttpStatus());
    }

    public CustomException(VenueGenreErrorCode venueGenreErrorCode) {
        super(venueGenreErrorCode.getMessage(), venueGenreErrorCode.getHttpStatus());
    }

    public CustomException(VenueMoodErrorCode venueMoodErrorCode) {
        super(venueMoodErrorCode.getMessage(), venueMoodErrorCode.getHttpStatus());
    }

    public CustomException(HeartbeatErrorCode heartbeatErrorCode) {
        super(heartbeatErrorCode.getMessage(), heartbeatErrorCode.getHttpStatus());
    }

    public CustomException(ArchiveErrorCode archiveErrorCode) {
        super(archiveErrorCode.getMessage(), archiveErrorCode.getHttpStatus());
    }

    public CustomException(VectorErrorCode vectorErrorCode){
        super(vectorErrorCode.getMessage(), vectorErrorCode.getHttpStatus());
    }

    public CustomException(SearchErrorCode searchErrorCode){
        super(searchErrorCode.getMessage(), searchErrorCode.getHttpStatus());
    }

    public CustomException(OauthErrorCode oauthErrorCode) {
        super(oauthErrorCode.getMessage(), oauthErrorCode.getHttpStatus());
    }
}
