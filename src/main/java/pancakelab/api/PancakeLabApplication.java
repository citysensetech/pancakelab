package main.java.pancakelab.api;

import main.java.pancakelab.concurrency.LockManager;
import main.java.pancakelab.repository.InMemoryOrderRepository;
import main.java.pancakelab.service.DeliveryService;
import main.java.pancakelab.service.KitchenService;
import main.java.pancakelab.service.OrderService;
import main.java.pancakelab.validation.InputValidator;

/**
 * Simple bootstrapper to wire minimal components. Real wiring will grow with implementation.
 */
public final class PancakeLabApplication {
    private PancakeLabApplication() {
    }

    public static PancakeLabApi bootstrap() {
        InputValidator validator = new InputValidator();
        LockManager lockManager = new LockManager();
        InMemoryOrderRepository repository = new InMemoryOrderRepository();
        OrderService orderService = new OrderService() { };
        KitchenService kitchenService = new KitchenService() { };
        DeliveryService deliveryService = new DeliveryService() { };
        return new PancakeLabApiImpl(validator, lockManager, repository, orderService, kitchenService, deliveryService);
    }
}
