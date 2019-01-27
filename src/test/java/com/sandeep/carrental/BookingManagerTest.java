package com.sandeep.carrental;

import org.junit.*;
import org.junit.runners.MethodSorters;

import java.awt.print.Book;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit test for Inventory App.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BookingManagerTest
{
    static BookingManager manager ;

    @BeforeClass
    public static void initialiseBookingSystem()
    {
        initialiseInventoryDefaults();
        System.out.println("\nTest Cases for Car rental system \n");
        manager = new BookingManager();
    }

    @Before
    public void beforeEachTest()
    {
        System.out.print("\nTEST Case name : ");
    }

    @After
    public void afterEachTest()
    {
        System.out.println("\n--------- Finished ----------");
    }

    @Test
    public void testIfCarTypeIsNotAvailable() throws BookingException {
        System.out.print("testIfCarTypeIsNotAvailable\n\n");

        List<Car> cars = manager.getAvailableCarsByType("BlaBla");
        assertEquals(0, cars.size());
    }

    @Test
    public void testIfBookingIsWorking() throws BookingException {
        System.out.print("testIfBookingIsWorking\n\n");

        List<Car> cars = manager.getAvailableCarsByType("SUV");

        Calendar refDate = Calendar.getInstance();
        int count = 0 ;
        for(Car c : cars)
        {
            refDate.add(Calendar.DATE, ++count);
            Booking b = manager.createBooking(c.getId(), refDate.getTime(), 1);
            assertNotNull(b);
        }
    }

    @Test
    public void testIfBookingsAreEvenlyDistributed() throws BookingException {
        System.out.print("testIfBookingsAreEvenlyDistributed\n\n");

        List<Car> cars = manager.getAvailableCarsByType("Truck");

        Calendar refDate = Calendar.getInstance();
        int count = cars.size() ;
        while(count > 0)
        {
            Booking b = manager.requestBookingByType("Truck", refDate.getTime(), 1);
            assertNotNull(b);
            count -- ;
        }

        for(Car c : cars)
        {
            Set<Booking> bookings = manager.getBookingsByCarId(c.getId());
            assertEquals(1, bookings.size());
        }
        System.out.println("\nWe have successfully checked that each car is booked");
    }

    @Test
    public void testIfBookingIsAllowedForACarIdForFreeSlot() throws BookingException {
        System.out.print("testIfBookingIsAllowedForACarIdInBetween\n\n");

        List<Car> cars = manager.getAvailableCarsByType("Sedan");
        Calendar refDate = Calendar.getInstance();

        /* booked from day1 to day2 */
        Booking newBooking = manager.createBooking(cars.get(0).getId(), refDate.getTime(), 1);
        assertNotNull(newBooking);

        /* booked from day3  to day5 */
        refDate.add(Calendar.DATE, 2);
        newBooking = manager.createBooking(cars.get(0).getId(), refDate.getTime(), 2);
        assertNotNull(newBooking);

        /* booked from day2  to day3 in between */
        refDate.add(Calendar.DATE, -1);
        newBooking = manager.createBooking(cars.get(0).getId(), refDate.getTime(), 1);
        assertNotNull(newBooking);
    }


    @Test(expected = BookingException.class)
    public void testIfBookingIsNotAllowedForSameTimeForSameCarId() throws BookingException {
        System.out.print("testIfBookingIsNotAllowedForSameTimeForSameCarId\n\n");

        List<Car> cars = manager.getAvailableCarsByType("Van");
        Calendar refDate = Calendar.getInstance();
        Booking newBooking = manager.createBooking(cars.get(0).getId(), refDate.getTime(), 1);
        assertNotNull(newBooking);

        manager.createBooking(cars.get(0).getId(), refDate.getTime(), 1);
    }

    @Test
    public void testIfBookingIsCanceled() throws BookingException {
        System.out.print("testIfBookingIsCanceled\n\n");

        List<Car> cars = manager.getAvailableCarsByType("Wagon");
        Calendar refDate = Calendar.getInstance();
        Booking newBooking = manager.createBooking(cars.get(0).getId(), refDate.getTime(), 1);
        assertNotNull(newBooking);

        int status = manager.cancelBooking(newBooking.getId());
        assertEquals(status, Constants.SUCCESS);

        status = manager.cancelBooking(newBooking.getId());
        assertEquals(status, Constants.NOT_FOUND);

        Booking b = manager.getBookingById(newBooking.getId());
        assertEquals(null, b);

        Set<Booking> bookings = manager.getBookingsByCarId(cars.get(0).getId());
        assertEquals(0, bookings.size());

        newBooking = manager.createBooking(cars.get(0).getId(), refDate.getTime(), 1);
        assertNotNull(newBooking);
    }

    /* have added defaults initialisation function here */
    public static void initialiseInventoryDefaults()
    {
        System.out.println("\nInventory Initialising ....");
        //4 SUVs
        Inventory.getInstance().addCar("SUV");
        Inventory.getInstance().addCar("SUV");
        Inventory.getInstance().addCar("SUV");
        Inventory.getInstance().addCar("SUV");
        //5 Trucks
        Inventory.getInstance().addCar("Truck");
        Inventory.getInstance().addCar("Truck");
        Inventory.getInstance().addCar("Truck");
        Inventory.getInstance().addCar("Truck");
        Inventory.getInstance().addCar("Truck");
        //6 Sedan
        Inventory.getInstance().addCar("Sedan");
        Inventory.getInstance().addCar("Sedan");
        Inventory.getInstance().addCar("Sedan");
        Inventory.getInstance().addCar("Sedan");
        Inventory.getInstance().addCar("Sedan");
        Inventory.getInstance().addCar("Sedan");
        //2 Van
        Inventory.getInstance().addCar("Van");
        Inventory.getInstance().addCar("Van");
        //1 Wagon
        Inventory.getInstance().addCar("Wagon");
        System.out.println("Inventory Initialised....\n");
    }
}
