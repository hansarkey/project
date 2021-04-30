package edu.nd.cse.ids.project;

import edu.nd.cse.ids.project.messages.*;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;

import java.io.File;
import java.util.Arrays;
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

public class DocumentPlanner
{
    
		private List<Message> messages;
		/*
		KerasTokenizer tok;
		MultiLayerNetwork model;
		*/
    
		public DocumentPlanner()
    {
				messages = new LinkedList<Message>();
			/*	try{
						tok = KerasTokenizer.fromJson("/escnfs/home/hsarkey/cse40982/exam2prep/qa_tok.json");
						String simpleMlp = "/escnfs/home/hsarkey/cse40982/exam2prep/qa_g_lstm.h5";
						model = KerasModelImport.importKerasSequentialModelAndWeights(simpleMlp);	
				}
				catch (Exception e) {
						e.printStackTrace();
				}
			*/	
		}

    /*
    public static int[] padcrop(Integer[][] seqp, int seqlen)
		{
				
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
		} */
		
		public void createMessages(RestaurantEntry restaurant)
    {
				/*	
				question = question.replaceAll("[^a-zA-Z0-9]", " ");
				question =  question.toLowerCase();
				
				int seqlen = 50;
				String[] questions = new String[1];
				questions[0] = question;		
				
				Integer[][] seq = tok.textsToSequences(questions);
				
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
				*/

				NameMessage m0 = new NameMessage();
				m0.generate(restaurant);
				messages.add(m0);

				AddressMessage m1 = new AddressMessage();
				m1.generate(restaurant);
				messages.add(m1);

				CityMessage m2 = new CityMessage();
				m2.generate(restaurant);
				messages.add(m2);

				CuisineMessage m3 = new CuisineMessage();
				m3.generate(restaurant);
				messages.add(m3);

				RatingMessage m4 = new RatingMessage();
				m4.generate(restaurant);
				messages.add(m4);

				PriceRangeMessage m5 = new PriceRangeMessage();
				m5.generate(restaurant);
				messages.add(m5);

				AvgCostMessage m6 = new AvgCostMessage();
				m6.generate(restaurant);
				messages.add(m6);

				CanBookMessage m7 = new CanBookMessage();
				m7.generate(restaurant);
				messages.add(m7);

				HasDeliveryMessage m8 = new HasDeliveryMessage();
				m8.generate(restaurant);
				messages.add(m8);

				VotesMessage m9 = new VotesMessage();
				m0.generate(restaurant);
				messages.add(m9);

				//System.out.println(messages);
		}
    
    /**
    * Provide access to the list of message objects.
    * Note that the method createMessages() may be called many times for
    * different houses, or it may have been cleared.
    *
    * @return           a list of messages about the house (just one for hw3)
    */
    public List<Message> getMessages()
    {
        return messages;
    }
}
