package com.sandeep.carrental;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

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
public class UtilsTest
{
    @BeforeClass
    public static void initialiseInventory()
    {
    }

    @Test
    public void testTimeOverlap()
    {
        Calendar c = Calendar.getInstance();
        c.set(2019, 1, 26, 5, 0);
        Date startTime = c.getTime() ;
        c.set(2019, 1, 27, 5, 0);
        Date endTime = c.getTime() ;

        Date given_time = startTime; // 1/26/2019-5.0.0

        boolean isOverlap = Utils.findOverlap(given_time, 1, startTime, endTime);
        assertEquals(true, isOverlap);

        given_time = endTime; // 1/27/2019-5.0.0

        /* test if the end date-time is excluded and allow for booking to start */
        isOverlap = Utils.findOverlap(given_time, 0, startTime, endTime);
        assertEquals(false, isOverlap);

        c.set(2019, 1, 28, 5, 0);
        given_time = c.getTime() ; // 1/28/2019-5.0.0

        /* test if the window right side is fine */
        isOverlap = Utils.findOverlap(given_time, 1, startTime, endTime);
        assertEquals(false, isOverlap);

        c.set(2019, 1, 25, 5, 0);
        given_time = c.getTime() ; // 1/25/2019-5.0.0

        /* test if the window left side is fine */
        isOverlap = Utils.findOverlap(given_time, 1, startTime, endTime);
        assertEquals(false, isOverlap);
    }

    @Test
    public void testAdjustTime()
    {
        try {
            String currentDateString = "01/27/2019 17:45:30";
            SimpleDateFormat sd = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            Date currentDate = sd.parse(currentDateString);
            Date adjustedTime = Utils.adjustTime(currentDate);
            String adjustedString = sd.format(adjustedTime);
            assertEquals("01/27/2019 17:00:00", adjustedString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
