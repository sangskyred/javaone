# API Specification v1

Base URL mặc định: `http://localhost:8080`

## Quy ước chung

### Xác thực

Các endpoint khách hàng sử dụng Basic Authentication/Principal hiện tại được cấu hình cho tài khoản đã đăng nhập. Truyền header:

```http
Authorization: Basic <base64(username:password)>
Content-Type: application/json
```

### Envelope response

```json
{
  "code": 200,
  "message": "Success",
  "data": {},
  "timestamp": "2026-11-01T12:00:00+07:00"
}
```

`data` có thể là object, array hoặc `null`.

### Lỗi

```json
{
  "code": 400,
  "message": "Coupon code is invalid",
  "data": null,
  "timestamp": "2026-11-01T12:00:00+07:00"
}
```

### Phân trang

```json
{
  "content": [],
  "totalElements": 0,
  "totalPages": 0,
  "pageNumber": 0,
  "pageSize": 20
}
```

## 1. Sản phẩm và biến thể

### GET /api/v1/products

- Quyền: Public.
- Query: không có filter bắt buộc.
- Response legacy: `ApiResponse<List<ProductResponse>>`.

```json
{
  "code": 200,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "name": "Laptop",
      "description": "Laptop văn phòng",
      "price": 15000000.00,
      "stock": 20
    }
  ],
  "timestamp": "2026-11-01T12:00:00+07:00"
}
```

### GET /api/v1/products/search

- Quyền: Public.
- Query Parameters:
  - `keyword`: string, tùy chọn.
  - `categoryId`: integer 64-bit, tùy chọn.
  - `brandId`: integer 64-bit, tùy chọn.
  - `page`: integer, mặc định `0`, tối thiểu `0`.
  - `size`: integer, mặc định `20`, từ `1` đến `100`.
- Response: `ApiResponse<PageResponse<ProductResponse>>`.

Ví dụ:

```http
GET /api/v1/products/search?keyword=laptop&categoryId=1&brandId=2&page=0&size=20
```

### GET /api/v1/products/{id}

- Quyền: Public.
- Path: `id: Long`.
- Response: `ApiResponse<ProductDetailResponse>`.

```json
{
  "id": 1,
  "name": "Laptop",
  "description": "Laptop văn phòng",
  "variants": [
    {
      "id": 10,
      "sku": "LAPTOP-16GB",
      "name": "16GB RAM",
      "attributes": { "ram": "16GB", "color": "black" },
      "price": 15000000.00,
      "stock": 20,
      "isActive": true,
      "media": []
    }
  ]
}
```

## 2. Quản trị sản phẩm và kho

Các endpoint dưới đây yêu cầu role `ADMIN` hoặc `STAFF`.

### POST /api/v1/admin/products

Request `ProductRequest`:

```json
{
  "categoryId": 1,
  "brandId": 2,
  "name": "Laptop",
  "description": "Laptop văn phòng",
  "media": [
    { "mediaType": "image", "url": "https://cdn.example.com/products/laptop-1.jpg", "sortOrder": 0, "isMain": true },
    { "mediaType": "video", "url": "https://cdn.example.com/products/laptop.mp4", "thumbnailUrl": "https://cdn.example.com/products/laptop-cover.jpg", "sortOrder": 1, "isMain": false }
  ]
}
```

- `categoryId`: Long | null.
- `brandId`: Long | null.
- `name`: string, bắt buộc, không rỗng.
- `description`: string | null.

Giá và stock không thuộc Product tổng thể. Chúng được tạo/cập nhật qua endpoint variant `/api/v1/admin/products/{id}/variants` và `/api/v1/admin/products/variants/{variantId}`.

Response: `ApiResponse<ProductResponse>`.

### PUT /api/v1/admin/products/{id}

Request: giống `ProductRequest`.
Response: `ApiResponse<ProductResponse>`.

### DELETE /api/v1/admin/products/{id}

- Soft delete bằng `deleted_at`.
- Response: `ApiResponse<Void>`.

### POST /api/v1/admin/products/{id}/variants

Request `VariantRequest`:

