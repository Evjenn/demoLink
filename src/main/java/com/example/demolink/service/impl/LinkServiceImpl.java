package com.example.demolink.service.impl;

import com.example.demolink.exception.BaseException;
import com.example.demolink.model.dto.request.UpdateLinkRequest;
import com.example.demolink.model.entity.LinkEntity;
import com.example.demolink.model.entity.UserEntity;
import com.example.demolink.repository.LinkRepository;
import com.example.demolink.repository.UserRepository;
import com.example.demolink.service.LinkService;
import com.example.demolink.service.generator.ShortLinkGenerator;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        try {
            if (link.getOriginalLink() == null || link.getOriginalLink().isBlank()) {
                throw new BaseException("Original URL cannot be empty", HttpStatus.BAD_REQUEST);
            }
            new URI(link.getOriginalLink()).toURL();
        } catch (Exception e) {
            throw new BaseException("The provided string '"
                    + link.getOriginalLink() + "' is not a valid URL", HttpStatus.BAD_REQUEST);
        }
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new BaseException("User not found", HttpStatus.NOT_FOUND));
        if (link.getExpiresAt() == null) {
            link.setExpiresAt(LocalDateTime.now().plusDays(10));
        }
        link.setUser(user);

        String code = shortLinkGenerator.generate();
        if (linkRepository.existsByShortLink(code)) {
            code = shortLinkGenerator.generate();
        }
        link.setShortLink(code);

        try {
            return linkRepository.save(link);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new BaseException("Short link collision occurred. Please try again.",
                    HttpStatus.CONFLICT);
        }
    }

    @Override
    public LinkEntity update(Long id, UpdateLinkRequest request, Long currentUserId) {

        LinkEntity updatedLink = getById(id);
        validateLinkOwnership(updatedLink, currentUserId);
        if (request.getExpiresAt() != null) {
            updatedLink.setExpiresAt(request.getExpiresAt());
        }

        updatedLink.setActive(request.isActive());
        return linkRepository.save(updatedLink);
    }

    @Override
    public LinkEntity getById(Long id) {
        return linkRepository.findById(id)
                .orElseThrow(() -> new BaseException("Link not found", HttpStatus.NOT_FOUND));
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
    @Transactional
    public String getOriginalLink(String shortLink) {

        LinkEntity link = linkRepository.findByShortLink(shortLink)
                .orElseThrow(() -> new BaseException("Link not found", HttpStatus.NOT_FOUND));
        if (!link.isActive()) {
            throw new BaseException("Link is inactive", HttpStatus.BAD_REQUEST);
        }
        if (link.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BaseException("Link has expired", HttpStatus.BAD_REQUEST);
        }

        linkRepository.incrementLinkFollows(link.getId());
        return link.getOriginalLink();
    }

    @Override
    public void deleteById(Long id, Long currentUserId) {

        LinkEntity link = getById(id);
        validateLinkOwnership(link, currentUserId);
        linkRepository.delete(link);
    }

    @Override
    public LinkEntity getStats(Long id, Long currentUserId) {

        LinkEntity link = getById(id);
        validateLinkOwnership(link, currentUserId);
        return link;
    }

    private void validateLinkOwnership(LinkEntity link, Long currentUserId) {
        if (link.getUser() == null || !link.getUser().getId().equals(currentUserId)) {
            throw new BaseException("You do not have permission to manage this link",
                    HttpStatus.FORBIDDEN);
        }
    }
}
