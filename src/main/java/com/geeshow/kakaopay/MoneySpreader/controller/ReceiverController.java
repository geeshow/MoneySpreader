package com.geeshow.kakaopay.MoneySpreader.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/receiver")
@Validated
@RequiredArgsConstructor
public class ReceiverController {
}
