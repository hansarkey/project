package edu.nd.cse.ids.project.messages;

import edu.nd.cse.ids.project.*;

public class AddressMessage extends Message
{
    private String address;
    
    public AddressMessage()
    {
    }
    
    public void generate(RestaurantEntry restaurant)
    {
        this.address = restaurant.getAddress();
    }
    
    public void setAddress(String address)
    {
        this.address = address;
    }
    
    public String getAddress()
    {
        return this.address;
    }
}
