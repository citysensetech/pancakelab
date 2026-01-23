package test.java.pancakelab.api;

import main.java.pancakelab.api.PancakeLabApi;
import main.java.pancakelab.api.PancakeLabApplication;
import main.java.pancakelab.domain.valueobject.OrderHandle;
import main.java.pancakelab.domain.valueobject.OrderStatus;

public class PancakeLabApiTest {
    public static void main(String[] args) {
        PancakeLabApi api = PancakeLabApplication.bootstrap();
        assert api != null : "API bootstrap should not return null";

        System.out.println("Step 1: creating new order and validating handle");
        // Step 1: create a new order and ensure handle is valid
        OrderHandle handle = api.startOrder("dojo", "101A");
        assert handle != null : "startOrder should return a handle";
        assert !handle.value().isBlank() : "handle value should not be blank";

        System.out.println("Step 2: verifying unsupported operations fail fast");
        // Step 2: unsupported operations still fail fast
        expectUnsupported(() -> api.completeOrder(handle), "completeOrder");
        expectUnsupported(() -> api.markPrepared(handle), "markPrepared");
        expectUnsupported(() -> api.dispatch(handle), "dispatch");

        System.out.println("Step 3: checking status is CREATED for fresh order");
        // Step 3: status of a fresh order is CREATED
        shouldReportCreatedStatusForNewOrder(api, handle);
        System.out.println("Step 4: cancelling order and expecting CANCELLED status");
        // Step 4: cancelling moves order to CANCELLED
        shouldCancelNewOrderAndReportCancelledStatus(api);
        System.out.println("Step 5: adding ingredient while CREATED and keeping status CREATED");
        // Step 5: adding ingredient in CREATED succeeds and keeps status CREATED
        shouldAddIngredientWhileCreated(api);

        System.out.println("PancakeLabApiTest passed");
    }

    private static void shouldAddIngredientWhileCreated(PancakeLabApi api) {
        System.out.println(" - starting order for ingredient addition");
        OrderHandle createdHandle = api.startOrder("dojo", "103C");
        try {
            api.addIngredient(createdHandle, "plain", "banana");
            System.out.println(" - addIngredient succeeded for CREATED order");
        } catch (Exception e) {
            throw new AssertionError("addIngredient should succeed for CREATED order", e);
        }
        OrderStatus statusAfterAdd = api.statusOf(createdHandle);
        assert statusAfterAdd == OrderStatus.CREATED : "status should remain CREATED after adding ingredient";
    }

    private static void shouldCancelNewOrderAndReportCancelledStatus(PancakeLabApi api) {
        System.out.println(" - starting order for cancellation");
        OrderHandle cancelHandle = api.startOrder("dojo", "102B");
        api.cancelOrder(cancelHandle);
        System.out.println(" - order cancelled");
        OrderStatus statusAfterCancel = api.statusOf(cancelHandle);
        assert statusAfterCancel == OrderStatus.CANCELLED : "cancelled order should be in CANCELLED status";
    }

    private static void shouldReportCreatedStatusForNewOrder(PancakeLabApi api, OrderHandle handle) {
        OrderStatus status = api.statusOf(handle);
        System.out.println(" - status for fresh order: " + status);
        assert status == OrderStatus.CREATED : "new order should be in CREATED status";
    }

    private static void expectUnsupported(Runnable action, String name) {
        try {
            action.run();
            throw new AssertionError(name + " should be unsupported for now");
        } catch (UnsupportedOperationException expected) {
            System.out.println(" - " + name + " correctly unsupported");
        }
    }
}
