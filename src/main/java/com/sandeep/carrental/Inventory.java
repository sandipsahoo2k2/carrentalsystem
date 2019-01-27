package com.sandeep.carrental;

import java.util.*;
import java.util.stream.Collectors;

public class Inventory
{
    /* we could have count cars by type but for ease i am using another map */
    Set<Car> store ;
    long nextId = 0;

    /* Inventory is singleton , early initialisation as not dealing with threading */
    private static final Inventory inventory = new Inventory();

    private Inventory()
    {
        store = new HashSet<Car>();
    }

    public static Inventory getInstance()
    {
        return inventory;
    }

    long size()
    {
        return store.size();
    }

    long countCars(String aType)
    {
        return store.stream().filter(c -> c.getId().startsWith(aType)).count();
    }

    List<Car> listCars(String aType) {
        return store.stream().filter(c -> c.getId().startsWith(aType)).collect(Collectors.toList());
    }

    String addCar(String aType) {
        Car newcar = new Car(aType, ++nextId);
        store.add(newcar);
        return newcar.getId();
    }

    void removeCarById(String aCarId) throws InventoryException
    {
        Car aCar = Car.getCarById(aCarId);
        if(aCar == null || !store.remove(aCar))
        {
            throw new InventoryException("NotFound");
        }
    }

    public void clear()
    {
        store.clear();
    }
}