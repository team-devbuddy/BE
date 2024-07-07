package com.ceos.beatbuddy.domain.search.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Builder
@RedisHash(value="search", timeToLive=60)
public class Search {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long searchId;

    public String searchTerm;

}
