 Pancake Lab

Pancake Lab is a **pure Java**, object-oriented application that models the lifecycle of pancake orders for disciples, from creation to delivery, with a strong focus on **domain rules**, **validation**, and **clean architecture**.

The project was developed with **TDD as a guiding principle**, but with pragmatic adjustments due to time constraints, prioritizing correctness, clarity, and extensibility over speculative implementation.

---

 Features

* Create pancake orders for a specific building and room
* Incrementally add ingredients to pancakes via the API
* Validate input data defensively
* Enforce forbidden ingredient combinations
* Control order lifecycle with strict state transitions
* Remove orders on cancel or dispatch
* Thread-safe design considerations (no shared mutable API exposure)
* No frameworks, no external dependencies — **pure Java**

---

Order Lifecycle

An order goes through the following states:

```
CREATED → COMPLETED → PREPARED → DISPATCHED
```

Additional rules:

* Orders can be **cancelled only while CREATED**
* Cancelling an order removes it from the system
* Dispatched orders are removed from the system
* Ingredients can only be added while the order is in CREATED state
* Invalid state transitions throw exceptions

---

Architectural Decisions

### 1. Pure Java, No Frameworks

All components are written using only the Java standard library, as required.

---

### 2. Clean API Boundary

The public API:

* Does **not expose domain objects**
* Uses value objects (`OrderHandle`, `OrderStatus`) only
* Returns defensive, read-only views of internal data

---

### 3. Incremental Ingredient Modeling

Disciples do **not** build pancakes upfront.
Ingredients are added one by one through the API, enforcing validation at each step.

---

### 4. Forbidden Ingredient Combinations (No Hardcoding)

Forbidden combinations are enforced via a **domain-level policy**:

* Rules are **provided at bootstrap time**
* No hardcoded combinations inside the policy itself
* The policy validates *current ingredients + new ingredient* incrementally
* Violations fail fast with clear exceptions

This keeps the domain flexible and secure against invalid or malicious input.

---

### 5. Services as Extension Points

The following services exist as **architectural placeholders**:

* `OrderService`
* `KitchenService`
* `DeliveryService`

They are intentionally **not implemented** in the current scope, as:

* No behavior is required yet
* They are not exercised by the API or tests
* Implementing them now would introduce speculative design

They exist to clearly communicate future responsibilities and maintain separation of concerns.

---

Testing Strategy

* Behavior validated through API-level tests
* State transitions and invalid operations are explicitly tested
* Defensive exceptions are part of the contract

Although not every class was developed strictly test-first, **TDD principles informed the design**, and the system is fully testable.

---

Package Structure

```
pancakelab
 ├── api
 │   ├── PancakeLabApi
 │   └── PancakeLabApiImpl
 ├── domain
 │   ├── ingredient
 │   │   └── ForbiddenCombinationPolicy
 │   └── valueobject
 │       ├── OrderHandle
 │       └── OrderStatus
 ├── repository
 │   └── InMemoryOrderRepository
 ├── service
 │   ├── OrderService
 │   ├── KitchenService
 │   └── DeliveryService
 ├── concurrency
 │   └── LockManager
 └── validation
     └── InputValidator
```

---

 Bootstrap Example

```java
PancakeLabApi api = PancakeLabApplication.bootstrap();

OrderHandle handle = api.startOrder("dojo", "101A");
api.addIngredient(handle, "plain", "banana");
api.completeOrder(handle);
api.markPrepared(handle);
api.dispatch(handle);
```

---

Future Improvements (Out of Scope)

* Persistent storage
* Asynchronous kitchen/delivery workflow
* Richer ingredient and recipe modeling
* External configuration of forbidden rules
* UML diagrams (domain & sequence)

---

Conclusion

Pancake Lab demonstrates how a small system can remain robust and expressive by:

* enforcing domain rules at the right layer
* avoiding premature abstraction
* keeping APIs clean and intention-revealing

The project is complete within its defined scope and ready for controlled evolution.


Claro 👍
Segue o **gabarito direto**, limpo, sem explicações — pronto para entrega.

---

Exercices:

1. Yes
2. No
3. No
4. 2
5. 8
6. b
7. True
8. b
9. a
10. a





