package com.example.demolink.repository;

import com.example.demolink.model.entity.LinkEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkRepository extends JpaRepository<LinkEntity, Long> {

    Optional<LinkEntity> findByShortLink(String shortLink);

    List<LinkEntity> findAllByUserId(Long userId);

    List<LinkEntity> findAllByUserIdAndActiveTrue(Long userId);
}
