package com.team16.aims.controller;

import com.team16.aims.dto.MediaDTO;
import com.team16.aims.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/media")
@CrossOrigin(origins = "*") // Allow all origins for now
public class MediaController {

    @Autowired
    private MediaService mediaService;

    @GetMapping
    public ResponseEntity<List<MediaDTO>> getAllMedia() {
        return new ResponseEntity<>(mediaService.getAllMedia(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MediaDTO> getMediaById(@PathVariable Integer id) {
        return new ResponseEntity<>(mediaService.getMediaById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<MediaDTO> createMedia(@RequestBody MediaDTO mediaDTO) {
        return new ResponseEntity<>(mediaService.createMedia(mediaDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MediaDTO> updateMedia(@PathVariable Integer id, @RequestBody MediaDTO mediaDTO) {
        return new ResponseEntity<>(mediaService.updateMedia(id, mediaDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedia(@PathVariable Integer id) {
        mediaService.deleteMedia(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
