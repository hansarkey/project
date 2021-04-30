package edu.nd.cse.ids.project.messages;

import edu.nd.cse.ids.project.*;

public class CityMessage extends Message
{
    private String city;
    
    public CityMessage()
    {
    }
    
    public void generate(RestaurantEntry restaurant)
    {
        this.city = restaurant.getCity();
    }
    
    public void setCity(String city)
    {
        this.city = city;
    }
    
    public String getCity()
    {
        return this.city;
    }
}
