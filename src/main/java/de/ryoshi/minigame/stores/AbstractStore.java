package de.ryoshi.minigame.stores;

import de.ryoshi.minigame.model.AbstractGameObject;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AbstractStore<T extends AbstractGameObject>  {

    protected final ConcurrentHashMap<Integer, T> store;
    protected final AtomicInteger counter;

    public AbstractStore() {
        store = new ConcurrentHashMap<>();
        counter = new AtomicInteger(0);
    }

    public T save(T obj) {
        int id = this.counter.incrementAndGet();
        obj.setId(id);
        store.put(id, obj);
        return store.get(id);
    }

    public void delete(int id) {
        store.remove(id);
    }

    public boolean exists(int id) {
        return store.containsKey(id);
    }

    public T find(int id) {
        return store.get(id);
    }

    public Collection<T> getAll() {
        return store.values();
    }

}
