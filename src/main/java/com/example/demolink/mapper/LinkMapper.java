package com.example.demolink.mapper;

import com.example.demolink.model.dto.request.CreateLinkRequest;
import com.example.demolink.model.dto.response.LinkResponse;
import com.example.demolink.model.dto.response.LinkStatsResponse;
import com.example.demolink.model.entity.LinkEntity;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class LinkMapper {

    public LinkEntity toEntity(CreateLinkRequest request) {
        LinkEntity link = new LinkEntity();
        link.setOriginalLink(request.getOriginalLink());
        link.setExpiresAt(request.getExpiresAt());
        return link;
    }

    public LinkResponse toResponse(LinkEntity link) {
        LinkResponse response = new LinkResponse();
        response.setId(link.getId());
        response.setOriginalLink(link.getOriginalLink());
        response.setShortLink(link.getShortLink());
        response.setShortUrl("http://localhost:8080/" + link.getShortLink());
        response.setLinkFollows(link.getLinkFollows());
        response.setExpiresAt(link.getExpiresAt());
        response.setActive(link.isActive());
        return response;
    }

    public LinkStatsResponse toStatsResponse(LinkEntity link) {

        LinkStatsResponse response = new LinkStatsResponse();
        response.setId(link.getId());
        response.setShortLink(link.getShortLink());
        response.setLinkFollows(link.getLinkFollows());
        response.setActive(link.isActive());
        return response;
    }

    public List<LinkResponse> toResponses(List<LinkEntity> links) {
        return links.stream()
                .map(this::toResponse)
                .toList();
    }
}
