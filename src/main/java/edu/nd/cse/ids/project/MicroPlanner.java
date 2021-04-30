package edu.nd.cse.ids.project;

import edu.nd.cse.ids.project.messages.*;

import java.util.List;
import java.util.LinkedList;

import simplenlg.framework.*;
import simplenlg.lexicon.*;
import simplenlg.realiser.english.*;
import simplenlg.phrasespec.*;
import simplenlg.features.*;

public class MicroPlanner
{
    private Lexicon lexicon;
    private NLGFactory nlgFactory;
 		public int count = 0;   
    
		/**
    * Constructor for MicroPlanner.
    * Initialize the lexicon and nlgFactory for this object.  Do not recreate
    * a new lexicon/nlgFactory every time you call lexicalize().  Instead,
    * create it once here for the whole object and reuse it.
    */
    public MicroPlanner()
    {
        this.lexicon = Lexicon.getDefaultLexicon();
				this.nlgFactory = new NLGFactory(this.lexicon);
    }

    /**
    * Create a the text specification from the document plan.
    * Recall that a text specification is just a list of sentence
    * specifications, and that a document plan is just a list of messages.
    * This method should go through each message and call the
    * correct handleMessage() function for that message.
    *
    * @param    documentPlan    the list of messages to process
    * @return                   the text specification
    */
    public List<SPhraseSpec> lexicalize(List<Message> documentPlan)
    {
				SPhraseSpec s1;
				List<SPhraseSpec> text_spec;
				//List<SPhraseSpec> ag_text_spec;
				//List<SPhraseSpec> re_text_spec;
				text_spec = new LinkedList<SPhraseSpec>();
				//ag_text_spec = new LinkedList<SPhraseSpec>();
				//re_text_spec = new LinkedList<SPhraseSpec>();

				for (Message m: documentPlan) {
						if (m instanceof NameMessage) {
								s1 = handleMessage((NameMessage)m);
								text_spec.add(s1);
						}
						else if (m instanceof AddressMessage) {
								s1 = handleMessage((AddressMessage)m);
								text_spec.add(s1);
						} 
						else if (m instanceof CityMessage) {
								s1 = handleMessage((CityMessage)m);
								text_spec.add(s1);
						}
						else if (m instanceof CuisineMessage) {
								s1 = handleMessage((CuisineMessage)m);
								text_spec.add(s1);
						}
						else if (m instanceof RatingMessage) {
								s1 = handleMessage((RatingMessage)m);
								text_spec.add(s1);
						}
						else if (m instanceof PriceRangeMessage) {
								s1 = handleMessage((PriceRangeMessage)m);
								text_spec.add(s1);
						}
						else if (m instanceof AvgCostMessage) {
								s1 = handleMessage((AvgCostMessage)m);
								text_spec.add(s1);
						}
						else if (m instanceof CanBookMessage) {
								s1 = handleMessage((CanBookMessage)m);
								text_spec.add(s1);
						}
						else if (m instanceof HasDeliveryMessage) {
								s1 = handleMessage((HasDeliveryMessage)m);
								text_spec.add(s1);
						}
						else if (m instanceof VotesMessage) {
								s1 = handleMessage((VotesMessage)m);
								text_spec.add(s1);
						}
				}
				return text_spec;
    }

    /**
    * Creates a single sentence specification for each message.
    */
    public SPhraseSpec handleMessage(NameMessage message)
    {
				SPhraseSpec s1 = nlgFactory.createClause();
     		String name  = message.getRestaurantName();
				//s1.setFeature(Feature.TENSE, Tense.PAST);

				s1.setSubject("My recommendation");
				s1.setVerb("am");
				s1.setObject(name);
				
				//PPPhraseSpec pp = nlgFactory.createPrepositionPhrase("in", String.valueOf(year));
				//s1.setObject(pp);

        return s1;
    }

		public SPhraseSpec handleMessage(AddressMessage message)
		{
				SPhraseSpec s1 = nlgFactory.createClause();
				String addr = message.getAddress();
				s1.setFeature(Feature.TENSE, Tense.PAST);
				s1.setSubject("it is");
				s1.setVerb("locate");
				PPPhraseSpec pp = nlgFactory.createPrepositionPhrase("at", addr);
				s1.setObject(pp);

				return s1;
		}

		public SPhraseSpec handleMessage(CityMessage message) 
		{

				SPhraseSpec s1 = nlgFactory.createClause();

				String city = message.getCity();
				
				s1.setSubject("you");
				s1.setVerb("can find");
				s1.setIndirectObject("it");
				PPPhraseSpec pp = nlgFactory.createPrepositionPhrase("in", city);
				s1.setObject(pp);
				return s1;
		}

