package edu.nd.cse.ids.project.messages;

import edu.nd.cse.ids.project.*;

public class VotesMessage extends Message
{
    private int votes;
    
    public VotesMessage()
    {
    }
    
    public void generate(RestaurantEntry restaurant)
    {
        this.votes = restaurant.getVotes();
    }
    
    public void setVotes(int votes)
    {
        this.votes = votes;
    }
    
    public float getVotes()
    {
        return this.votes;
    }
}
