package edu.nd.cse.ids.project;

import com.opencsv.bean.*;

import java.util.List;
import java.util.HashMap;
import java.io.FileReader;

public class RestaurantEntryReader
{
    private List<RestaurantEntry> restaurants;

    public RestaurantEntryReader()
    {
        restaurants = null;
    }

    public void readRestaurantEntryFile(String filename)
    {
        try {
            this.restaurants = new CsvToBeanBuilder(new FileReader(filename))
                                .withType(RestaurantEntry.class).build().parse();
        } catch(Exception ex)
        {
            System.out.println(ex);
        }
    }
    
    public List<RestaurantEntry> getRestaurants()
    {
        return this.restaurants;
    }
}