		public SPhraseSpec handleMessage(CuisineMessage message) 
		{
						
				SPhraseSpec s1 = nlgFactory.createClause();
				s1.setSubject("it");
				s1.setVerb("serve");
				String cuisine = message.getCuisine();
				NPPhraseSpec food = nlgFactory.createNounPhrase("food");
				food.addPreModifier(cuisine);
				s1.setObject(food);

				return s1;
		}

		public SPhraseSpec handleMessage(AvgCostMessage message)
		{
				SPhraseSpec s1 = nlgFactory.createClause();
			         
				float ac = message.getAverageCost();
				String curr = message.getCurrency();				
				NPPhraseSpec cost = nlgFactory.createNounPhrase("cost");
				cost.addPreModifier("the average");
				PPPhraseSpec pp = nlgFactory.createPrepositionPhrase("for", "two");
				cost.addPostModifier(pp);
				s1.setSubject(cost);
				s1.setVerb("is");
				NPPhraseSpec costcurr = nlgFactory.createNounPhrase(curr);
				costcurr.addPreModifier(String.valueOf(ac));
				s1.setObject(costcurr);
				return s1;
		}

		public SPhraseSpec handleMessage(PriceRangeMessage message)
		{
				SPhraseSpec s1 = nlgFactory.createClause();
				
				int pr = message.getPriceRange();
				NPPhraseSpec range = nlgFactory.createNounPhrase("range");
				range.addPreModifier("the price");
				
				s1.setSubject(range);

				s1.setVerb("is");
				NPPhraseSpec apr = nlgFactory.createNounPhrase(String.valueOf(pr));
				apr.setDeterminer("a");
				s1.setIndirectObject(apr);
				PPPhraseSpec pp = nlgFactory.createPrepositionPhrase("on", "a scale");
				PPPhraseSpec pp2 = nlgFactory.createPrepositionPhrase("from", "1 to 4");
				pp2.addPreModifier(pp);
				s1.setObject(pp2);
				
				return s1;
		}
		
		public SPhraseSpec handleMessage(RatingMessage message) 
		{
				SPhraseSpec s1 = nlgFactory.createClause();
				 
				float  r = message.getRating();

		    s1.setSubject("it");

				s1.setVerb("rates");
				NPPhraseSpec ar = nlgFactory.createNounPhrase(String.valueOf(r));
				ar.setDeterminer("a");
				s1.setIndirectObject(ar);
				 
				PPPhraseSpec pp = nlgFactory.createPrepositionPhrase("on", "a scale");
				PPPhraseSpec pp2 = nlgFactory.createPrepositionPhrase("from", "1 to 5");
				pp2.addPreModifier(pp);
				s1.setObject(pp2);

				return s1;
						
		}	

		public SPhraseSpec handleMessage(CanBookMessage message)
		{
				SPhraseSpec s1 = nlgFactory.createClause();
				String cb = message.getCanBook();
				s1.setSubject("you");
				if (cb.equals("No")) {
								s1.setVerb("can not book");
				} else {
								s1.setVerb("can book");
				}

				NPPhraseSpec table = nlgFactory.createNounPhrase("table");
				table.setDeterminer("a");
				PPPhraseSpec pp = nlgFactory.createPrepositionPhrase("in", "advance");
				table.addPostModifier(pp);
				s1.setObject(table);
								
				return s1;	
		} 

		public SPhraseSpec handleMessage(VotesMessage message)
		{
				SPhraseSpec s1 = nlgFactory.createClause();
				
				float votes = message.getVotes();
				
				s1.setSubject("it");
				s1.setVerb("has");
				
				NPPhraseSpec at = nlgFactory.createNounPhrase("total");
				at.setDeterminer("a");
				s1.setIndirectObject(at);

				PPPhraseSpec pp = nlgFactory.createPrepositionPhrase("of", String.valueOf(votes));
				pp.addPostModifier("votes");
				PPPhraseSpec pp2 = nlgFactory.createPrepositionPhrase("on", "zomato.com");
				pp2.addPreModifier(pp);
				s1.setObject(pp2);
				return s1;
		}
		
		public SPhraseSpec handleMessage(HasDeliveryMessage message)
		{
				SPhraseSpec s1 = nlgFactory.createClause();
				String hd = message.getOnlineDelivery();
				s1.setSubject("you");
				if (hd.equals("No")) {
						s1.setVerb("can not order");
				} else {
						s1.setVerb("can order");
				}
				
				s1.setObject("delivery");
				return s1;
		}

}
