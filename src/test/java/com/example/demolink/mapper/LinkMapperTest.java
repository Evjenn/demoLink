package com.example.demolink.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demolink.model.dto.response.LinkResponse;
import com.example.demolink.model.entity.LinkEntity;
import org.junit.jupiter.api.Test;

class LinkMapperTest {

    private final LinkMapper mapper = new LinkMapper() {
        @Override
        public LinkResponse toResponse(LinkEntity entity) {
            if (entity == null) {
                return null;
            }
            LinkResponse response = new LinkResponse();
            response.setId(entity.getId());
            response.setId(entity.getId());
            response.setOriginalLink(entity.getOriginalLink());
            response.setShortLink(entity.getShortLink());
            response.setShortUrl("http://localhost:8080/" + entity.getShortLink());
            response.setLinkFollows(entity.getLinkFollows());
            response.setExpiresAt(entity.getExpiresAt());
            return response;
        }
    };

    @Test
    void shouldMapEntityToResponse() {

        LinkEntity entity = new LinkEntity();
        entity.setId(100L);

        LinkResponse response = mapper.toResponse(entity);
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);
    }
}
