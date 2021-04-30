package edu.nd.cse.ids.project.messages;

import edu.nd.cse.ids.project.*;

public class AvgCostMessage extends Message
{
    private float avgCost;
 		private String curr;	
 			
    public AvgCostMessage()
    {
    }
    
    public void generate(RestaurantEntry restaurant)
    {
        this.avgCost = restaurant.getAverageCost();
    		this.curr = restaurant.getCurrency();
		}
    
    public void setAverageCost(float avgCost)
    {
        this.avgCost = avgCost;
    }
    
    public float getAverageCost()
    {
        return this.avgCost;
    }

		public void setCurrency(String curr)
		{
				this.curr = curr;
		}

		public String getCurrency()
		{
				return this.curr;
		}
}
