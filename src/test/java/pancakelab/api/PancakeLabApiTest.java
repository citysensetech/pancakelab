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

        expectUnsupported(() -> api.markPrepared(handle), "markPrepared");
        expectUnsupported(() -> api.dispatch(handle), "dispatch");

        shouldReportCreatedStatusForNewOrder(api, handle);
        shouldCancelNewOrderAndReportCancelledStatus(api);
        shouldAddIngredientWhileCreated(api);
        shouldCompleteOrderFromCreated(api);
        shouldRejectAddIngredientAfterCompleted(api);
        shouldRejectCancelAfterCompleted(api);
        shouldMarkPreparedAfterCompleted(api);
        shouldDispatchPreparedAndBecomeUnreachable(api);
    }

    private static void shouldDispatchPreparedAndBecomeUnreachable(PancakeLabApi api) {
        OrderHandle handle = api.startOrder("dojo", "108H");
        api.completeOrder(handle);
        api.markPrepared(handle);
        try {
            api.dispatch(handle);
        } catch (Exception e) {
            throw new AssertionError("dispatch should succeed for PREPARED order", e);
        }
        try {
            api.statusOf(handle);
            throw new AssertionError("statusOf should fail after dispatch");
        } catch (IllegalArgumentException expected) {
            // expected: order no longer reachable
        }
    }

    private static void shouldMarkPreparedAfterCompleted(PancakeLabApi api) {
        OrderHandle handle = api.startOrder("dojo", "107G");
        api.completeOrder(handle);
        try {
            api.markPrepared(handle);
        } catch (Exception e) {
            throw new AssertionError("markPrepared should succeed for COMPLETED order", e);
        }
        OrderStatus statusAfterPrepare = api.statusOf(handle);
        assert statusAfterPrepare == OrderStatus.PREPARED : "status should be PREPARED after markPrepared";
    }

    private static void shouldRejectCancelAfterCompleted(PancakeLabApi api) {
        OrderHandle handle = api.startOrder("dojo", "106F");
        api.completeOrder(handle);
        try {
            api.cancelOrder(handle);
            throw new AssertionError("cancelOrder should fail after order is COMPLETED");
        } catch (IllegalStateException expected) {
            if (expected.getMessage() == null || !expected.getMessage().contains("COMPLETED")) {
                throw new AssertionError("Expected message to mention COMPLETED state", expected);
            }
        }
        OrderStatus statusAfterAttempt = api.statusOf(handle);
        assert statusAfterAttempt == OrderStatus.COMPLETED : "status should remain COMPLETED after failed cancelOrder";
    }

    private static void shouldRejectAddIngredientAfterCompleted(PancakeLabApi api) {
        OrderHandle handle = api.startOrder("dojo", "105E");
        api.completeOrder(handle);
        try {
            api.addIngredient(handle, "plain", "strawberry");
            throw new AssertionError("addIngredient should fail after order is COMPLETED");
        } catch (IllegalStateException expected) {
            if (expected.getMessage() == null || !expected.getMessage().contains("COMPLETED")) {
                throw new AssertionError("Expected message to mention COMPLETED state", expected);
            }
        }
        OrderStatus statusAfterAttempt = api.statusOf(handle);
        assert statusAfterAttempt == OrderStatus.COMPLETED : "status should remain COMPLETED after failed addIngredient";
    }

    private static void shouldCompleteOrderFromCreated(PancakeLabApi api) {
        OrderHandle handle = api.startOrder("dojo", "104D");
        try {
            api.completeOrder(handle);
        } catch (Exception e) {
            throw new AssertionError("completeOrder should succeed for CREATED order", e);
        }
        OrderStatus statusAfterComplete = api.statusOf(handle);
        assert statusAfterComplete == OrderStatus.COMPLETED : "status should be COMPLETED after completion";
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
