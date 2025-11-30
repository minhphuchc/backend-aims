# AIMS Backend API Documentation

Base URL: `http://localhost:8081`

## 1. Media Management

### Get All Media
Retrieves a list of all available media items.

**Endpoint:** `GET /api/media`

**Curl:**
```bash
curl -X GET http://localhost:8081/api/media
```

### Get Media by ID
Retrieves details of a specific media item.

**Endpoint:** `GET /api/media/{id}`

**Curl:**
```bash
curl -X GET http://localhost:8081/api/media/1
```

### Create Media
Creates a new media item (Book, CD, DVD, or Newspaper).

**Endpoint:** `POST /api/media`

**Request Body (Book Example):**
```json
{
  "title": "Effective Java",
  "category": "Education",
  "price": 500000,
  "quantity": 100,
  "weight": 0.5,
  "imageUrl": "http://example.com/image.jpg",
  "mediaType": "Book",
  "authors": "Joshua Bloch",
  "coverType": "Hardcover",
  "publisher": "Addison-Wesley",
  "publishDate": "2017-12-27T00:00:00",
  "language": "English",
  "numOfPages": 416
}
```

**Curl:**
```bash
curl -X POST http://localhost:8081/api/media \
-H "Content-Type: application/json" \
-d '{
  "title": "Effective Java",
  "category": "Education",
  "price": 500000,
  "quantity": 100,
  "weight": 0.5,
  "imageUrl": "http://example.com/image.jpg",
  "mediaType": "Book",
  "authors": "Joshua Bloch",
  "coverType": "Hardcover",
  "publisher": "Addison-Wesley",
  "publishDate": "2017-12-27T00:00:00",
  "language": "English",
  "numOfPages": 416
}'
```

---

## 2. Cart Management

### Get Cart (User or Guest)
Retrieves the shopping cart for a specific user or session.

**Endpoint:** `GET /api/cart`

**Query Parameters:**
- `userId` (optional): ID of the logged-in user.
- `sessionId` (optional): Session ID for guest users.

**Curl (User):**
```bash
curl -X GET "http://localhost:8081/api/cart?userId=1"
```

**Curl (Guest):**
```bash
curl -X GET "http://localhost:8081/api/cart?sessionId=guest-123"
```

### Add to Cart (User or Guest)
Adds an item to the cart.

**Endpoint:** `POST /api/cart/add`

**Request Body:**
```json
{
  "userId": 1,
  "sessionId": "guest-123",
  "mediaId": 1,
  "quantity": 2
}
```
*Note: Provide either `userId` or `sessionId`.*

**Curl:**
```bash
curl -X POST http://localhost:8081/api/cart/add \
-H "Content-Type: application/json" \
-d '{"sessionId": "guest-123", "mediaId": 1, "quantity": 2}'
```

### Update Cart Item
Updates the quantity of an item in the cart.

**Endpoint:** `PUT /api/cart/update`

**Request Body:**
```json
{
  "userId": 1,
  "sessionId": "guest-123",
  "mediaId": 1,
  "quantity": 5
}
```

**Curl:**
```bash
curl -X PUT http://localhost:8081/api/cart/update \
-H "Content-Type: application/json" \
-d '{"sessionId": "guest-123", "mediaId": 1, "quantity": 5}'
```

### Remove from Cart
Removes an item from the cart.

**Endpoint:** `DELETE /api/cart/remove`

**Query Parameters:**
- `userId` (optional)
- `sessionId` (optional)
- `mediaId` (required)

**Curl:**
```bash
curl -X DELETE "http://localhost:8081/api/cart/remove?sessionId=guest-123&mediaId=1"
```

---

## 3. Order Placement

### Place Order (Logged-in User)
Creates an order from the user's current cart contents.

**Endpoint:** `POST /api/place-order/{userId}`

**Request Body:**
```json
{
  "customerName": "Nguyen Van A",
  "customerPhone": "0987654321",
  "customerEmail": "email@example.com",
  "shippingAddress": "123 Le Thanh Ton",
  "shippingProvince": "Hanoi",
  "shippingInstructions": "Call before delivery"
}
```

### Place Order (Guest Cart)
Creates an order from a guest cart identified by session ID.

**Endpoint:** `POST /api/place-order/cart?sessionId={sessionId}`

**Request Body:** Same as above.

**Curl:**
```bash
curl -X POST "http://localhost:8081/api/place-order/cart?sessionId=guest-123" \
-H "Content-Type: application/json" \
-d '{
  "customerName": "Guest User",
  "customerPhone": "0912345678",
  "shippingAddress": "456 Kim Ma",
  "shippingProvince": "Hanoi"
}'
```

### Place Order (Direct Items)
Creates an order directly with a list of items (bypassing cart).

**Endpoint:** `POST /api/place-order`

**Request Body:**
```json
{
  "customerName": "Guest User",
  "customerPhone": "0912345678",
  "customerEmail": "guest@example.com",
  "shippingAddress": "456 Kim Ma",
  "shippingProvince": "Hanoi",
  "items": [
    {
      "mediaId": 1,
      "quantity": 1
    }
  ]
}
```

**Curl:**
```bash
curl -X POST http://localhost:8081/api/place-order \
-H "Content-Type: application/json" \
-d '{
  "customerName": "Guest User",
  "customerPhone": "0912345678",
  "customerEmail": "guest@example.com",
  "shippingAddress": "456 Kim Ma",
  "shippingProvince": "Hanoi",
  "items": [
    {
      "mediaId": 1,
      "quantity": 1
    }
  ]
}'
```

### Validate Delivery Info
Validates the delivery information before placing an order.

**Endpoint:** `POST /api/place-order/validate-delivery-info`

**Curl:**
```bash
curl -X POST http://localhost:8081/api/place-order/validate-delivery-info \
-H "Content-Type: application/json" \
-d '{
  "customerName": "Test",
  "customerPhone": "0987654321",
  "shippingAddress": "Address",
  "shippingProvince": "City"
}'
```

### Calculate Shipping Fee
Calculates the shipping fee based on the order items and location.

**Endpoint:** `POST /api/place-order/calculate-shipping-fee`

**Curl:**
```bash
curl -X POST http://localhost:8081/api/place-order/calculate-shipping-fee \
-H "Content-Type: application/json" \
-d '{
  "shippingProvince": "Hanoi",
  "items": [{"mediaId": 1, "quantity": 2}]
}'
```

---

## 4. Payment (VietQR)

### Get Payment URL
Generates a VietQR URL for an order, which can be rendered as a QR code.

**Endpoint:** `GET /api/payment/url/{orderId}`

**Curl:**
```bash
curl -X GET http://localhost:8081/api/payment/url/1
```

### Process Payment (Simulation)
Simulates a payment transaction callback.

**Endpoint:** `POST /api/payment`

**Request Body:**
```json
{
  "orderId": 1,
  "amount": 500000,
  "paymentMethod": "QR_CODE",
  "transactionContent": "Payment for Order 1"
}
```

**Curl:**
```bash
curl -X POST http://localhost:8081/api/payment \
-H "Content-Type: application/json" \
-d '{
  "orderId": 1,
  "amount": 500000,
  "paymentMethod": "QR_CODE",
  "transactionContent": "Payment for Order 1"
}'
```
