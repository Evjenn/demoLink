package com.example.demolink.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demolink.exception.BaseException;
import com.example.demolink.model.dto.request.UpdateLinkRequest;
import com.example.demolink.model.entity.LinkEntity;
import com.example.demolink.model.entity.UserEntity;
import com.example.demolink.repository.LinkRepository;
import com.example.demolink.repository.UserRepository;
import com.example.demolink.service.generator.ShortLinkGenerator;
import com.example.demolink.service.impl.LinkServiceImpl;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class LinkServiceImplTest {

    @Mock
    private LinkRepository linkRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ShortLinkGenerator shortLinkGenerator;
    @InjectMocks
    private LinkServiceImpl linkService;

    @Test
    void shouldCreateLink() {

        Long userId = 1L;
        UserEntity user = new UserEntity();
        user.setId(userId);
        LinkEntity link = new LinkEntity();
        link.setOriginalLink("https://github.com");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(shortLinkGenerator.generate())
                .thenReturn("abc123");
        when(linkRepository.save(any(LinkEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LinkEntity saved = linkService.create(link, userId);

        assertEquals("abc123", saved.getShortLink());
        assertEquals(user, saved.getUser());
        assertEquals("https://github.com", saved.getOriginalLink());
        verify(linkRepository).save(link);
    }

    @Test
    void shouldSetDefaultExpirationWhenExpiresAtIsNull() {

        Long userId = 1L;
        UserEntity user = new UserEntity();
        user.setId(userId);
        LinkEntity link = new LinkEntity();
        link.setOriginalLink("https://google.com");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(shortLinkGenerator.generate())
                .thenReturn("short123");
        when(linkRepository.save(any(LinkEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LinkEntity saved = linkService.create(link, userId);

        assertEquals("short123", saved.getShortLink());
        assertEquals(user, saved.getUser());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {

        LinkEntity link = new LinkEntity();
        link.setOriginalLink("https://google.com");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        BaseException exception = assertThrows(BaseException.class, () -> {
            linkService.create(link, 1L);
        });
        assertEquals(org.springframework.http.HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void shouldUpdateLink() {
        // Given
        Long linkId = 1L;
        Long currentUserId = 2L;

        UserEntity owner = new UserEntity();
        owner.setId(currentUserId);

        LinkEntity existing = new LinkEntity();
        existing.setId(linkId);
        existing.setActive(true);
        existing.setUser(owner);

        UpdateLinkRequest request = new UpdateLinkRequest();
        request.setActive(false);
        request.setExpiresAt(LocalDateTime.now().plusDays(5));

        when(linkRepository.findById(linkId))
                .thenReturn(Optional.of(existing));
        when(linkRepository.save(any(LinkEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LinkEntity updated = linkService.update(linkId, request, currentUserId);

        assertEquals(false, updated.isActive());
        assertEquals(request.getExpiresAt(), updated.getExpiresAt());
    }

    @Test
    void shouldReturnLinkById() {

        LinkEntity link = new LinkEntity();
        link.setId(1L);

        when(linkRepository.findById(1L))
                .thenReturn(Optional.of(link));
        LinkEntity result = linkService.getById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void shouldThrowExceptionWhenLinkNotFound() {

        Long targetLinkId = 1L;
        Long currentUserId = 2L;
        UpdateLinkRequest request = new UpdateLinkRequest();

        when(linkRepository.findById(targetLinkId)).thenReturn(Optional.empty());
        BaseException exception = assertThrows(BaseException.class, () -> {
            linkService.update(targetLinkId, request, currentUserId);
        });

        assertEquals(404, exception.getStatus().value());

    }

    @Test
    void shouldReturnUserLinks() {

        List<LinkEntity> links = List.of(
                new LinkEntity(),
                new LinkEntity()
        );

        when(linkRepository.findAllByUserId(1L))
                .thenReturn(links);
        List<LinkEntity> result = linkService.getUserLinks(1L);

        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnOriginalLink() {

        LinkEntity link = new LinkEntity();

        link.setId(100L);
        link.setOriginalLink("https://github.com");
        link.setShortLink("abc123");
        link.setActive(true);
        link.setExpiresAt(LocalDateTime.now().plusDays(1));
        link.setLinkFollows(0L);

        when(linkRepository.findByShortLink("abc123"))
                .thenReturn(Optional.of(link));

        String result = linkService.getOriginalLink("abc123");
        assertEquals("https://github.com", result);
        verify(linkRepository, times(1)).incrementLinkFollows(100L);
    }

    @Test
    void shouldThrowExceptionWhenLinkInactive() {

        LinkEntity link = new LinkEntity();
        link.setActive(false);
        link.setExpiresAt(LocalDateTime.now().plusDays(1));

        when(linkRepository.findByShortLink("abc123"))
                .thenReturn(Optional.of(link));

        BaseException exception = assertThrows(BaseException.class, () -> {
            linkService.getOriginalLink("abc123");
        });
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void shouldDeleteLinkById() {

        Long linkId = 1L;
        Long currentUserId = 2L;
        UserEntity owner = new UserEntity();
        owner.setId(currentUserId);

        LinkEntity existingLink = new LinkEntity();
        existingLink.setId(linkId);
        existingLink.setUser(owner);

        when(linkRepository.findById(linkId)).thenReturn(Optional.of(existingLink));

        linkService.deleteById(linkId, currentUserId);
        verify(linkRepository).delete(existingLink);
    }

    @Test
    void shouldThrowExceptionWhenLinkIsExpired() {
        LinkEntity expiredLink = new LinkEntity();
        expiredLink.setActive(true);
        expiredLink.setExpiresAt(LocalDateTime.now().minusDays(1));

        when(linkRepository.findByShortLink("oldCode")).thenReturn(Optional.of(expiredLink));

        BaseException exception = assertThrows(BaseException.class, () -> {
            linkService.getOriginalLink("oldCode");
        });
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

    }

    @Test
    void shouldThrowExceptionWhenOriginalLinkIsInvalidUrl() {

        LinkEntity badLink = new LinkEntity();
        badLink.setOriginalLink("not-a-real-url");

        BaseException exception = assertThrows(BaseException.class, () -> {
            linkService.create(badLink, 1L);
        });
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void shouldGenerateNewShortLink_whenCollisionOccurs() {

        Long userId = 1L;
        UserEntity user = new UserEntity();
        user.setId(userId);
        LinkEntity link = new LinkEntity();
        link.setOriginalLink("https://google.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(shortLinkGenerator.generate()).thenReturn("collisionCode", "uniqueCode");
        when(linkRepository.existsByShortLink("collisionCode")).thenReturn(true);
        when(linkRepository.save(any(LinkEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LinkEntity saved = linkService.create(link, userId);
        assertEquals("uniqueCode", saved.getShortLink());
        verify(linkRepository, times(1)).existsByShortLink("collisionCode");
        verify(shortLinkGenerator, times(2)).generate();
    }
}
