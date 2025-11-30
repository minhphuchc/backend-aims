package com.team16.aims.service;

import com.team16.aims.dto.CartDTO;
import com.team16.aims.dto.OrderDTO;
import com.team16.aims.entity.*;
import com.team16.aims.exception.ResourceNotFoundException;
import com.team16.aims.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartRepo cartRepo;
    @Autowired
    private CartMediaRepo cartMediaRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private MediaRepo mediaRepo;
    @Autowired
    private OrderService orderService;

    public CartDTO getCart(Integer userId, String sessionId) {
        Cart cart = findOrCreateCart(userId, sessionId);
        return convertToDTO(cart);
    }

    @Transactional
    public CartDTO addToCart(Integer userId, String sessionId, Integer mediaId, Integer quantity) {
        Cart cart = findOrCreateCart(userId, sessionId);

        Media media = mediaRepo.findById(mediaId)
                .orElseThrow(() -> new ResourceNotFoundException("Media not found: " + mediaId));

        // Check if item already exists in cart
        Optional<CartMedia> existingItem = cart.getItems().stream()
                .filter(item -> item.getMedia().getMediaId().equals(mediaId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartMedia item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartMediaRepo.save(item);
        } else {
            CartMedia newItem = new CartMedia();
            newItem.setCart(cart);
            newItem.setMedia(media);
            newItem.setQuantity(quantity);
            newItem.setPrice(media.getPrice());
            cart.getItems().add(newItem);
            cartMediaRepo.save(newItem);
        }

        return convertToDTO(cart);
    }

    @Transactional
    public void removeFromCart(Integer userId, String sessionId, Integer mediaId) {
        Cart cart = findCart(userId, sessionId);

        CartMedia itemToRemove = cart.getItems().stream()
                .filter(item -> item.getMedia().getMediaId().equals(mediaId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Media not found in cart: " + mediaId));

        cart.getItems().remove(itemToRemove);
        cartMediaRepo.delete(itemToRemove);
    }

    @Transactional
    public CartDTO updateCartItemQuantity(Integer userId, String sessionId, Integer mediaId, Integer quantity) {
        Cart cart = findCart(userId, sessionId);

        CartMedia item = cart.getItems().stream()
                .filter(i -> i.getMedia().getMediaId().equals(mediaId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Media not found in cart: " + mediaId));

        if (quantity <= 0) {
            cart.getItems().remove(item);
            cartMediaRepo.delete(item);
        } else {
            item.setQuantity(quantity);
            cartMediaRepo.save(item);
        }

        return convertToDTO(cart);
    }

    @Transactional
    public OrderDTO placeOrder(Integer userId, String sessionId, OrderDTO orderInfo) {
        CartDTO cartDTO = getCart(userId, sessionId);
        if (cartDTO.getItems() == null || cartDTO.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Prepare OrderDTO from Cart and OrderInfo
        OrderDTO newOrderDTO = new OrderDTO();
        newOrderDTO.setCustomerName(orderInfo.getCustomerName());
        newOrderDTO.setCustomerPhone(orderInfo.getCustomerPhone());
        newOrderDTO.setCustomerEmail(orderInfo.getCustomerEmail());
        newOrderDTO.setShippingAddress(orderInfo.getShippingAddress());
        newOrderDTO.setShippingProvince(orderInfo.getShippingProvince());
        newOrderDTO.setShippingInstructions(orderInfo.getShippingInstructions());
        newOrderDTO.setShippingFee(orderInfo.getShippingFee());

        List<OrderDTO.OrderItemDTO> orderItems = cartDTO.getItems().stream()
                .map(item -> new OrderDTO.OrderItemDTO(item.getMediaId(), item.getQuantity(), item.getPrice()))
                .collect(Collectors.toList());
        newOrderDTO.setItems(orderItems);

        // Create Order
        OrderDTO createdOrder = orderService.createOrder(newOrderDTO);

        // Clear Cart
        clearCart(userId, sessionId);

        return createdOrder;
    }

    @Transactional
    public void clearCart(Integer userId, String sessionId) {
        Cart cart = findCart(userId, sessionId);
        if (cart != null) {
            cart.getItems().clear();
            cartMediaRepo.deleteAll(cartMediaRepo.findAll().stream()
                    .filter(item -> item.getCart().getCartId().equals(cart.getCartId()))
                    .collect(Collectors.toList()));
        }
    }

    private Cart findOrCreateCart(Integer userId, String sessionId) {
        if (userId != null) {
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
            return cartRepo.findByUser(user).orElseGet(() -> {
                Cart newCart = new Cart();
                newCart.setUser(user);
                return cartRepo.save(newCart);
            });
        } else if (sessionId != null) {
            return cartRepo.findBySessionId(sessionId).orElseGet(() -> {
                Cart newCart = new Cart();
                newCart.setSessionId(sessionId);
                return cartRepo.save(newCart);
            });
        }
        throw new IllegalArgumentException("Either userId or sessionId must be provided");
    }

    private Cart findCart(Integer userId, String sessionId) {
        if (userId != null) {
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
            return cartRepo.findByUser(user)
                    .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));
        } else if (sessionId != null) {
            return cartRepo.findBySessionId(sessionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Cart not found for session: " + sessionId));
        }
        throw new IllegalArgumentException("Either userId or sessionId must be provided");
    }

    private CartDTO convertToDTO(Cart cart) {
        CartDTO dto = new CartDTO();
        dto.setCartId(cart.getCartId());
        if (cart.getUser() != null) {
            dto.setUserId(cart.getUser().getUserId());
        }
        // dto.setSessionId(cart.getSessionId()); // If CartDTO has sessionId field

        List<CartDTO.CartItemDTO> items = new ArrayList<>();
        double total = 0;

        if (cart.getItems() != null) {
            items = cart.getItems().stream().map(item -> {
                CartDTO.CartItemDTO itemDTO = new CartDTO.CartItemDTO();
                itemDTO.setCartMediaId(item.getCartMediaId());
                itemDTO.setMediaId(item.getMedia().getMediaId());
                itemDTO.setMediaTitle(item.getMedia().getTitle());
                itemDTO.setMediaType(item.getMedia().getMediaType());
                itemDTO.setQuantity(item.getQuantity());
                itemDTO.setPrice(item.getPrice());
                itemDTO.setImageUrl(item.getMedia().getImageUrl()); // Added imageUrl
                return itemDTO;
            }).collect(Collectors.toList());

            total = cart.getItems().stream()
                    .mapToDouble(item -> item.getPrice() * item.getQuantity())
                    .sum();
        }

        dto.setItems(items);
        dto.setTotalPrice(total);
        return dto;
    }
}
