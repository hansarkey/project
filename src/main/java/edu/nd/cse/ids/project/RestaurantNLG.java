package edu.nd.cse.ids.project;

import edu.nd.cse.ids.project.messages.*;

import java.util.*;
import java.util.List;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Map.Entry;
import java.lang.Object;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.lang.String;
import java.io.FileNotFoundException;

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
		

		public static double cosineSimilarity(String[] tkn0, String[] tkn1) {
					HashMap<String, int[]> map = new HashMap<String, int[]>();
					for (int i = 0; i < tkn0.length; i++) {
								String t = tkn0[i].toLowerCase();
								if (!map.containsKey(t)) {
											map.put(t, new int[2]);
								}
								map.get(t)[0]++;
					}
					for (int i = 0; i < tkn1.length; i++) {
								String t = tkn1[i].toLowerCase();
								if (!map.containsKey(t)) {
											map.put(t, new int[2]);
								}
								map.get(t)[1]++;
					}
					double dot = 0;
					double norma = 0;
					double normb = 0;
					for (Entry<String, int[]> e : map.entrySet()) {
								int[] v = e.getValue();
								dot += v[0] * v[1];
								norma += v[0] * v[0];
								normb += v[1] * v[1];
					}
					norma = Math.sqrt(norma);
					normb = Math.sqrt(normb);
					if (dot == 0) {
								return 0;
					} else {
								return dot / (norma * normb);
					}
		}

    public static void main(String[] args)
    {
				
				Scanner scanner =  new Scanner(System.in);
				String[] userprofile = new String[6];

				System.out.println("Hi there, where are you located today?");
				String city = scanner.next();
				userprofile[0] = city;

				System.out.println("What type of cuisine would you like?");
				String cuisine = scanner.next();
				userprofile[1] = cuisine;

				System.out.println("How much would you like to spend per person?");
				float avgprice = scanner.nextFloat();
				avgprice = (avgprice*2);
				String price = String.valueOf(avgprice);
				userprofile[2] = price;

				System.out.println("Would you like delivery?");
				String delivery = scanner.next();
				userprofile[3] = delivery;

				System.out.println("Do you want to book in advance?");
				String booking = scanner.next();
				userprofile[4] = booking;

				System.out.println("On a scale of 1.0 to 5.0, what is the minimum rating you would like your restaurant to have?");
				float minrating = scanner.nextFloat();
				String rating = String.valueOf(minrating);
				userprofile[5] = rating; 

				System.out.println(Arrays.toString(userprofile));
				
				double cosinesim = 0.0; 
				double maxsim = 0.0;
				int restaurantID = 0;

				//read in restaurant vectors and calculate similarity 	
				try { 	
						Scanner sc = new Scanner(new BufferedReader(new FileReader("/escnfs/home/hsarkey/cse40982/project/data/zomato_vectors2.txt")));
						int rows = 9550;
						int columns = 6;
			 			System.out.println("after scanner");
						String[][] restaurantVectors = new String[rows][columns];
						while (sc.hasNextLine()) {
								for (int i=0; i < restaurantVectors.length; i++) {
											if (sc.hasNextLine()) {
													String[] line = sc.nextLine().trim().split("--");
													for (int j=0; j < line.length; j++) {
														restaurantVectors[i][j] = line[j];
													}
											}
											System.out.println(Arrays.toString(restaurantVectors[i]));
											cosinesim  = cosineSimilarity(userprofile, restaurantVectors[i]);
											if (cosinesim > maxsim) {
													maxsim = cosinesim;
													restaurantID = i;		
											}
											
								}
								System.out.println(restaurantID);
						}	
				}
				catch(FileNotFoundException ex)
				{
						System.out.println("An error occurred.");
						System.exit(2);
				}
			
				RestaurantNLG restaurantNlg = new RestaurantNLG("/escnfs/home/hsarkey/cse40982/project/data/zomato_updated.csv");	
			
				System.out.println("How about this restaurant?"); 

				List<String> describedRestaurant = restaurantNlg.realizeRestaurantById(restaurantID);

				for(String Sentence: describedRestaurant)
				{
						System.out.print(Sentence);
				}
		}
}
