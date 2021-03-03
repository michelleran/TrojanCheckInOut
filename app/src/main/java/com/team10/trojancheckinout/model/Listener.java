package com.team10.trojancheckinout.model;

public interface Listener<T> {
    void onAdd(T item);
    void onRemove(T item);
    void onUpdate(T item);
    void onFailure(Exception exception);
}
