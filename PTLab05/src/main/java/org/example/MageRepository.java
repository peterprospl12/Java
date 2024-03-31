package org.example;

import java.util.*;

public class MageRepository {
    private Collection<Mage> collection;

    public MageRepository(Collection<Mage> collection) {
        this.collection = Objects.requireNonNullElseGet(collection, ArrayList::new);
    }

    public Collection<Mage> getCollection() {
        return collection;
    }


    public Optional<Mage> find(String name) {
        for (Mage mage : collection) {
            if (mage.getName().equals(name)) {
                return Optional.of(mage);
            }
        }
        return Optional.empty();
    }

    public void delete(String name) {
        Iterator<Mage> iterator = collection.iterator();
        while (iterator.hasNext()) {
            Mage mage = iterator.next();
            if (mage.getName().equals(name)) {
                iterator.remove();
                return;
            }
        }
        throw new IllegalArgumentException("Mage not found");
    }

    public void save(Mage mage) {
        for (Mage m : collection) {
            if (m.getName().equals(mage.getName())) {
                throw new IllegalArgumentException("Mage with this name already exists in collection");
            }
        }
        collection.add(mage);
    }
}
