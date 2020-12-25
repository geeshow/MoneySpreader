package com.geeshow.kakaopay.MoneySpreader.controller;

import com.geeshow.kakaopay.MoneySpreader.domain.Spreader;
import com.geeshow.kakaopay.MoneySpreader.domain.SpreaderTicket;
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

import static com.geeshow.kakaopay.MoneySpreader.dto.SpreaderDto.ResponseSpreadDto;
import static com.geeshow.kakaopay.MoneySpreader.dto.SpreaderDto.ResponseReceiveDto;
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
    public ResponseEntity<ResponseSpreadDto> spread(
            @RequestHeader("X-USER-ID") @Positive int userId,
            @RequestHeader("X-ROOM-ID") @NotBlank String roomID,
            @RequestBody @Valid SpreaderDto.RequestSpreadDto requestSpreadDto) {

        String token = spreaderService.spread(
                roomID, userId, requestSpreadDto.getAmount(), requestSpreadDto.getNumber()
        ).getToken();

        ResponseSpreadDto responseSpreadDto =
                ResponseSpreadDto.builder()
                        .token(token)
                        .build()
                        .add(linkTo(SpreaderController.class).withSelfRel())
                        .add(linkTo(methodOn(SpreaderController.class).read(token, userId)).withRel("read"))
                        .add(Link.of("/docs/index.html#spreader").withRel("profile"));

        return ResponseEntity.created(
                linkTo(methodOn(SpreaderController.class).read(token, userId)).slash(token).toUri())
                .body(responseSpreadDto);
    }

    @GetMapping("/{token}")
    public ResponseEntity<SpreaderDto.ResponseReadDto> read(
            @PathVariable String token, @RequestHeader("X-USER-ID") @Positive int userId) {

        Spreader spreader = spreaderService.read(token, userId);
        SpreaderDto.ResponseReadDto responseReadDto = spreaderMapper.toDto(spreader);

        responseReadDto.add(linkTo(methodOn(SpreaderController.class).read(token, userId)).withSelfRel())
                .add(linkTo(SpreaderController.class).withRel("spreader"))
//                .add(linkTo(methodOn(SpreaderController.class).receive(token, userId, "roomId")).withRel("receiving"))
                .add(Link.of("/docs/index.html#read").withRel("profile"));
        return ResponseEntity.ok(responseReadDto);
    }

    @PutMapping("/{token}")
    public ResponseEntity<ResponseReceiveDto> receive(
            @PathVariable String token,
            @RequestHeader("X-USER-ID") @Positive int userId,
            @RequestHeader("X-ROOM-ID") @NotBlank String roomID) {

        SpreaderTicket receivedTicket = spreaderService.receive(roomID, token, userId);

        ResponseReceiveDto responseReceiveDto =
                ResponseReceiveDto.builder()
                        .amount(receivedTicket.getAmount())
                        .build()
                        .add(
                                linkTo(methodOn(SpreaderController.class).receive(token, userId, roomID))
                                        .withSelfRel())
                        .add(linkTo(SpreaderController.class).withRel("sprinkling"))
                        .add(linkTo(methodOn(SpreaderController.class).read(token, userId)).withRel("read"))
                        .add(Link.of("/docs/index.html#receiving").withRel("profile"));

        return ResponseEntity.ok(responseReceiveDto);
    }
}
