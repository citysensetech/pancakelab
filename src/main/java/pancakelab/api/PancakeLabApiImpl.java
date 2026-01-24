package main.java.pancakelab.api;

import java.util.Map;
import java.util.UUID;
import main.java.pancakelab.concurrency.LockManager;
import main.java.pancakelab.domain.valueobject.OrderHandle;
import main.java.pancakelab.domain.valueobject.OrderStatus;
import main.java.pancakelab.repository.InMemoryOrderRepository;
import main.java.pancakelab.service.DeliveryService;
import main.java.pancakelab.service.KitchenService;
import main.java.pancakelab.service.OrderService;
import main.java.pancakelab.validation.InputValidator;

/**
 * Temporary stub implementation to satisfy compilation; methods throw until implemented.
 */
final class PancakeLabApiImpl implements PancakeLabApi {
    private final InputValidator validator;
    private final LockManager lockManager;
    private final InMemoryOrderRepository repository;
    private final OrderService orderService;
    private final KitchenService kitchenService;
    private final DeliveryService deliveryService;

    PancakeLabApiImpl(InputValidator validator,
                      LockManager lockManager,
                      InMemoryOrderRepository repository,
                      OrderService orderService,
                      KitchenService kitchenService,
                      DeliveryService deliveryService) {
        this.validator = validator;
        this.lockManager = lockManager;
        this.repository = repository;
        this.orderService = orderService;
        this.kitchenService = kitchenService;
        this.deliveryService = deliveryService;
    }

    @Override
    public OrderHandle startOrder(String building, String room) {
        OrderHandle handle = new OrderHandle(UUID.randomUUID().toString());
        repository.save(handle, OrderStatus.CREATED);
        return handle;
    }

    @Override
    public void addIngredient(OrderHandle handle, String pancakeName, String ingredientName) {
        ensureKnownHandle(handle);
        OrderStatus status = repository.findStatus(handle);
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("Cannot add ingredients when order status is " + status);
        }
        repository.addIngredient(handle, pancakeName, ingredientName);
    }

    @Override
    public void completeOrder(OrderHandle handle) {
        ensureKnownHandle(handle);
        OrderStatus status = repository.findStatus(handle);
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("Cannot complete order unless it is in CREATED state");
        }
        repository.save(handle, OrderStatus.COMPLETED);
    }

    @Override
    public void cancelOrder(OrderHandle handle) {
        ensureKnownHandle(handle);
        OrderStatus status = repository.findStatus(handle);
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("Cannot cancel order when status is " + status);
        }
        repository.save(handle, OrderStatus.CANCELLED);
    }

    @Override
    public void markPrepared(OrderHandle handle) {
        ensureKnownHandle(handle);
        OrderStatus status = repository.findStatus(handle);
        if (status != OrderStatus.COMPLETED) {
            throw new UnsupportedOperationException("Cannot mark prepared when status is " + status);
        }
        repository.save(handle, OrderStatus.PREPARED);
    }

    @Override
    public void dispatch(OrderHandle handle) {
        ensureKnownHandle(handle);
        OrderStatus status = repository.findStatus(handle);
        if (status != OrderStatus.PREPARED) {
            throw new UnsupportedOperationException("Cannot dispatch when status is " + status);
        }
        repository.delete(handle);
    }

    @Override
    public OrderStatus statusOf(OrderHandle handle) {
        OrderStatus status = repository.findStatus(handle);
        if (status == null) {
            throw new IllegalArgumentException("Unknown order handle");
        }
        return status;
    }

    @Override
    public Map<String, java.util.List<String>> pancakesOf(OrderHandle handle) {
        ensureKnownHandle(handle);
        return repository.findPancakes(handle);
    }

    private void ensureKnownHandle(OrderHandle handle) {
        if (!repository.exists(handle)) {
            throw new IllegalArgumentException("Unknown order handle");
        }
    }
}
