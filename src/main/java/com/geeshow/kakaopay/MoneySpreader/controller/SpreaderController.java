package com.geeshow.kakaopay.MoneySpreader.controller;

import com.geeshow.kakaopay.MoneySpreader.constant.SpreaderConstant;
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
            @RequestHeader(SpreaderConstant.HTTP_HEADER_ROOM_ID) @NotBlank String roomID,
            @RequestHeader(SpreaderConstant.HTTP_HEADER_USER_ID) @Positive int userId,
            @RequestBody @Valid SpreaderDto.RequestSpreadDto requestSpreadDto) {

        String token = spreaderService.spread(
                roomID, userId, requestSpreadDto.getAmount(), requestSpreadDto.getNumber()
        ).getToken();

        ResponseSpreadDto responseSpreadDto =
                ResponseSpreadDto.builder()
                        .token(token)
                        .build()
                        .add(linkTo(SpreaderController.class).withSelfRel())
                        .add(linkTo(methodOn(SpreaderController.class).read(roomID, userId, token)).withRel("read"))
                        .add(linkTo(methodOn(SpreaderController.class).receive(token, userId, "roomId")).withRel("receipt"))
                        .add(Link.of("/docs/index.html#spreader").withRel("profile"));

        return ResponseEntity.created(
                linkTo(methodOn(SpreaderController.class).read(roomID, userId, token)).slash(token).toUri())
                .body(responseSpreadDto);
    }

    @GetMapping("/{token}")
    public ResponseEntity<SpreaderDto.ResponseReadDto> read(
            @RequestHeader(SpreaderConstant.HTTP_HEADER_ROOM_ID) @NotBlank String roomID,
            @RequestHeader(SpreaderConstant.HTTP_HEADER_USER_ID) @Positive int userId,
            @PathVariable String token) {

        Spreader spreader = spreaderService.read(roomID, userId, token);
        SpreaderDto.ResponseReadDto responseReadDto = spreaderMapper.toDto(spreader);

        responseReadDto.add(linkTo(methodOn(SpreaderController.class).read(roomID, userId, token)).withSelfRel())
                .add(linkTo(SpreaderController.class).withRel("spreader"))
                .add(linkTo(methodOn(SpreaderController.class).receive("roomId", userId, token)).withRel("receipt"))
                .add(Link.of("/docs/index.html#read").withRel("profile"));
        return ResponseEntity.ok(responseReadDto);
    }

    @PutMapping("/receipt/{token}")
    public ResponseEntity<ResponseReceiveDto> receive(
            @RequestHeader(SpreaderConstant.HTTP_HEADER_ROOM_ID) @NotBlank String roomID,
            @RequestHeader(SpreaderConstant.HTTP_HEADER_USER_ID) @Positive int userId,
            @PathVariable String token
            ) {

        SpreaderTicket receivedTicket = spreaderService.receive(roomID, userId, token);

        ResponseReceiveDto responseReceiveDto =
                ResponseReceiveDto.builder()
                        .amount(receivedTicket.getAmount())
                        .build()
                        .add(
                                linkTo(methodOn(SpreaderController.class).receive(token, userId, roomID))
                                        .withSelfRel())
                        .add(linkTo(SpreaderController.class).withRel("spreader"))
                        .add(Link.of("/docs/index.html#receipt").withRel("profile"));

        return ResponseEntity.ok(responseReceiveDto);
    }


}
