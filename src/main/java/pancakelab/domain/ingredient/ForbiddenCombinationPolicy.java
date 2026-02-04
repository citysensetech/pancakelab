package main.java.pancakelab.domain.ingredient;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Evaluates whether adding an ingredient would violate any forbidden combination rules.
 */
public final class ForbiddenCombinationPolicy {
    private final List<Set<String>> forbiddenRules;

    public ForbiddenCombinationPolicy(List<Set<String>> forbiddenRules) {
        this.forbiddenRules = List.copyOf(Objects.requireNonNull(forbiddenRules));
    }

    public void validate(List<String> currentIngredients, String nextIngredient) {
        Objects.requireNonNull(currentIngredients, "currentIngredients");
        if (nextIngredient == null || nextIngredient.isBlank()) {
            throw new IllegalArgumentException("Ingredient must not be null or blank");
        }
        Set<String> snapshot = new HashSet<>();
        for (String ingredient : currentIngredients) {
            snapshot.add(normalize(ingredient));
        }
        snapshot.add(normalize(nextIngredient));
        for (Set<String> rule : forbiddenRules) {
            if (snapshot.containsAll(rule)) {
                throw new IllegalArgumentException("Forbidden ingredient combination");
            }
        }
    }

    private String normalize(String ingredient) {
        if (ingredient == null) {
            return "";
        }
        return ingredient.trim().toLowerCase();
    }
}
