package com.sandeep.carrental;

import java.util.Calendar;
import java.util.Date;

public class Booking implements Comparable<Booking>{
    private String id;
    private Car car;
    private Date startDate;
    private Date endDate;

    Booking(Car aCar, Date aDate, int noOfdays)
    {
        car = aCar ;
        startDate = aDate ;
        Calendar refDate = Calendar.getInstance();
        refDate.setTime(aDate);
        refDate.add(Calendar.DATE, noOfdays);
        endDate = refDate.getTime() ;
        //endDate = refDate.set(refDate.get(Calendar.YEAR), refDate.get(Calendar.MONTH), refDate.get(Calendar.DAY_OF_MONTH)
          //      + noOfdays, 0, 0, 0);
        setId(car.getId());
    }

    public String getId() {
        return id;
    }

    private void setId(String carId) {
        this.id = carId + "_" + startDate + "_" + endDate;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Booking other = (Booking) obj;

        if ((this.id == null) ? (other.getId() != null) : !this.id.equals(other.getId())) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public int compareTo(Booking o) {
        if(endDate.before(o.startDate))
            return -1;
        else if(startDate.after(o.endDate))
            return 1;
        else
            return 0;
    }
}
