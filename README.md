**Problem statement**

Design and prototype a car rental system using object-oriented principles. Please focus on delivering the following core features:

1.      The system should let a customer reserve a car of a given type at a desired date and time for a given number of days

2.     The number of cars of each type is limited, but customers should be able to reserve a single rental car for multiple, non-overlapping time frames

3.      Provide a Junit test that illustrates the core reservation workflow and demonstrates its correctness

Please use Java as the implementation language.

While the solution should allow for extension to be exposed as a service, please note that

·        No UI needs to be provided

·        No need to explicitly suspend the solution in a web container, e.g. using spring boot

·        No need to explicitly integrate the solution with a database

**Solution & Assumptions**

In this solutions for any given Date minute, second and millicond part is stripped before requesting any booking.
e.g booking at 01/27/2019 17:45:30 will be treated as 01/27/2019 17:00:00

Classes of Interest :

    BookingManager.java
    BookingManagerTest.java

    Inventory.java
    InventoryTest.java

MinHeap of Cars which are least booked is used for the benifit of customers so that bookings are evenly distributed

**Test**

    Run BookingManagerTest cases which covers all core functionality
    Prepared by : Sandeep Sahoo Email : sandipsahoo2k2@gmail.com
