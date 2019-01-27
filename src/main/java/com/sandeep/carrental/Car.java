package com.sandeep.carrental;

public class Car
{
    private String type ;
    private String id ;
    public Car(String aType, long id)
    {
        type = aType;
        setId(id);
    }

    public static Car getCarById(String aCarId)
    {
        if(aCarId != null && !aCarId.isEmpty())
        {
            String [] properties = aCarId.split("_")  ;
            return new Car(properties[0], Long.valueOf(properties[1]));
        }
        return null;
    }

    private void setId(long anId)
    {
        if(anId > 0) {
            id = type + "_" + anId;
        }
    }

    public String getId()
    {
        return id;
    }

    public boolean equals(Object obj)
    {
        if(obj==null) return false;
        if (!(obj instanceof Car))
            return false;
        if (obj == this)
            return true;
        return this.id.equals(((Car) obj).id);
    }

    @Override
    public int hashCode(){
        return this.id.hashCode();
    }

    @Override
    public String toString()
    {
        return id;
    }
}