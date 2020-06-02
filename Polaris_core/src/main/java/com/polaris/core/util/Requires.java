package com.polaris.core.util;

public final class Requires {

    /**
     * Checks that the specified object reference is not {@code null}.
     *
     * @param obj the object reference to check for nullity
     * @param <T> the type of the reference
     * @return {@code obj} if not {@code null}
     * @throws NullPointerException if {@code obj} is {@code null}
     */
    public static <T> T requireNonNull(T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }

    /**
     * Checks that the specified object reference is not {@code null} and
     * throws a customized {@link NullPointerException} if it is.
     *
     * @param obj     the object reference to check for nullity
     * @param message detail message to be used in the event that a {@code
     *                NullPointerException} is thrown
     * @param <T>     the type of the reference
     * @return {@code obj} if not {@code null}
     * @throws NullPointerException if {@code obj} is {@code null}
     */
    public static <T> T requireNonNull(T obj, String message) {
        if (obj == null) {
            throw new NullPointerException(message);
        }
        return obj;
    }

    /**
     * Ensures the truth of an expression involving one or more parameters
     * to the calling method.
     *
     * @param expression a boolean expression
     * @throws IllegalArgumentException if {@code expression} is false
     */
    public static void requireTrue(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Ensures the truth of an expression involving one or more parameters
     * to the calling method.
     *
     * @param expression a boolean expression
     * @param message    the exception message to use if the check fails;
     *                   will be converted to a string using
     *                   {@link String#valueOf(Object)}
     * @throws IllegalArgumentException if {@code expression} is false
     */
    public static void requireTrue(boolean expression, Object message) {
        if (!expression) {
            throw new IllegalArgumentException(String.valueOf(message));
        }
    }

    /**
     * Ensures the truth of an expression involving one or more parameters
     * to the calling method.
     *
     * @param expression a boolean expression
     * @param fmt        the exception message with format string
     * @param args       arguments referenced by the format specifiers in the format
     *                   string
     * @throws IllegalArgumentException if {@code expression} is false
     */
    public static void requireTrue(boolean expression, String fmt, Object... args) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(fmt, args));
        }
    }

    private Requires() {
    }
}

