package com.example.demolink.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demolink.exception.NotFoundException;
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

        // given
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

        // when
        LinkEntity saved = linkService.create(link, userId);

        // then
        assertEquals("abc123", saved.getShortLink());
        assertEquals(user, saved.getUser());
        assertEquals("https://github.com", saved.getOriginalLink());

        verify(linkRepository).save(link);
    }

    @Test
    void shouldSetDefaultExpiration_whenExpiresAtIsNull() {

        // given
        Long userId = 1L;

        UserEntity user = new UserEntity();
        user.setId(userId);

        LinkEntity link = new LinkEntity();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(shortLinkGenerator.generate())
                .thenReturn("short123");

        when(linkRepository.save(any(LinkEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        LinkEntity saved = linkService.create(link, userId);

        // then
        assertEquals("short123", saved.getShortLink());
        assertEquals(user, saved.getUser());
    }

    @Test
    void shouldThrowException_whenUserNotFound() {

        // given
        LinkEntity link = new LinkEntity();
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        // when + then
        assertThrows(
                NotFoundException.class,
                () -> linkService.create(link, 1L)
        );
    }

    @Test
    void shouldUpdateLink() {

        // given
        Long linkId = 1L;
        LinkEntity existing = new LinkEntity();
        existing.setId(linkId);
        existing.setActive(true);

        UpdateLinkRequest request = new UpdateLinkRequest();
        request.setActive(false);
        request.setExpiresAt(LocalDateTime.now().plusDays(5));

        when(linkRepository.findById(linkId))
                .thenReturn(Optional.of(existing));
        when(linkRepository.save(any(LinkEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        // when
        LinkEntity updated = linkService.update(linkId, request);
        // then
        assertEquals(false, updated.isActive());
        assertEquals(request.getExpiresAt(), updated.getExpiresAt());
    }

    @Test
    void shouldReturnLinkById() {

        // given
        LinkEntity link = new LinkEntity();
        link.setId(1L);

        when(linkRepository.findById(1L))
                .thenReturn(Optional.of(link));
        // when
        LinkEntity result = linkService.getById(1L);
        // then
        assertEquals(1L, result.getId());
    }

    @Test
    void shouldThrowException_whenLinkNotFound() {

        // given
        when(linkRepository.findById(1L))
                .thenReturn(Optional.empty());
        // when + then
        assertThrows(
                NotFoundException.class,
                () -> linkService.getById(1L)
        );
    }

    @Test
    void shouldReturnUserLinks() {

        // given
        List<LinkEntity> links = List.of(
                new LinkEntity(),
                new LinkEntity()
        );

        when(linkRepository.findAllByUserId(1L))
                .thenReturn(links);
        // when
        List<LinkEntity> result = linkService.getUserLinks(1L);
        // then
        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnOriginalLink() {

        // given
        LinkEntity link = new LinkEntity();

        link.setOriginalLink("https://github.com");
        link.setShortLink("abc123");
        link.setActive(true);
        link.setExpiresAt(LocalDateTime.now().plusDays(1));
        link.setLinkFollows(0L);

        when(linkRepository.findByShortLink("abc123"))
                .thenReturn(Optional.of(link));
        when(linkRepository.save(any(LinkEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        // when
        String result = linkService.getOriginalLink("abc123");
        // then
        assertEquals("https://github.com", result);
        assertEquals(1, link.getLinkFollows());
    }

    @Test
    void shouldThrowException_whenLinkInactive() {

        // given
        LinkEntity link = new LinkEntity();
        link.setActive(false);
        link.setExpiresAt(LocalDateTime.now().plusDays(1));

        when(linkRepository.findByShortLink("abc123"))
                .thenReturn(Optional.of(link));
        // when + then
        assertThrows(
                RuntimeException.class,
                () -> linkService.getOriginalLink("abc123")
        );
    }

    @Test
    void shouldThrowException_whenLinkExpired() {

        // given
        LinkEntity link = new LinkEntity();
        link.setActive(true);
        link.setExpiresAt(LocalDateTime.now().minusDays(1));

        when(linkRepository.findByShortLink("abc123"))
                .thenReturn(Optional.of(link));
        // when + then
        assertThrows(
                RuntimeException.class,
                () -> linkService.getOriginalLink("abc123")
        );
    }

    @Test
    void shouldDeleteLinkById() {

        // when
        linkService.deleteById(1L);
        // then
        verify(linkRepository).deleteById(1L);
    }

}
