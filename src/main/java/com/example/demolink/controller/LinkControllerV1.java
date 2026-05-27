package com.example.demolink.controller;

import com.example.demolink.mapper.LinkMapper;
import com.example.demolink.model.dto.request.CreateLinkRequest;
import com.example.demolink.model.dto.request.UpdateLinkRequest;
import com.example.demolink.model.dto.response.LinkResponse;
import com.example.demolink.model.dto.response.LinkStatsResponse;
import com.example.demolink.model.entity.LinkEntity;
import com.example.demolink.security.service.UserDetailsImpl;
import com.example.demolink.service.LinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/V1/links")
public class LinkControllerV1 {

    private final LinkService linkService;
    private final LinkMapper linkMapper;

    public LinkControllerV1(LinkService linkService, LinkMapper linkMapper) {
        this.linkService = linkService;
        this.linkMapper = linkMapper;
    }

    @PostMapping
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Create short URL.")
    @ApiResponse(responseCode = "201", description = "Short link created.")
    public ResponseEntity<LinkResponse> create(
            @Valid @RequestBody CreateLinkRequest request,
            @AuthenticationPrincipal UserDetailsImpl user) {

        LinkEntity saved = linkService.create(linkMapper.toEntity(request),user.getId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(linkMapper.toResponse(saved));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Search URL by link id.")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<LinkResponse> getById(@PathVariable Long id) {

        return ResponseEntity.ok(
                linkMapper.toResponse(linkService.getById(id))
        );
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active links of the current user")
    public ResponseEntity<List<LinkResponse>> getUserActiveLinks(
            @AuthenticationPrincipal UserDetailsImpl principalUser) {

        List<LinkEntity> activeLinks = linkService.getUserActiveLinks(principalUser.getId());
        List<LinkResponse> response = activeLinks.stream()
                .map(linkMapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all links by user id.")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<List<LinkResponse>> getAll(
            @AuthenticationPrincipal UserDetailsImpl user) {

        return ResponseEntity.ok(
                linkMapper.toResponses(linkService.getUserLinks(user.getId()))
        );
    }

    @GetMapping("/{id}/stats")
    @Operation(summary = "Get link follows by link id.")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<LinkStatsResponse> getStats(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        LinkEntity link = linkService.getStats(id, userDetails.getId());
        return ResponseEntity.ok(linkMapper.toStatsResponse(link));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update link.")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<LinkResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLinkRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        LinkEntity link = linkService.update(id, request, userDetails.getId());
        return ResponseEntity.ok(linkMapper.toResponse(link));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete link.")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        linkService.deleteById(id, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

}
