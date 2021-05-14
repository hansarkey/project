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
import org.deeplearning4j.nn.modelimport.keras.preprocessing.text.KerasTokenizer;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.LossLayer;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class RestaurantNLG
{
    private RestaurantEntryReader reader;
 	//	KerasTokenizer tok;
	//	MultiLayerNetwork model;
		private MicroPlanner mp;

    public RestaurantNLG(String datfile)
    {
        this.reader = new RestaurantEntryReader();
        this.reader.readRestaurantEntryFile(datfile);
/*		
				try { 
						tok = KerasTokenizer.fromJson("/escnfs/home/hsarkey/cse40982/project/rate_tok.json");
						String simpleMlp = "/escnfs/home/hsarkey/cse40982/project/rate_model.h5";
						model = KerasModelImport.importKerasSequentialModelAndWeights(simpleMlp);
				} catch (Exception e) {
						e.printStackTrace();
				} */
		
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

		public static int[] padcrop(Integer[][] seqp, int seqlen) {
				int[] newseq = new int[seqlen];
				List <Integer> seqlist = new ArrayList<Integer>(Arrays.asList(seqp[0]));
			
				int qlen = (seqp[0]).length;
				if (qlen > seqlen) {
						while (qlen > seqlen) {
								seqlist.remove(qlen-1);
								qlen = seqlist.size();
						}
				}
				else {
						while (qlen < seqlen) {
								seqlist.add(0);
								qlen = seqlist.size();
						}
				}
				seqp[0] = seqlist.toArray(seqp[0]);
			
				for (int i =0; i < (seqp[0]).length; i++) {
						newseq[i] = (seqp[0][i]);
				}
				return newseq;
		}
		
		public static int interpretResponse(String response) {
					KerasTokenizer tok = null;
					MultiLayerNetwork model = null;
					
					try {
							tok = KerasTokenizer.fromJson("/escnfs/home/hsarkey/cse40982/project/rate_tok.json");
							String simpleMlp = "/escnfs/home/hsarkey/cse40982/project/rate_model.h5";
							model = KerasModelImport.importKerasSequentialModelAndWeights(simpleMlp);
					} catch (Exception e) {
							e.printStackTrace();
					} 
						
					response = response.replaceAll("[^a-zA-Z0-9]", " ");
					response =  response.toLowerCase();
					int seqlen = 50;
					String[] responses = new String[1];
					responses[0] = response;
					Integer[][] seq = tok.textsToSequences(responses);
						
					int newseq[] = padcrop(seq,seqlen);
					INDArray input = Nd4j.create(1, seqlen);
					for(int i=0; i<seqlen; i++)
					{   
							input.putScalar(new int[] {i}, newseq[i]);
					}
					INDArray output = model.output(input);
					double[] array = output.toDoubleVector();
					double max = 0.0;
					int decision = 0;
					for (int i =0; i<array.length; i++) {
							if (array[i] > max) {
									max = array[i];
									decision = i;
							}
					}
					return decision;
		}
		

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
				String city = scanner.nextLine();
				userprofile[0] = city;

				System.out.println("What type of cuisine would you like?");
				String cuisine = scanner.nextLine();
				userprofile[1] = cuisine;

				System.out.println("How much would you like to spend per person?");
				float avgprice = scanner.nextFloat();
		//		avgprice = avgprice.replaceAll("[^0-9]+", " ");
		//		System.out.println("after replaced");
		//		System.out.println(avgprice);
		//		float avgp = Float.parseFloat(avgprice); 
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
				int restaurantID = -1;

				//read in restaurant vectors and calculate similarity 	
				try { 	
						Scanner sc = new Scanner(new BufferedReader(new FileReader("data/zomato_vectors2.txt")));
						int rows = 9550;
						int columns = 6;
			 			//System.out.println("after scanner");
						String[][] restaurantVectors = new String[rows][columns];
						while (sc.hasNextLine()) {
								for (int i=0; i < restaurantVectors.length; i++) {
                                                                                        double citySim = 0.0;
											if (sc.hasNextLine()) {
													String[] line = sc.nextLine().trim().split("--");
                                                                                                        String[] v1 = new String[1];
                                                                                                        String[] v2 = new String[1];
                                                                                                        v1[0] = line[0];
                                                                                                        v2[0] = userprofile[0];
                                                                                                        citySim = cosineSimilarity(v1, v2);
                                                                                                        if (Double.parseDouble(line[5]) < Double.parseDouble(userprofile[5])) {
                                                                                                            continue;
                                                                                                        }
                                                                                                        if (citySim > 0.500) {
													    for (int j=0; j < line.length; j++) {
													    	restaurantVectors[i][j] = line[j];
													    }
                                                                                                        }
											}
                                                                                        if (citySim > 0.500) {
                                                                                                        System.out.println(Arrays.toString(restaurantVectors[i]));
                                                                                                        cosinesim  = cosineSimilarity(userprofile, restaurantVectors[i]);
                                                                                                        if (cosinesim > maxsim) {
                                                                                                            maxsim = cosinesim;
                                                                                                            restaurantID = i;		
                                                                                                        }
                                                                                        }
											
								}
								//System.out.println(restaurantID);
						}
                                                if (restaurantID == -1) {
                                                    System.out.println("Could not find any restaurants meeting this criteria.\nTry picking a new city or lowering your rating\n");
                                                    System.exit(3);
                                                }
				}
				catch(FileNotFoundException ex)
				{
						System.out.println("An error occurred.");
						System.exit(2);
				}
			
				RestaurantNLG restaurantNlg = new RestaurantNLG("data/zomato_updated.csv");	
			
				//System.out.println("How about this restaurant?"); 

				//List<String> describedRestaurant = restaurantNlg.realizeRestaurantById(restaurantID);
				//System.out.println(describedRestaurant);
			 		
				//String userresponse = scanner.nextLine();
				int decision = -1;
				String userresponse = " ";
				//interpretResponse(userresponse);
				while (decision	!= 2) {
						
						System.out.println("Ok, how about this one?");
						List<String> describedRestaurant = restaurantNlg.realizeRestaurantById(restaurantID);
						System.out.println(describedRestaurant);
						userresponse = scanner.nextLine();
						decision = interpretResponse(userresponse);
				}
				System.out.println("Enjoy your meal.");			
				/*
				while (!(describedRestaurant.get(0)).equals("Enjoy your meal.")) {
								describedRestaurant = restaurantNlg.realizeRestaurantById(restu
				*/ /*
				for(String Sentence: describedRestaurant)
				{
						System.out.print(Sentence);
				} */
		}
}
