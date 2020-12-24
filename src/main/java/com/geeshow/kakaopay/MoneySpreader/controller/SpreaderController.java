package com.geeshow.kakaopay.MoneySpreader.controller;

import com.geeshow.kakaopay.MoneySpreader.domain.Spreader;
import com.geeshow.kakaopay.MoneySpreader.dto.SpreaderDto;
import com.geeshow.kakaopay.MoneySpreader.mapper.SpreaderMapper;
import com.geeshow.kakaopay.MoneySpreader.service.SpreaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

import static com.geeshow.kakaopay.MoneySpreader.dto.SpreaderDto.ResponseSpread;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/v1/spreader")
@Validated
@RequiredArgsConstructor
public class SpreaderController {

    private final SpreaderService spreaderService;
    private final SpreaderMapper spreaderMapper;

    @PostMapping
    public ResponseEntity<ResponseSpread> spread(
            @RequestHeader("X-USER-ID") @Positive int userId,
            @RequestHeader("X-ROOM-ID") @NotBlank String roomID,
            @RequestBody @Valid SpreaderDto.RequestSpread requestSpread) {

        String token = spreaderService.spread(
                roomID, userId, requestSpread.getAmount(), requestSpread.getNumber()
        ).getToken();

        ResponseSpread responseSpread =
                ResponseSpread.builder()
                        .token(token)
                        .build()
                        .add(linkTo(SpreaderController.class).withSelfRel())
                        .add(linkTo(methodOn(SpreaderController.class).read(token, userId)).withRel("read"))
                        .add(Link.of("/docs/index.html#spreader").withRel("profile"));

        return ResponseEntity.created(
                linkTo(methodOn(SpreaderController.class).read(token, userId)).slash(token).toUri())
                .body(responseSpread);
    }

    @GetMapping("/{token}")
    public ResponseEntity<SpreaderDto.ResponseRead> read(
            @PathVariable String token, @RequestHeader("X-USER-ID") @Positive int userId) {

        Spreader spreader = spreaderService.read(token, userId);
        SpreaderDto.ResponseRead responseRead = spreaderMapper.toDto(spreader);

        responseRead.add(linkTo(methodOn(SpreaderController.class).read(token, userId)).withSelfRel())
                .add(linkTo(SpreaderController.class).withRel("spreader"))
//                .add(linkTo(methodOn(SpreaderController.class).receive(token, userId, "roomId")).withRel("receiving"))
                .add(Link.of("/docs/index.html#read").withRel("profile"));
        return ResponseEntity.ok(responseRead);
    }

}
