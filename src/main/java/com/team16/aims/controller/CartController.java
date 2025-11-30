package com.team16.aims.controller;

import com.team16.aims.dto.CartDTO;
import com.team16.aims.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<CartDTO> getCart(@RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String sessionId) {
        return new ResponseEntity<>(cartService.getCart(userId, sessionId), HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<CartDTO> getCartByUserId(@PathVariable Integer userId) {
        return new ResponseEntity<>(cartService.getCart(userId, null), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<CartDTO> addToCart(@RequestBody Map<String, Object> request) {
        Integer userId = request.get("userId") != null ? (Integer) request.get("userId") : null;
        String sessionId = (String) request.get("sessionId");
        Integer mediaId = (Integer) request.get("mediaId");
        Integer quantity = (Integer) request.get("quantity");
        return new ResponseEntity<>(cartService.addToCart(userId, sessionId, mediaId, quantity), HttpStatus.OK);
    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<CartDTO> addToCartUser(@PathVariable Integer userId,
            @RequestBody Map<String, Integer> request) {
        Integer mediaId = request.get("mediaId");
        Integer quantity = request.get("quantity");
        return new ResponseEntity<>(cartService.addToCart(userId, null, mediaId, quantity), HttpStatus.OK);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeFromCart(@RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String sessionId,
            @RequestParam Integer mediaId) {
        cartService.removeFromCart(userId, sessionId, mediaId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{userId}/remove/{mediaId}")
    public ResponseEntity<Void> removeFromCartUser(@PathVariable Integer userId, @PathVariable Integer mediaId) {
        cartService.removeFromCart(userId, null, mediaId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/update")
    public ResponseEntity<CartDTO> updateCartItem(@RequestBody Map<String, Object> request) {
        Integer userId = request.get("userId") != null ? (Integer) request.get("userId") : null;
        String sessionId = (String) request.get("sessionId");
        Integer mediaId = (Integer) request.get("mediaId");
        Integer quantity = (Integer) request.get("quantity");
        return new ResponseEntity<>(cartService.updateCartItemQuantity(userId, sessionId, mediaId, quantity),
                HttpStatus.OK);
    }

    @PutMapping("/{userId}/update")
    public ResponseEntity<CartDTO> updateCartItemUser(@PathVariable Integer userId,
            @RequestBody Map<String, Integer> request) {
        Integer mediaId = request.get("mediaId");
        Integer quantity = request.get("quantity");
        return new ResponseEntity<>(cartService.updateCartItemQuantity(userId, null, mediaId, quantity), HttpStatus.OK);
    }

}
