package com.saltpgp.notionproxy.modules.idcard.controller;

import com.saltpgp.notionproxy.modules.idcard.service.IdCardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/id-cards")
@CrossOrigin
@Slf4j
public class IdCardController {
    private final IdCardService idCardService;

    public IdCardController(IdCardService idCardService) {
        this.idCardService = idCardService;
    }



}
