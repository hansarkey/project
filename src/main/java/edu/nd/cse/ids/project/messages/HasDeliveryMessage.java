package edu.nd.cse.ids.project.messages;

import edu.nd.cse.ids.project.*;

public class HasDeliveryMessage extends Message
{
    private String delivery;
    
    public HasDeliveryMessage()
    {
    }
    
    public void generate(RestaurantEntry restaurant)
    {
        this.delivery = restaurant.getHasOnlineDelivery();
    }
    
    public void setHasOnlineDelivery(String delivery)
    {
        this.delivery = delivery;
    }
    
    public String getOnlineDelivery()
    {
        return this.delivery;
    }
}
