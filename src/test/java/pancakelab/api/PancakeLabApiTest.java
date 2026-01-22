package test.java.pancakelab.api;

import main.java.pancakelab.api.PancakeLabApi;
import main.java.pancakelab.api.PancakeLabApplication;
import main.java.pancakelab.domain.valueobject.OrderHandle;
import main.java.pancakelab.domain.valueobject.OrderStatus;

public class PancakeLabApiTest {
    public static void main(String[] args) {
        PancakeLabApi api = PancakeLabApplication.bootstrap();
        assert api != null : "API bootstrap should not return null";

        OrderHandle handle = api.startOrder("dojo", "101A");
        assert handle != null : "startOrder should return a handle";
        assert !handle.value().isBlank() : "handle value should not be blank";

        expectUnsupported(() -> api.addIngredient(handle, "plain", "banana"), "addIngredient");
        expectUnsupported(() -> api.completeOrder(handle), "completeOrder");
        expectUnsupported(() -> api.markPrepared(handle), "markPrepared");
        expectUnsupported(() -> api.dispatch(handle), "dispatch");

        shouldReportCreatedStatusForNewOrder(api, handle);
        shouldCancelNewOrderAndReportCancelledStatus(api);
        shouldAddIngredientWhileCreated(api);

        System.out.println("PancakeLabApiTest passed");
    }

    private static void shouldAddIngredientWhileCreated(PancakeLabApi api) {
        OrderHandle createdHandle = api.startOrder("dojo", "103C");
        try {
            api.addIngredient(createdHandle, "plain", "banana");
        } catch (Exception e) {
            throw new AssertionError("addIngredient should succeed for CREATED order", e);
        }
        OrderStatus statusAfterAdd = api.statusOf(createdHandle);
        assert statusAfterAdd == OrderStatus.CREATED : "status should remain CREATED after adding ingredient";
    }

    private static void shouldCancelNewOrderAndReportCancelledStatus(PancakeLabApi api) {
        OrderHandle cancelHandle = api.startOrder("dojo", "102B");
        api.cancelOrder(cancelHandle);
        OrderStatus statusAfterCancel = api.statusOf(cancelHandle);
        assert statusAfterCancel == OrderStatus.CANCELLED : "cancelled order should be in CANCELLED status";
    }

    private static void shouldReportCreatedStatusForNewOrder(PancakeLabApi api, OrderHandle handle) {
        OrderStatus status = api.statusOf(handle);
        assert status == OrderStatus.CREATED : "new order should be in CREATED status";
    }

    private static void expectUnsupported(Runnable action, String name) {
        try {
            action.run();
            throw new AssertionError(name + " should be unsupported for now");
        } catch (UnsupportedOperationException expected) {
            // expected until implemented
        }
    }
}
