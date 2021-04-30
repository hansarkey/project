package edu.nd.cse.ids.project.messages;

import edu.nd.cse.ids.project.*;

public class CuisineMessage extends Message
{
    private String cuisine;
    
    public CuisineMessage()
    {
    }
    
    public void generate(RestaurantEntry restaurant)
    {
        this.cuisine = restaurant.getCuisine();
    }
    
    public void setCuisine(String cuisine)
    {
        this.cuisine = cuisine;
    }
    
    public String getCuisine()
    {
        return this.cuisine;
    }
}
