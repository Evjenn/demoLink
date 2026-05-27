package com.example.demolink.mapper;

import com.example.demolink.model.dto.request.CreateLinkRequest;
import com.example.demolink.model.dto.response.LinkResponse;
import com.example.demolink.model.dto.response.LinkStatsResponse;
import com.example.demolink.model.dto.response.UserResponse;
import com.example.demolink.model.entity.LinkEntity;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LinkMapper {

    @Value("${app.base-url}")
    private String appBaseUrl;

    public LinkEntity toEntity(CreateLinkRequest request) {
        LinkEntity link = new LinkEntity();
        link.setOriginalLink(request.getOriginalLink());
        link.setExpiresAt(request.getExpiresAt());
        return link;
    }

    public LinkResponse toResponse(LinkEntity link) {

        if (link == null) {
            return null;
        }
        LinkResponse response = new LinkResponse();
        response.setId(link.getId());
        response.setOriginalLink(link.getOriginalLink());
        response.setShortLink(link.getShortLink());
        response.setShortUrl(appBaseUrl + "/" + link.getShortLink());
        response.setLinkFollows(link.getLinkFollows());
        response.setCreatedAt(link.getCreatedAt());
        response.setExpiresAt(link.getExpiresAt());
        response.setActive(link.isActive());

        if (link.getUser() != null) {
            UserResponse userDto = new UserResponse();
            userDto.setId(link.getUser().getId());
            userDto.setUsername(link.getUser().getUsername());
            response.setUser(userDto);
        }
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
