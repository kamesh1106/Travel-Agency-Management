package com.travelagencies.dao;

import java.util.Optional;

public interface Repository<T> {

    void save(T item);

    void update(T item);

    void delete(T item);

    Optional<T> getById(int id);
}
