package com.sandeep.carrental;

import java.util.Calendar;
import java.util.Date;

public class Utils {
    public static boolean findOverlap(Date aGivenDate, int plusNoOfdays, Date startDate, Date endDate)
    {
        Calendar refStartDate = Calendar.getInstance();
        refStartDate.setTime(aGivenDate);
        refStartDate.add(Calendar.DATE, plusNoOfdays);
        Date refEndDate = refStartDate.getTime() ;

        //if a given date is >= startdate and < enddate
        if(aGivenDate.getTime() >= startDate.getTime()
                && aGivenDate.getTime() < endDate.getTime())
        {
            return true;
        }
        //if a refEndDate < enddate && > startdate
        else if(refEndDate.getTime() > startDate.getTime()
                && refEndDate.getTime() < endDate.getTime())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static String generateBookingId(String aType, Date aDate)
    {
        return aType + aDate.toString();
    }

    /* strip the minutes, seconds, milliseconds */
    public static Date adjustTime(Date aDate)
    {
        Calendar refDate = Calendar.getInstance();
        refDate.setTime(aDate);
        refDate.set(Calendar.MINUTE, 0);
        refDate.set(Calendar.SECOND, 0);
        refDate.set(Calendar.MILLISECOND, 0);
        return refDate.getTime();
    }

}
