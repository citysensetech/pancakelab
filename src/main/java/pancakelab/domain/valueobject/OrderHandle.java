package main.java.pancakelab.domain.valueobject;

public final class OrderHandle {
    private final String value;

    public OrderHandle(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("order handle value cannot be blank");
        }
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderHandle that = (OrderHandle) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
