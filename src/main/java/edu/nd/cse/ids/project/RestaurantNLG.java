package edu.nd.cse.ids.project;

import edu.nd.cse.ids.project.messages.*;

import java.util.*;
import java.util.List;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Map.Entry;
import java.lang.Object;
import java.io.*;
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
							tok = KerasTokenizer.fromJson("data/rate_tok.json");
							String simpleMlp = "data/rate_model.h5";
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

    public static float[] getEmbed(int index, String fileName)
    {
        float[] embed = new float[100];
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String strEmbedding = new String();
            int newIndex = index * 4;
            for (int i = 0; i <= newIndex; i++) {
                String[] line = br.readLine().trim().split(":");
                if (i == newIndex) {
                    strEmbedding = line[0];
                    break;
                }
            }

            String[] stringArray = strEmbedding.split(" ");
            for (int i = 0; i < stringArray.length; i++) {
                embed[i] = Float.parseFloat(stringArray[i]);
            }

        }
	catch(FileNotFoundException ex)
	{
	    System.out.println("An error occurred while retrieving the embeddings.");
	    System.exit(2);
	}
        catch(IOException io)
        {
            System.out.println("IOException occured while retrieving the embeddings.");
            System.exit(3);
        }
        return embed;
    }
    
    public static int getRid(int id)
    {
        int rid = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader("data/zomato_updated.csv"));
            String strEmbedding = new String();
            String line = br.readLine();
            while ((line = br.readLine()) != null)
            {
                try {
                    String[] rest = line.trim().split(",");
                    if (Integer.parseInt(rest[0]) == id) {
                        break;
                    }
                rid++;
                } catch (NumberFormatException e){
                    continue;
                }
            }
        }
	catch(FileNotFoundException ex)
	{
	    System.out.println("An error occurred while retrieving the rids.");
	    System.exit(2);
	}
        catch(IOException io)
        {
            System.out.println("IOException occured while retrieving the rids.");
            System.exit(3);
        }
        return rid;
    }

    public static float[] getCity(String response) 
    {
        KerasTokenizer tok = null;
        MultiLayerNetwork model = null;
        
        try {
            tok = KerasTokenizer.fromJson("data/cities.json");
            String simpleMlp = "data/cities.h5";
            model = KerasModelImport.importKerasSequentialModelAndWeights(simpleMlp);
        } catch (Exception e) {
            e.printStackTrace();
        } 
                
        response = response.replaceAll("[^a-zA-Z0-9]", " ");
        response =  response.toLowerCase();
        int seqlen = 200;
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

        float[] cityEmbedding = getEmbed(decision, "data/city_samples.txt");

        return cityEmbedding;

    }

    public static float[] getCuisine(String response) 
    {
        KerasTokenizer tok = null;
        MultiLayerNetwork model = null;
        
        try {
            tok = KerasTokenizer.fromJson("data/cuisine_tok.json");
            String simpleMlp = "data/cuisines.h5";
            model = KerasModelImport.importKerasSequentialModelAndWeights(simpleMlp);
        } catch (Exception e) {
            e.printStackTrace();
        } 
                
        response = response.replaceAll("[^a-zA-Z0-9]", " ");
        response =  response.toLowerCase();
        int seqlen = 200;
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

        float[] cuisineEmbedding = getEmbed(decision, "data/cuisine_samples.txt");

        return cuisineEmbedding;

    }

    public static float cosDistance(float[] v1, float[] v2)
    {
        float dotProduct = dotProduct(v1, v2);
        float sumNorm = vectorNorm(v1) * vectorNorm(v2);
        return dotProduct/sumNorm;
    }
    
    public static float dotProduct(float[] v1, float[] v2) {
        float result = 0;
        for (int i = 0; i < v1.length; i++) {
            result += v1[i] * v2[i];
        }
        return result;
    }
    
    public static float vectorNorm(float[] v) {
        float result = 0;
        for (float aV:v) {
            result += aV * aV;
        }
        result = (float) Math.sqrt(result);
        return result;
    }

    public static void main(String[] args)
    {
				
				Scanner scanner =  new Scanner(System.in);
                                float[] userTotal = new float[104];
				String[] userprofile = new String[6];
                                float maxPrice = 800000;

				System.out.println("Hi there, where are you located today?");
				String city = scanner.nextLine();

				System.out.println("What type of cuisine would you like?");
				String cuisine = scanner.nextLine();
				userprofile[1] = cuisine;


				System.out.println("How much would you like to spend per person?");
				float avgprice = scanner.nextFloat();
				
                                avgprice = (avgprice*2);
				String price = String.valueOf(avgprice);
				userTotal[100] = avgprice/800000;

				System.out.println("Would you like delivery? Yes or No");
				String delivery = scanner.next();
                                if(delivery.toLowerCase() == "yes") {
				    userTotal[101] = (float)1;
                                } else {
				    userTotal[101] = (float)0;
                                }

				System.out.println("Do you want to book in advance?");
				String booking = scanner.next();
                                if(booking.toLowerCase() == "yes") {
				    userTotal[102] = (float)1;
                                } else {
				    userTotal[102] = (float)0;
                                }

				System.out.println("On a scale of 1.0 to 5.0, what is the minimum rating you would like your restaurant to have?");
				float minrating = scanner.nextFloat();
                                userTotal[103] = minrating/5;

				
                                float[] cityEmbedding = getCity(city);
                                float[] cuisineEmbedding = getCuisine(cuisine);
                                for(int i = 0; i < cuisineEmbedding.length; i++) {
                                    userTotal[i] = cuisineEmbedding[i];
                                }
                                double totalSim = 0.0;
				double citySim = 0.0; 
				double cuisineSim = 0.0; 
                                double otherSim = 0.0;
				double maxsim = 0.0;
				int rid = 0;
                                int restaurantID = -1;

                                float[] totalVector = new float[104];
                                float[] otherVector = new float[4];
                                float[] userOther = new float[4];
                                double[] cosList = new double[5445];   // only considering city, and new delhi has most with 5445 cities
                                int[] idList = new int[5445];       

				//read in restaurant vectors and calculate similarity 	
				try { 	
						Scanner sc = new Scanner(new BufferedReader(new FileReader("data/zomato_vectors3.txt")));
                                                int i = 0;
                                                String[] restaurantVectors = new String[9];
						while (sc.hasNextLine()) {
					    	    if (sc.hasNextLine()) {
							String[] line = sc.nextLine().trim().split(",");
                                                        
                                                        float[] v1 = new float[100];
                                                        String[] nextNum = line[1].split(" ");
                                                        for (int j = 0; j < nextNum.length; j++) {
                                                            v1[j] = Float.parseFloat(nextNum[j]);
                                                        }
                                                        
                                                        citySim = cosDistance(v1, cityEmbedding);
                                                        if (Float.parseFloat(line[6]) < userTotal[103]) {
                                                            continue;
                                                        }

                                                        if (citySim > 0.950) {
                                                            nextNum = line[2].split(" ");
                                                            for (int j = 0; j < nextNum.length; j++) {
                                                                totalVector[j] = Float.parseFloat(nextNum[j]);
                                                            }
                                                            totalVector[100] = Float.parseFloat(line[3]);
                                                            totalVector[101] = Float.parseFloat(line[4]);
                                                            totalVector[102] = Float.parseFloat(line[5]);
                                                            totalVector[103] = Float.parseFloat(line[6]);
                                                            
                                                            totalSim = cosDistance(totalVector, userTotal);
                                                            cosList[i] = 2*citySim + totalSim;
                                                            idList[i] = Integer.parseInt(line[0]);
                                                            if (cosList[i] > maxsim) {
                                                                maxsim = cosList[i];
                                                                restaurantID = idList[i];
                                                            }
                                                            i++;
                                                        }
						    }
						}

                                                // Get the restaurant ID from file

                                                
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
			
				int decision = -1;
				String userresponse = " ";

                                // weird bug where its asking about restaurant twice. 
                                scanner.nextLine();
				
                                while (decision	!= 2) {
                                    maxsim = 0;
                                    int maxIndex = 0;
                                    for(int i = 0; i < idList.length; i++) {
                                        if (cosList[i] > maxsim) {
                                            maxIndex = i;
                                            maxsim = cosList[i];
                                            restaurantID = idList[i];
                                        }
                                    }
                                    if (maxsim == 0) {
                                        System.out.println("\nThere are sadly no more suggestions for restaurants in your area.");
                                        System.out.println("Please restart if you would like to see your options again.");
                                        System.exit(0);
                                    }
                                    cosList[maxIndex] = 0;
			            rid = getRid(restaurantID);
				    System.out.println("Ok, how about this one?");
				    List<String> describedRestaurant = restaurantNlg.realizeRestaurantById(rid);
				    System.out.println(describedRestaurant);
				    userresponse = scanner.nextLine();
				    decision = interpretResponse(userresponse);
				}
				System.out.println("Enjoy your meal.");			
		}
}
