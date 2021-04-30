package edu.nd.cse.ids.project.messages;

import edu.nd.cse.ids.project.*;

public class PriceRangeMessage extends Message
{
    private int priceRange;
    
    public PriceRangeMessage()
    {
    }
    
    public void generate(RestaurantEntry restaurant)
    {
        this.priceRange= restaurant.getPriceRange();
    }
    
    public void setPriceRange(int priceRange)
    {
        this.priceRange = priceRange;
    }
    
    public int getPriceRange()
    {
        return this.priceRange;
    }
}
