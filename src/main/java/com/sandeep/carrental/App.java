package com.sandeep.carrental;


/**
 * Hello world!
 *
 */
public class App 
{
    static BookingManager manager ;
    static Inventory inventory = Inventory.getInstance();
    public static void main( String[] args )
    {
        System.out.println( "Car rental system" );
        manager = new BookingManager();
    }
}
