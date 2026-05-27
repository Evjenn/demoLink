package com.example.demolink.service;

import com.example.demolink.model.dto.request.UpdateLinkRequest;
import com.example.demolink.model.entity.LinkEntity;
import java.util.List;

public interface LinkService {

    LinkEntity create(LinkEntity link, Long id);

    LinkEntity update(Long id, UpdateLinkRequest request, Long currentUserId);

    LinkEntity getById(Long id);

    List<LinkEntity> getUserLinks(Long userId);

    List<LinkEntity> getUserActiveLinks(Long userId);

    String getOriginalLink(String shortLink);

    void deleteById(Long id, Long currentUserId);

    LinkEntity getStats(Long id, Long currentUserId);
}
