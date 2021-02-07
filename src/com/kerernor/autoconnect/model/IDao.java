package com.kerernor.autoconnect.model;

public interface IDao<T> {

    void remove(T pingerToDelete);

    void add(T pinger);

    void loadData();
}
