package com.sandeep.carrental;

import java.util.*;

import static com.sandeep.carrental.Constants.*;

public class BookingManager {

    /* car_id as key and Bookings as the values */
    Map<String, Set<Booking>> bookingsByCar = new HashMap<>(); //master data

    /* Even this cache is unnecessary as we could parse the id and search from bookingsByCar
      but for an ideal system Ids are cryptic */
    Map<String, Booking> bookingsById = new HashMap<>();
    Inventory inventory = null;

    BookingManager()
    {
        inventory = Inventory.getInstance();
    }

    /* Core function for the given problem */
    public Booking requestBookingByType(String aCarType, Date aDate, int noOfdays) throws BookingException
    {
        Booking newBooking = null;
        /* find all cars for a given type */
        /* find availability for each car and find the first availability OR */
        /* We can even have a logic find the less booked car first by USING A MIN HEAP for cars
        * for the benefit of our customers */
        PriorityQueue<Car> cars = getLessBookedCarsFirst(aCarType);
        while (newBooking == null)
        {
            Car c = cars.poll() ; //heapify
            newBooking = createBooking(c.getId(), aDate, noOfdays);
        }
        return newBooking;
    }

    /* Can be used for service get operation */
    public Booking getBookingById(String aBookingId)
    {
        return bookingsById.get(aBookingId);
    }

    /* Can be used for service post operation */
    public Booking createBooking(String aCarId, Date aDate, int noOfdays) throws BookingException
    {
        if(noOfdays <= 0)
            throw new BookingException ("Booking is not allowed for Zero days !");

        aDate = Utils.adjustTime(aDate);
        Date now  = Utils.adjustTime(new Date());
        if(aDate.before(now))
            throw new BookingException ("Date can not be before today !");

        String bookingId = null;
        boolean canBook = true;
        Booking newBooking = null;
        Set<Booking> bookings = bookingsByCar.getOrDefault(aCarId, new TreeSet<>());
        if(bookings.size() > 0)
        {
            //check if the booking is possible and then add
            for(Booking b : bookings)
            {
                if(Utils.findOverlap(aDate, noOfdays, b.getStartDate(), b.getEndDate()))
                {
                    canBook = false;
                    String msg = "Booking not possible due to a conflict with : " + b;
                    System.out.println(msg);
                    throw new BookingException (msg);
                    //break; //we could have break here and return null also
                }
            }
        }
        if(canBook) {
            newBooking = new Booking(Car.getCarById(aCarId), aDate, noOfdays);
            bookings.add(newBooking);
            bookingId = newBooking.getId();
            bookingsByCar.put(aCarId, bookings);
            bookingsById.put(bookingId, newBooking);
            System.out.println("New Booking Created : " + newBooking);
        }
        return newBooking;
    }

    /* its simple search and update but we would rather create a new order canceling the old booking
        an existing booking and create a new one cause its a new unique booking
         and we dont want to loose the information that the user has infact created a new booking */
    public Booking updateBooking(String aBookingId, String aCarId, Date aDate, int noOfdays) throws BookingException
    {
        Booking newBooking = null ;
        int status = cancelBooking(aBookingId);
        if(status == SUCCESS)
        {
            newBooking = createBooking(aCarId, aDate, noOfdays);
        }
        else if(status == NOT_FOUND)
        {
            throw new BookingException("Booking Id not found");
        }
        return newBooking;
    }

    /* Can be used for service delete operation
    I didn't throw exception to showcase return with valid a meaningful Status Codes */
    public int cancelBooking(String aBookingId)
    {
        Booking found = bookingsById.get(aBookingId);
        if(found != null)
        {
            String carId = found.getCar().getId() ;
            Set<Booking> allBookingsForThisCar = bookingsByCar.get(carId);
            allBookingsForThisCar.remove(found);
            bookingsById.remove(aBookingId); //Remove booking from cache also
            System.out.println("Booking Canceled : " + aBookingId);
            return SUCCESS;
        }
        else
        {
            return NOT_FOUND;
        }
    }

    public PriorityQueue<Car> getLessBookedCarsFirst(String aCarType)
    {
        List<Car> cars = getAvailableCarsByType(aCarType);

        PriorityQueue<Car> carsHeap = new PriorityQueue<>( (newVal, oldVal) -> {
            Set<Booking> newList = bookingsByCar.getOrDefault(newVal.getId(), new TreeSet<>());
            Set<Booking> oldList = bookingsByCar.getOrDefault(oldVal.getId(), new TreeSet<>());
            return newList.size() - oldList.size() ;
        });
        for(Car c : cars)
        {
            carsHeap.add(c);
        }
        return carsHeap;
    }

    public Set<Booking> getBookingsByCarId(String aCarId)
    {
        return bookingsByCar.getOrDefault(aCarId, new TreeSet<>());
    }

    public List<Car> getAvailableCarsByType(String aType)
    {
        return inventory.listCars(aType);
    }
}
