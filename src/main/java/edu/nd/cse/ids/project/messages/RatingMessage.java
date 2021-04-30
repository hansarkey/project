package edu.nd.cse.ids.project.messages;

import edu.nd.cse.ids.project.*;

public class RatingMessage extends Message
{
    private float rating;
    
    public RatingMessage()
    {
    }
    
    public void generate(RestaurantEntry restaurant)
    {
        this.rating = restaurant.getRating();
    }
    
    public void setRating(float rating)
    {
        this.rating = rating;
    }
    
    public float getRating()
    {
        return this.rating;
    }
}
