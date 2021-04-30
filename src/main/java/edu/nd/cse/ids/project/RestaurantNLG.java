package edu.nd.cse.ids.project;

import edu.nd.cse.ids.project.messages.*;

import java.util.List;
import java.util.LinkedList;
import java.util.Scanner;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;

import simplenlg.framework.*;
import simplenlg.lexicon.*;
import simplenlg.realiser.english.*;
import simplenlg.phrasespec.*;
import simplenlg.features.*;

public class RestaurantNLG
{
    private RestaurantEntryReader reader;
 		private MicroPlanner mp;

    public RestaurantNLG(String datfile)
    {
        this.reader = new RestaurantEntryReader();
        this.reader.readRestaurantEntryFile(datfile);
		}

    public List<String> realizeRestaurantById(int id)
		{
				try
				{
						RestaurantEntry restaurant = this.reader.getRestaurants().get(id);
						this.mp = new MicroPlanner();
						return(this.describeRestaurant(restaurant));
				} catch(Exception ex)
				{		
						return(null);
				}
		}
		
		
		public List<String> describeRestaurantById(int id)
    {
        try
        {
            RestaurantEntry restaurant = this.reader.getRestaurants().get(id);
            return(this.describeRestaurant(restaurant));
        } catch(Exception ex)
        {
            return(null);
        }
    }
   
	 
    public List<String> describeRestaurant(RestaurantEntry restaurant)
    {
				DocumentPlanner docplanner = new DocumentPlanner();
  		  docplanner.createMessages(restaurant);
        List<Message> documentPlan = docplanner.getMessages();
        
				List<SPhraseSpec> sentences;
				sentences = mp.lexicalize(documentPlan);

        Realizer realizer = new Realizer();
        return(realizer.realize(sentences));
    }

    public List<List<String>> describeAllRestaurants()
    {
        List<List<String>> allSentences = new LinkedList<List<String>>();
    
        for(RestaurantEntry restaurant: this.reader.getRestaurants())
        {
            allSentences.add(describeRestaurant(restaurant));
        }
        
        return(allSentences);
    }
/*
		public List<String> askQuestion(String question, int planetid) 
		{
				PlanetEntry planet = this.reader.getPlanets().get(planetid);
				
				DocumentPlanner docplanner = new DocumentPlanner();
				docplanner.createMessages(question, planet);
			
				List<Message> documentPlan = docplanner.getMessages();
				//System.out.println("inside ask question");	

				List<SPhraseSpec> sentences;
				this.mp = new MicroPlanner();
				sentences = mp.lexicalize(documentPlan);
				//System.out.println("lexicalized");				 
				Realizer realizer = new Realizer();
				return(realizer.realize(sentences));
		} */

    public static void main(String[] args)
    {

				RestaurantNLG restaurantNlg = new RestaurantNLG("/escnfs/home/hsarkey/cse40982/project/data/zomato_updated.csv");	
				
				List<String> describedRestaurant = restaurantNlg.realizeRestaurantById(6);
				System.out.println(describedRestaurant);

				for(String Sentence: describedRestaurant)
				{
						System.out.println(Sentence);
				}
		}
}
