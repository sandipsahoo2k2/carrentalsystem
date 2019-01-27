package com.sandeep.carrental;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.List;

/**
 * Unit test for Inventory App.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InventoryTest
{
    static Inventory inventory ;

    @BeforeClass
    public static void initialiseInventory()
    {
        inventory = Inventory.getInstance();
        initialiseDefaults();
    }

    @Test
    public void countDefaultStocks()
    {
        long suv = inventory.countCars("SUV");
        assertEquals(4, suv);

        long truck = inventory.countCars("Truck");
        assertEquals(5, truck);

        long sedan = inventory.countCars("Sedan");
        assertEquals(6, sedan);

        long van = inventory.countCars("Van");
        assertEquals(2, van);

        long wagon = inventory.countCars("Wagon");
        assertEquals(1, wagon);
    }

    @Test
    public void testAddCarToStore()
    {
        inventory.addCar("Van");
        long wagon = inventory.countCars("Van");
        assertEquals(3, wagon);
    }

    @Test(expected = InventoryException.class)
    public void testRemoveCarFromStore() throws InventoryException
    {
        List<Car> wagons = inventory.listCars("Wagon");
        for(Car c : wagons) {
            inventory.removeCarById(c.getId());
            long wagon = inventory.countCars("Wagon");
            assertEquals(0, wagon);
            /* throw exception */
            inventory.removeCarById(c.getId());
        }
    }

    /* have added defaults initialisation function here */
    public static void initialiseDefaults()
    {
        System.out.println("Inventory Initialising ....");
        //4 SUVs
        inventory.addCar("SUV");
        inventory.addCar("SUV");
        inventory.addCar("SUV");
        inventory.addCar("SUV");
        //5 Trucks
        inventory.addCar("Truck");
        inventory.addCar("Truck");
        inventory.addCar("Truck");
        inventory.addCar("Truck");
        inventory.addCar("Truck");
        //6 Sedan
        inventory.addCar("Sedan");
        inventory.addCar("Sedan");
        inventory.addCar("Sedan");
        inventory.addCar("Sedan");
        inventory.addCar("Sedan");
        inventory.addCar("Sedan");
        //2 Van
        inventory.addCar("Van");
        inventory.addCar("Van");
        //1 Wagon
        inventory.addCar("Wagon");
        System.out.println("Inventory Initialised");
    }
}
