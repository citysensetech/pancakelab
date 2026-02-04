package main.java.pancakelab.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import main.java.pancakelab.domain.ingredient.ForbiddenCombinationPolicy;
import main.java.pancakelab.domain.valueobject.OrderHandle;
import main.java.pancakelab.domain.valueobject.OrderStatus;

public class InMemoryOrderRepository {
    private final Map<OrderHandle, OrderStatus> statuses = new HashMap<>();
    private final Map<OrderHandle, Map<String, List<String>>> pancakes = new HashMap<>();
    private final ForbiddenCombinationPolicy combinationPolicy;

    public InMemoryOrderRepository() {
        this(new ForbiddenCombinationPolicy(Collections.emptyList()));
    }

    public InMemoryOrderRepository(ForbiddenCombinationPolicy combinationPolicy) {
        this.combinationPolicy = Objects.requireNonNull(combinationPolicy);
    }

    public void save(OrderHandle handle, OrderStatus status) {
        statuses.put(handle, status);
        pancakes.computeIfAbsent(handle, key -> new HashMap<>());
    }

    public void addIngredient(OrderHandle handle, String pancakeName, String ingredientName) {
        Map<String, List<String>> byName = pancakes.computeIfAbsent(handle, key -> new HashMap<>());
        List<String> ingredients = byName.computeIfAbsent(pancakeName, key -> new ArrayList<>());
        combinationPolicy.validate(ingredients, ingredientName);
        ingredients.add(ingredientName);
    }

    public Map<String, List<String>> findPancakes(OrderHandle handle) {
        Map<String, List<String>> byName = pancakes.get(handle);
        if (byName == null) {
            return Collections.emptyMap();
        }
        Map<String, List<String>> copy = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : byName.entrySet()) {
            copy.put(entry.getKey(), Collections.unmodifiableList(new ArrayList<>(entry.getValue())));
        }
        return Collections.unmodifiableMap(copy);
    }

    public OrderStatus findStatus(OrderHandle handle) {
        return statuses.get(handle);
    }

    public boolean exists(OrderHandle handle) {
        return statuses.containsKey(handle);
    }

    public void delete(OrderHandle handle) {
        statuses.remove(handle);
        pancakes.remove(handle);
    }
}
