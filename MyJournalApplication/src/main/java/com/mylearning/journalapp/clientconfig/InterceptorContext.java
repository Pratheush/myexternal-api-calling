package com.mylearning.journalapp.clientconfig;

public class InterceptorContext {
    private static final ThreadLocal<Boolean> disableInterceptor = ThreadLocal.withInitial(() -> false);

    public static void setDisableInterceptor(boolean value) {
        disableInterceptor.set(value);
    }

    public static boolean isInterceptorDisabled() {
        return disableInterceptor.get();
    }

    public static void clear() {
        disableInterceptor.remove();
    }
}