```json
{
  "sku": "LAPTOP-16GB",
  "name": "16GB RAM",
  "attributes": { "ram": "16GB", "color": "black" },
  "price": 15000000.00,
  "stock": 20,
  "isActive": true
}
```

- `sku`: string, bắt buộc.
- `name`: string | null.
- `attributes`: object JSON | null.
- `price`: BigDecimal, `>= 0`.
- `stock`: integer, `>= 0`.
- `isActive`: boolean.

Response: `ApiResponse<ProductResponse>`.

### PUT /api/v1/admin/products/variants/{variantId}

Request: giống `VariantRequest`.
Response: `ApiResponse<ProductResponse>`.

### DELETE /api/v1/admin/products/variants/{variantId}

Soft delete biến thể.
Response: `ApiResponse<Void>`.

### GET /api/v1/admin/stock-movements

- Query:
  - `variantId`: Long | null.
  - `reason`: string | null: `purchase`, `sale`, `return`, `adjustment`, `reservation`, `release`, `damage`.
  - `page`: integer, mặc định `0`.
  - `size`: integer, mặc định `20`, tối đa `100`.
- Response: `ApiResponse<PageResponse<StockMovement>>`.

```json
{
  "id": 1,
  "variantId": 10,
  "changeAmount": -2,
  "reason": "sale",
  "referenceId": 55,
  "note": null,
  "createdAt": "2026-11-01T12:00:00+07:00"
}
```

## 3. Danh mục và thương hiệu

Hiện backend chưa có controller/router v1 cho Category và Brand. Không gọi các URL giả định cho hai cụm này. Sau khi controller được triển khai, endpoint dự kiến nên nằm dưới:

- `GET /api/v1/categories`
- `GET /api/v1/categories/{id}`
- `GET /api/v1/brands`
- `GET /api/v1/brands/{id}`

## 4. Địa chỉ nhận hàng

Endpoint dưới đây yêu cầu khách hàng đã đăng nhập. Danh sách chỉ trả địa chỉ thuộc tài khoản hiện tại.

### GET /api/v1/addresses

Response: `ApiResponse<List<AddressResponse>>`.

```json
[
  {
    "id": 12,
    "recipientName": "Nguyễn Văn A",
    "phone": "0901234567",
    "line1": "123 Nguyen Trai",
    "line2": "Tang 2",
    "city": "Ha Noi",
    "postalCode": "100000",
    "countryCode": "VN",
    "isDefault": true,
    "createdAt": "2026-11-01T12:00:00+07:00"
  }
]
```

### POST /api/v1/addresses

Request `CreateAddressRequest`:

```json
{
  "recipientName": "Nguyễn Văn A",
  "phone": "0901234567",
  "line1": "123 Nguyen Trai",
  "line2": "Tang 2",
  "city": "Ha Noi",
  "postalCode": "100000",
  "countryCode": "VN",
  "isDefault": true
}
```

- `recipientName`: string, bắt buộc, không rỗng.
- `phone`: string, bắt buộc, không rỗng.
- `line1`: string, bắt buộc, không rỗng.
- `line2`: string | null.
- `city`: string, bắt buộc, không rỗng.
- `postalCode`: string | null.
- `countryCode`: string, 2 ký tự, mặc định `VN`.
- `isDefault`: boolean.

Nếu `isDefault = true`, backend đặt tất cả địa chỉ cũ của user thành `false` trong cùng transaction trước khi tạo địa chỉ mới.

## 5. Giỏ hàng

Cần token đăng nhập khách hàng. Không truyền `userId` trong body.

### GET /api/v1/cart

Response: `ApiResponse<CartResponse>`.

```json
{
  "items": [],
  "total": 0.00
}
```

### POST /api/v1/cart/add

Request `AddCartItemRequest`:

```json
{
  "variantId": 10,
  "quantity": 2
}
```

- `variantId`: Long, bắt buộc.
- `quantity`: integer, `> 0`.

Response: `ApiResponse<CartResponse>`.

### PUT /api/v1/cart/items/{cartItemId}

Request `UpdateCartItemRequest`:

```json
{
  "quantity": 3
}
```

- `quantity`: integer, `> 0`.
- `cartItemId`: Long trong path.

