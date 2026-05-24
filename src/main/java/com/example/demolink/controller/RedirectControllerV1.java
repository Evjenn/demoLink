package com.example.demolink.controller;

import com.example.demolink.service.LinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedirectControllerV1 {

    private final LinkService linkService;

    public RedirectControllerV1(LinkService linkService) {
        this.linkService = linkService;
    }

    @GetMapping("/{shortLink}")
    @Operation(
            summary = "Redirect to original URL"
    )
    @ApiResponse(responseCode = "302", description =
            "Successful redirection. The actual target URL is located in the Location header.")
    @ApiResponse(responseCode = "404", description = "Short link not found in the database.")
    public ResponseEntity<Void> redirect(
            @PathVariable String shortLink) {

        String originalLink = linkService.getOriginalLink(shortLink);
        return ResponseEntity
                .status(302)
                .header(HttpHeaders.LOCATION, originalLink)
                .build();
    }
}
