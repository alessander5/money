package com.infy.repo;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public abstract class CrudRepository<T> {

    public abstract Class<T> getEntityClass();

    public abstract Provider<EntityManager> getEntityManagerProvider();

    public <ID> Optional<T> getById(final ID id) {
        return Optional.ofNullable(getEntityManagerProvider().get().find(getEntityClass(), id));
    }

    public List<T> getAll() {
        return getEntityManagerProvider().get()
            .createQuery("Select t from " + getEntityClass().getSimpleName() + " t").getResultList();
    }

    public T create(final T object) {
        getEntityManagerProvider().get().persist(object);
        return object;
    }

    public boolean remove(final long id) {
        EntityManager entityManager = getEntityManagerProvider().get();
        T item = entityManager.find(getEntityClass(), id);
        if(item == null) {
            return false;
        } else {
            entityManager.remove(item);
            return true;
        }
    }

    public T update(final T object) {
        return getEntityManagerProvider().get().merge(object);
    }
}
