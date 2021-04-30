package edu.nd.cse.ids.project.messages;

import edu.nd.cse.ids.project.*;

public class NameMessage extends Message
{
    private String name;
    
    public NameMessage()
    {
    }
    
    public void generate(RestaurantEntry restaurant)
    {
        this.name = restaurant.getRestaurantName();
    }
    
    public void setRestaurantName(String name)
    {
        this.name = name;
    }
    
    public String getRestaurantName()
    {
        return this.name;
    }
}
