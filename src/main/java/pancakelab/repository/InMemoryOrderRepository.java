package main.java.pancakelab.repository;

import java.util.HashMap;
import java.util.Map;
import main.java.pancakelab.domain.valueobject.OrderHandle;
import main.java.pancakelab.domain.valueobject.OrderStatus;

public class InMemoryOrderRepository {
    private final Map<OrderHandle, OrderStatus> statuses = new HashMap<>();

    public void save(OrderHandle handle, OrderStatus status) {
        statuses.put(handle, status);
    }

    public OrderStatus findStatus(OrderHandle handle) {
        return statuses.get(handle);
    }

    public boolean exists(OrderHandle handle) {
        return statuses.containsKey(handle);
    }

    public void delete(OrderHandle handle) {
        statuses.remove(handle);
    }
}
