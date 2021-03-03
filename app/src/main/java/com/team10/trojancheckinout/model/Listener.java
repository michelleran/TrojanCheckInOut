package com.team10.trojancheckinout.model;

/**
 * <p>How to pass to a method:</p>
 * <pre>{@code
 * myMethod(..., new Listener<T>() {
 *      @Override
 *      public void onAdd(T item) {
 *          // ...
 *      }
 *
 *      @Override
 *      public void onRemove(T item) {
 *          // ...
 *      }
 *
 *      // etc.
 * });
 * }</pre>
 */
public interface Listener<T> {
    void onAdd(T item);
    void onRemove(T item);
    void onUpdate(T item);
    void onFailure(Exception exception);
}
