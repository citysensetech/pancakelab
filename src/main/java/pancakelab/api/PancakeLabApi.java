package main.java.pancakelab.api;

import java.util.List;
import java.util.Map;
import main.java.pancakelab.domain.valueobject.OrderHandle;
import main.java.pancakelab.domain.valueobject.OrderStatus;

public interface PancakeLabApi {
    OrderHandle startOrder(String building, String room);

    void addIngredient(OrderHandle handle, String pancakeName, String ingredientName);

    void completeOrder(OrderHandle handle);

    void cancelOrder(OrderHandle handle);

    void markPrepared(OrderHandle handle);

    void dispatch(OrderHandle handle);

    OrderStatus statusOf(OrderHandle handle);

    Map<String, List<String>> pancakesOf(OrderHandle handle);
}
