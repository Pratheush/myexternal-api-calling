package com.mylearning.journalapp.clientopenfeign;

public class FeignContextHolder {
    private static final ThreadLocal<Boolean> skipAuthorization = ThreadLocal.withInitial(() -> false);

    public static void setSkipAuthorization(boolean value) {
        skipAuthorization.set(value);
    }

    public static boolean isSkipAuthorization() {
        return skipAuthorization.get();
    }

    public static void clear() {
        skipAuthorization.remove();
    }
}
