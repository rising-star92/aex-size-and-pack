package com.walmart.aex.sp.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequestMapping("/size-and-pack/v1")
@Controller
@Api(consumes = MediaType.APPLICATION_JSON_VALUE)
public class SizeAndPackController {
    @GetMapping("/health")
     public ResponseEntity<String> helloWorld(){
         return ResponseEntity.status(HttpStatus.OK).body("Hello");
     }
}