# Payment Platform - 결제 및 취소 시스템

## 제공 기능
- 결제 승인
- 결제 취소(부분 취소, 전체 취소)
- 주문 ID 기반 결제 조회
- 결제 ID 기반 결제 조회
- 가맹점별 결제 목록 조회
- 상태별 결제 목록 조회

## 핵심 설계
- 결제 현재 상태 조회 성능을 위해 `payment`에 현재 상태와 누적 취소 금액을 저장한다.
- 취소 이력 관리를 위해 `payment_cancel`을 분리하고, 승인/취소 이벤트 추적을 위해 `payment_event`를 저장한다.
- 외부에서 전달되는 `paymentId`는 내부 PK로 직접 사용하지 않고, 가맹점 범위에서만 유일한 비즈니스 키로 관리한다.
- `payment`는 내부 식별자와 외부 식별자(`merchantId + paymentId`)를 분리하고, `idempotency_key` 테이블에 요청 해시와 처리 결과 리소스를 저장해 중복 승인 요청을 한 번만 처리한다.
- 동일 결제에 대한 동시 취소 요청은 결제 단위 행 잠금으로 직렬화하고, 트랜잭션 안에서 누적 취소 금액이 승인 금액을 초과하지 않는지 검증한다.
- 결제 상태는 `APPROVED`, `PARTIAL_CANCELED`, `CANCELED`로 관리하며, 누적 취소 금액 기준으로 상태 전이 정합성을 보장한다.

## 1. ERD
merchant
- merchant_id (PK)
- merchant_name
- created_at
- updated_at

payment
- payment_no (PK)
- merchant_id (FK -> merchant.merchant_id)
- payment_id
- order_id
- approved_amount
- canceled_amount
- status (APPROVED / PARTIAL_CANCELED / CANCELED)
- created_at
- updated_at

제약 조건
- UNIQUE (merchant_id, payment_id)
- UNIQUE (merchant_id, order_id)

인덱스
- INDEX idx_payment_merchant_payment_id (merchant_id, payment_id)
- INDEX idx_payment_merchant_created_at (merchant_id, created_at)
- INDEX idx_payment_merchant_status_created_at (merchant_id, status, created_at)

payment_cancel
- cancel_id (PK)
- payment_no (FK -> payment.payment_no)
- cancel_amount
- created_at

인덱스
- INDEX idx_payment_cancel_payment_no (payment_no)

payment_event
- event_id (PK)
- payment_no (FK -> payment.payment_no)
- event_type (APPROVED / CANCELED)
- event_amount
- created_at

인덱스
- INDEX idx_payment_event_payment_no_created_at (payment_no, created_at)

idempotency_key
- id (PK)
- merchant_id
- idempotency_key
- request_hash
- resource_type
- resource_id
- status
- created_at
- expired_at

제약 조건
- UNIQUE (merchant_id, idempotency_key)

인덱스
- INDEX idx_idempotency_key_expired_at (expired_at)

## 2. 결제 상태 전이
- `APPROVED`: 결제 승인 완료, 누적 취소 금액 0
- `PARTIAL_CANCELED`: 부분 취소 완료, 0 < 누적 취소 금액 < 승인 금액
- `CANCELED`: 전체 취소 완료, 누적 취소 금액 = 승인 금액

허용 전이
- `APPROVED -> PARTIAL_CANCELED`
- `APPROVED -> CANCELED`
- `PARTIAL_CANCELED -> PARTIAL_CANCELED`
- `PARTIAL_CANCELED -> CANCELED`

```mermaid
erDiagram
    MERCHANT ||--o{ PAYMENT : has
    PAYMENT ||--o{ PAYMENT_CANCEL : has
    PAYMENT ||--o{ PAYMENT_EVENT : has
    MERCHANT ||--o{ IDEMPOTENCY_KEY : owns

    MERCHANT {
        string merchant_id PK
        string merchant_name
        datetime created_at
        datetime updated_at
    }

    PAYMENT {
        long payment_no PK
        string merchant_id FK
        string payment_id
        string order_id
        bigint approved_amount
        bigint canceled_amount
        string status
        datetime created_at
        datetime updated_at
    }

    PAYMENT_CANCEL {
        long cancel_id PK
        long payment_no FK
        bigint cancel_amount
        datetime created_at
    }

    PAYMENT_EVENT {
        long event_id PK
        long payment_no FK
        string event_type
        bigint event_amount
        datetime created_at
    }

    IDEMPOTENCY_KEY {
        long id PK
        string merchant_id
        string idempotency_key
        string request_hash
        string resource_type
        string resource_id
        string status
        datetime created_at
        datetime expired_at
    }
```

## 3. API 설계

### 결제 승인
- `POST /api/v1/merchants/{merchantId}/payments/approvals`
- 요청 필드: `orderId`, `paymentId`, `amount`
- 응답 필드: `paymentId`, `merchantId`, `orderId`, `approvedAmount`, `canceledAmount`, `status`

### 결제 취소
- `POST /api/v1/merchants/{merchantId}/payments/{paymentId}/cancels`
- 요청 필드: `cancelAmount`
- 응답 필드: `paymentId`, `merchantId`, `orderId`, `approvedAmount`, `canceledAmount`, `status`

### 결제 조회
- `GET /api/v1/merchants/{merchantId}/payments/{paymentId}`
- `GET /api/v1/merchants/{merchantId}/payments/orders/{orderId}`
- `GET /api/v1/merchants/{merchantId}/payments?page=0&size=20`
- `GET /api/v1/merchants/{merchantId}/payments?status=APPROVED&page=0&size=20`

### 공통 정책
- `paymentId`는 가맹점 범위 내에서만 유일하며, 승인 멱등성은 `merchantId + paymentId` 기준으로 보장
- 취소는 승인된 결제만 가능
- 부분 취소 가능, 누적 취소 금액은 승인 금액 이하여야 함
- 외부 조회 식별자는 `merchantId + paymentId` 조합을 사용하고, 내부 연관 관계는 `payment_no`로 연결
- 결제 취소는 트랜잭션으로 처리하며 동일 결제에 대한 동시 취소 요청의 정합성을 보장함
