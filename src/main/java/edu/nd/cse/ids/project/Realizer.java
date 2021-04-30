package edu.nd.cse.ids.project;

import java.util.List;
import java.util.LinkedList;

import simplenlg.framework.*;
import simplenlg.lexicon.*;
import simplenlg.realiser.english.*;
import simplenlg.phrasespec.*;
import simplenlg.features.*;

public class Realizer
{
    private Lexicon lexicon;
    private Realiser realizer;
    
    public Realizer()
    {
        lexicon = Lexicon.getDefaultLexicon();
        realizer = new Realiser(lexicon);
    }
    
    /**
    * Generate surface text for each sentence specification.
    * The input is the text specification (just a list of sentence
    * specifications).  The output is a list of the actual rendered text for
    * each sentence specification (which we call the surface text).
    * @param    sentences   the text specification
    * @return               the surface text
    */
    public List<String> realize(List<SPhraseSpec> sentences)
    {
				List<String> sents;
				String output;
			 	sents = new LinkedList<String>();	
				for (SPhraseSpec sent : sentences) {
								output = realizer.realiseSentence(sent);
								sents.add(output);
//								System.out.println(output);
				}
				return sents;
    }
}
