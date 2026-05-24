package com.example.demolink.model.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinkStatsResponse {

    private Long id;
    private String shortLink;
    private Long linkFollows;
    private boolean active;
}