Response: `ApiResponse<CartResponse>`.

### DELETE /api/v1/cart/items/{cartItemId}

Response: `ApiResponse<Void>`.

## 6. Đơn hàng và vận chuyển

### POST /api/v1/orders/checkout-preview

Request tùy chọn `CheckoutRequest`:

```json
{
  "shippingAddressId": null,
  "couponCode": "SALE10",
  "currency": "VND"
}
```

- `shippingAddressId`: Long | null.
- `couponCode`: string | null, tối đa 50 ký tự.
- `currency`: string | null.

Response:

```json
{
  "subtotal": 100000.00,
  "discountAmount": 10000.00,
  "shippingFee": 0.00,
  "total": 90000.00
}
```

### POST /api/v1/orders

Request: giống checkout-preview hoặc body rỗng.
Response: `ApiResponse<OrderResponse>`.

```json
{
  "id": 55,
  "total": 90000.00,
  "status": "pending",
  "createdAt": "2026-11-01T12:00:00+07:00",
  "items": [
    {
      "productId": 1,
      "productName": "Laptop",
      "unitPrice": 45000.00,
      "quantity": 2
    }
  ]
}
```

### GET /api/v1/orders/my-orders

- Query:
  - `status`: `pending | paid | shipped | delivered | cancelled`, tùy chọn.
  - `page`: integer, mặc định `0`.
  - `size`: integer, mặc định `20`.
- Response: `ApiResponse<PageResponse<OrderResponse>>`.

### POST /api/v1/orders/{id}/cancel

- Chỉ hủy đơn `pending`.
- Hoàn lại stock variant và ghi movement reason `release`.
- Response: `ApiResponse<OrderResponse>`.

### PUT /api/v1/admin/orders/{id}/status

Role: `ADMIN` hoặc `STAFF`.

```json
{
  "status": "paid"
}
```

Giá trị hợp lệ: `paid`, `shipped`, `delivered`, `cancelled`.
Response: `ApiResponse<OrderResponse>`.

### GET /api/v1/orders/{orderId}/shipment

Quyền: Khách hàng, chỉ xem vận đơn của đơn thuộc tài khoản hiện tại.

Response: `ApiResponse<ShipmentResponse>`.

```json
{
  "id": 9,
  "orderId": 55,
  "addressId": 12,
  "carrier": "Giao Hang Nhanh",
  "trackingNumber": "GHN123456789",
  "status": "shipping",
  "shippedAt": "2026-11-02T08:30:00+07:00",
  "deliveredAt": null,
  "createdAt": "2026-11-01T12:00:00+07:00"
}
```

`status` hợp lệ: `preparing`, `shipping`, `delivered`, `failed`, `returned`.

### PUT /api/v1/admin/shipments/{id}/status

Quyền: `ADMIN` hoặc `STAFF`.

Request:

```json
{
  "status": "shipping",
  "carrier": "Giao Hang Nhanh",
  "trackingNumber": "GHN123456789"
}
```

- `status`: bắt buộc, một trong whitelist `preparing`, `shipping`, `delivered`, `failed`, `returned`.
- `carrier`: string | null.
- `trackingNumber`: string | null.

Response: `ApiResponse<ShipmentResponse>`.

## 7. Coupon

Các endpoint coupon yêu cầu `ADMIN` hoặc `STAFF`.

### GET /api/v1/admin/coupons

Response: `ApiResponse<List<Coupon>>`.

### GET /api/v1/admin/coupons/{id}

Response: `ApiResponse<Coupon>`.

### POST /api/v1/admin/coupons

Request `CouponRequest`:

```json
{
  "code": "SALE10",
  "discountType": "percent",
  "discountValue": 10,
  "maxUses": 100,
  "minOrderAmount": 500000,
  "startsAt": "2026-11-01T00:00:00+07:00",
  "expiresAt": "2026-12-01T00:00:00+07:00",
  "isActive": true
}
```

- `discountValue`: percent: `0-100`; fixed: số tiền.
- `usedCount` được hệ thống quản lý và không nhận từ request.

### PUT /api/v1/admin/coupons/{id}

Request: giống CouponRequest.

