package com.example.demolink.repository;

import com.example.demolink.model.entity.LinkEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkRepository extends JpaRepository<LinkEntity, Long> {

    Optional<LinkEntity> findByShortLink(String shortLink);

    List<LinkEntity> findAllByUserId(Long userId);

    List<LinkEntity> findAllByUserIdAndActiveTrue(Long userId);

    @Modifying
    @Query("""
            update LinkEntity l
            set l.linkFollows = l.linkFollows + 1
            where l.id = :id
            """)
    void incrementLinkFollows(@Param("id") Long id);

    boolean existsByShortLink(String shortLink);

}
