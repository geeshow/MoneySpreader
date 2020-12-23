package com.geeshow.kakaopay.MoneySpreader.controller;

import com.geeshow.kakaopay.MoneySpreader.service.SpreaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static com.geeshow.kakaopay.MoneySpreader.dto.SpreaderDto.Request;
import static com.geeshow.kakaopay.MoneySpreader.dto.SpreaderDto.Response;

@RestController
@RequestMapping("/v1/spreader")
@Validated
@RequiredArgsConstructor
public class SpreaderController {

    private final SpreaderService spreaderService;

    @PostMapping
    public ResponseEntity<Response> spread(
            @RequestHeader("X-USER-ID") @Positive int userId,
            @RequestHeader("X-ROOM-ID") @NotBlank String roomID,
            @RequestBody @Valid Request request) {

        String token = spreaderService.spread(
                    roomID, userId, request.getAmount(), request.getNumber()
        );

        Response response =
                Response.builder()
                        .token(token)
                        .build()
                        .add(linkTo(SpreaderController.class).withSelfRel())
                        .add(linkTo(methodOn(SpreaderController.class).readSpread(token, userId)).withRel("read"))
                        .add(Link.of("/docs/index.html#spreader").withRel("profile"));

        return ResponseEntity.created(
                linkTo(methodOn(SpreaderController.class).readSpread(token, userId)).slash(token).toUri())
                .body(response);
    }


    @GetMapping("/{token}")
    public ResponseEntity<Response> readSpread(
            @PathVariable String token, @RequestHeader("X-USER-ID") @Positive int userId) {


        return ResponseEntity.ok(null);
    }
}
