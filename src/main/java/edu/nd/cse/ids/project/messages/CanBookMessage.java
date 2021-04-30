package edu.nd.cse.ids.project.messages;

import edu.nd.cse.ids.project.*;

public class CanBookMessage extends Message
{
    private String bookTables;
    
    public CanBookMessage()
    {
    }
    
    public void generate(RestaurantEntry restaurant)
    {
        this.bookTables = restaurant.getCanBook();
    }
    
    public void setCanBook(String bookTables)
    {
        this.bookTables = bookTables;
    }
    
    public String getCanBook()
    {
        return this.bookTables;
    }
}
