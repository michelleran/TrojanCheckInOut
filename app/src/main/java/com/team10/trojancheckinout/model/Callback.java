package com.team10.trojancheckinout.model;

/**
 * <p>How to pass to a method:</p>
 * <pre>{@code
 * myMethod(..., new Callback<T>() {
 *      @Override
 *      public void onSuccess(T result) {
 *          // ...
 *      }
 *
 *      @Override
 *      public void onFailure(Exception exception) {
 *          // ...
 *      }
 * });
 * }</pre>
 */
public interface Callback<T> {
    void onSuccess(T result);
    void onFailure(Exception exception);
}
