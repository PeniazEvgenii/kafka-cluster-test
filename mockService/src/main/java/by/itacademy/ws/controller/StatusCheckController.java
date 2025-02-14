package by.itacademy.ws.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/response")
public class StatusCheckController {

    @GetMapping("/200")
    public ResponseEntity<String> response200() {
        return ResponseEntity.ok().body("200");
    }

    @GetMapping("/500")
    public ResponseEntity<String> response500() {
        return ResponseEntity.internalServerError().build();
    }
}
