package com.team10.trojancheckinout.model;

public interface Callback<T> {
    void onSuccess(T result);
    void onFailure(Exception exception);
}
