package com.example.citygame.controller;

import com.example.citygame.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/city")
@RequiredArgsConstructor
public class CityController {
    private final CityService cityService;

    @GetMapping("/begin")
    public ResponseEntity<String> begin() {
        return ResponseEntity.ok(cityService.getRundomCity().getName());
    }

    @GetMapping("/next")
    public ResponseEntity<String> next(@RequestParam("word") String word) {
        if (word.isEmpty() || word == null) {
            return ResponseEntity.ok("Введіть місто");
        }
        return ResponseEntity.ok(cityService.getCityByLetter(word).get().getName());
    }

    @PostMapping("/end")
    public ResponseEntity<String> end() {
        return ResponseEntity.ok(cityService.endGame());
    }
}
