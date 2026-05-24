package com.example.demolink.service.impl;

import com.example.demolink.exception.NotFoundException;
import com.example.demolink.model.dto.request.UpdateLinkRequest;
import com.example.demolink.model.entity.LinkEntity;
import com.example.demolink.model.entity.UserEntity;
import com.example.demolink.repository.LinkRepository;
import com.example.demolink.repository.UserRepository;
import com.example.demolink.service.LinkService;
import com.example.demolink.service.generator.ShortLinkGenerator;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LinkServiceImpl implements LinkService {

    private final LinkRepository linkRepository;
    private final ShortLinkGenerator shortLinkGenerator;
    private final UserRepository userRepository;

    public LinkServiceImpl(LinkRepository linkRepository,
                           ShortLinkGenerator shortLinkGenerator,
                           UserRepository userRepository) {
        this.linkRepository = linkRepository;
        this.shortLinkGenerator = shortLinkGenerator;
        this.userRepository = userRepository;
    }

    @Override
    public LinkEntity create(LinkEntity link, Long id) {

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (link.getExpiresAt() == null) {
            link.setExpiresAt(LocalDateTime.now().plusDays(10));
        }
        link.setShortLink(shortLinkGenerator.generate());
        link.setUser(user);
        return linkRepository.save(link);
    }

    @Override
    public LinkEntity update(Long id, UpdateLinkRequest request) {

        LinkEntity updatedLink = getById(id);
        if (request.getExpiresAt() != null) {
            updatedLink.setExpiresAt(request.getExpiresAt());
        }

        updatedLink.setActive(request.isActive());
        return linkRepository.save(updatedLink);
    }

    @Override
    public LinkEntity getById(Long id) {
        return linkRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Link not found"));
    }

    @Override
    public List<LinkEntity> getUserLinks(Long userId) {
        return linkRepository.findAllByUserId(userId);
    }

    @Override
    public List<LinkEntity> getUserActiveLinks(Long userId) {
        return linkRepository.findAllByUserIdAndActiveTrue(userId);
    }

    @Override
    public String getOriginalLink(String shortLink) {

        LinkEntity link = linkRepository.findByShortLink(shortLink)
                .orElseThrow(() -> new NotFoundException("Link not found"));
        if (!link.isActive()) {
            throw new RuntimeException("Link inactive");
        }
        if (link.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Link expired");
        }

        link.setLinkFollows(link.getLinkFollows() + 1);
        linkRepository.save(link);
        return link.getOriginalLink();
    }

    @Override
    public void deleteById(Long id) {
        linkRepository.deleteById(id);
    }
}
