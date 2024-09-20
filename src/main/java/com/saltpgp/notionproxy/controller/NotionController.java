package com.saltpgp.notionproxy.controller;

import com.saltpgp.notionproxy.dtos.outgoing.*;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.models.Consultant;
import com.saltpgp.notionproxy.service.NotionService;
import org.ehcache.core.EhcacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("salt")
@CrossOrigin
public class NotionController {

    private final NotionService notionService;

    public NotionController(NotionService notionService) {
        this.notionService = notionService;
    }


    @Autowired
    CacheManager genericCacheManager;


    @GetMapping("test")
    public void bla() {
        Cache cache = genericCacheManager.getCache("developerScoreCard");
//        org.ehcache.Cache<String, Object> ehCache = (org.ehcache.Cache<String, Object>) cache.getNativeCache();
        ConcurrentHashMap<Object, Object> ehCache = (ConcurrentHashMap<Object, Object>) cache.getNativeCache();
        ehCache.entrySet().forEach(entry -> {
            System.out.println("entry.getKey() = " + entry.getKey().toString());
            System.out.println("entry.getValue() = " + ((Developer) entry.getValue()).getName());

            Object cachedObj = cache.get(entry.getKey());
            System.out.println("cachedObj = " + cachedObj);
        });
//        ehCache.forEach(cch -> {
////            System.out.println("cch.getKey() = " + cch.getKey());
////            System.out.println("cch.getValue() = " + cch.getValue());
//        });
//        boolean did = cache.invalidate();
//        System.out.println("did the cache have values? " + did);
    }

    @GetMapping("")
    public ResponseEntity<List<SaltiesDto>> getAllSalties() {
        try {
            return ResponseEntity.ok(SaltiesDto.fromModel(notionService.getSalties()));
        } catch (NotionException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("consultant/{id}")
    public ResponseEntity<ConsultantWithResponsibleDto> getConsultant(
            @PathVariable UUID id,
            @RequestParam(required = false, defaultValue = "false") boolean includeNull) {
        try {
            Consultant consultant = notionService.getResponsiblePersonNameByUserId(id, includeNull);
            if (consultant == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(ConsultantWithResponsibleDto.fromModel(consultant));
        } catch (NotionException e) {
            return ResponseEntity.internalServerError().build();
        } catch (HttpClientErrorException.NotFound e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("consultant")
    public ResponseEntity<List<ConsultantWithResponsibleDto>> getConsultants(
            @RequestParam(required = false, defaultValue = "false") boolean includeEmpty,
            @RequestParam(required = false, defaultValue = "false") boolean includeNull) {
        try {
            return ResponseEntity.ok(notionService.getConsultants(includeEmpty, includeNull).stream().map(ConsultantWithResponsibleDto::fromModel).toList());
        } catch (NotionException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("responsible")
    public <T> ResponseEntity<List<T>> getResponsiblePeople(
            @RequestParam(required = false, defaultValue = "false") boolean includeNull,
            @RequestParam(required = false, defaultValue = "false") boolean includeConsultants
    ) {
        try {
            if(includeConsultants){
                List<ResponsibleWithConsultantsDto> dtos = ResponsibleWithConsultantsDto.fromModelSet(notionService.getAllResponsiblePeople(includeNull, includeConsultants));
                return ResponseEntity.ok((List<T>) dtos);
            }
            else{
                List<BasicResponsiblePersonDto> dtos = BasicResponsiblePersonDto.fromModelSet(notionService.getAllResponsiblePeople(includeNull, includeConsultants));
                return ResponseEntity.ok((List<T>) dtos);
            }
        } catch (NotionException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("responsible/{id}")
    public <T> ResponseEntity<T> getResponsiblePeopleById(
            @PathVariable UUID id,
            @RequestParam(required = false, defaultValue = "false") boolean includeNull,
            @RequestParam(required = false, defaultValue = "false") boolean includeConsultants
    ) {
        try {
            if(includeConsultants){
                ResponsibleWithConsultantsDto dtos = ResponsibleWithConsultantsDto.fromModel(notionService.getResponsiblePersonById(id, includeNull, true));
                return ResponseEntity.ok((T) dtos);
            }
            else{
                BasicResponsiblePersonDto dtos = BasicResponsiblePersonDto.fromModel(notionService.getResponsiblePersonById(id,includeNull, false));
                return ResponseEntity.ok((T) dtos);
            }
        } catch (NotionException e) {
            return ResponseEntity.internalServerError().build();
        }
    }



    @GetMapping("developer/{id}/score")
    public ResponseEntity <DeveloperDto> getScoreCard(@PathVariable UUID id){
        try{
            return ResponseEntity.ok(DeveloperDto.fromModel(notionService.getDeveloperScoreCard(id)));
        } catch (NotionException e) {
            return ResponseEntity.internalServerError().build();
        }

    }

}