### PUT /api/v1/admin/coupons/{id}/active

```json
{
  "isActive": false
}
```

Response: `ApiResponse<Coupon>`.

### DELETE /api/v1/admin/coupons/{id}

Response: `ApiResponse<Void>`.

Coupon được áp trực tiếp trong checkout/preview bằng trường `couponCode`. Khi checkout thành công, backend tăng `used_count` và ghi `coupon_redemptions` trong cùng transaction.

## 8. Thanh toán

### POST /api/v1/payments/initiate

Quyền: khách hàng đã đăng nhập, phải sở hữu đơn hàng.

```json
{
  "orderId": 55,
  "method": "vnpay"
}
```

- `orderId`: Long, bắt buộc.
- `method`: `cod`, `bank_transfer`, `momo`, `vnpay`, `paypal`, `stripe`.

Response: `ApiResponse<Payment>`.

```json
{
  "id": 1,
  "orderId": 55,
  "amount": 90000.00,
  "currency": "VND",
  "method": "vnpay",
  "status": "pending",
  "gatewayTransactionId": "GW-VNPAY-55-1234567890",
  "paidAt": null,
  "refundedAt": null,
  "createdAt": "2026-11-01T12:00:00+07:00"
}
```

### POST /api/v1/payments/webhook/{gateway}

Thông thường webhook public, không yêu cầu Basic Authentication và không bọc kết quả trong `ApiResponse<T>`. Gateway hợp lệ: `cod`, `bank_transfer`, `momo`, `vnpay`, `paypal`, `stripe`.

Request chung:

```json
{
  "transactionId": "GW-VNPAY-55-1234567890",
  "status": "succeeded",
  "signature": "hex-hmac-sha256"
}
```

- `transactionId`: string, bắt buộc.
- `status`: `succeeded | failed`.
- `signature`: chuỗi hex HMAC-SHA256.

Khi `succeeded`, backend cập nhật payment `succeeded`, `paid_at`, và đơn hàng từ `pending` sang `paid`. Khi `failed`, backend cập nhật payment thành `failed`.

Webhook response tùy gateway:

#### VNPay

```json
{
  "RspCode": "00",
  "Message": "Confirm success"
}
```

Mã lỗi VNPay hợp lệ tùy xử lý gateway, ví dụ chữ ký không hợp lệ hoặc transaction không tồn tại:

```json
{
  "RspCode": "97",
  "Message": "Invalid signature"
}
```

#### Momo

```json
{
  "partnerCode": "MOMO",
  "orderId": "55",
  "requestId": "GW-MOMO-55-1234567890",
  "amount": 90000,
  "resultCode": 0,
  "message": "Successful",
  "responseTime": 1730448000000,
  "signature": "gateway-generated-signature"
}
```

Lỗi webhook Momo:

```json
{
  "resultCode": 97,
  "message": "Invalid signature"
}
```

#### Stripe

Stripe thường không gửi JSON xử lý nghiệp vụ qua response; nhận phản hồi HTTP:

```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "received": true
}
```

Sai chữ ký hoặc xử lý webhook Stripe thất bại phải trả HTTP `400` thay vì `ApiResponse<T>`:

```json
{
  "received": false,
  "error": "Invalid webhook signature"
}
```

#### COD và bank transfer

Cổng callback nội bộ có thể dùng response phẳng:

```json
{
  "code": "00",
  "message": "Success",
  "accepted": true
}
```

## Postman

1. Import collection thủ công bằng Base URL `{{baseUrl}}` = `http://localhost:8080`.
2. Tạo environment variables:
   - `baseUrl`: `http://localhost:8080`
   - `username`: email tài khoản.
   - `password`: mật khẩu tài khoản.
3. Basic Auth trong tab Authorization, chọn **Basic Auth** và điền username/password.
4. Với endpoint Admin/Staff, đăng nhập bằng tài khoản có role tương ứng trong bảng `user_roles`.
5. Với webhook, tính signature bằng HMAC-SHA256 từ chuỗi `transactionId` với key `PAYMENT_WEBHOOK_SECRET + ":" + gateway` theo định dạng hex lowercase.
