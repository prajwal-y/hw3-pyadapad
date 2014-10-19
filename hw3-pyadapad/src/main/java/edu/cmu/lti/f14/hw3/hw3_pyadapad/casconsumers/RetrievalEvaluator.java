package edu.cmu.lti.f14.hw3.hw3_pyadapad.casconsumers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;

import edu.cmu.lti.f14.hw3.hw3_pyadapad.typesystems.Document;
import edu.cmu.lti.f14.hw3.hw3_pyadapad.typesystems.Token;
import edu.cmu.lti.f14.hw3.hw3_pyadapad.utils.Utils;


public class RetrievalEvaluator extends CasConsumer_ImplBase {

	/** query id number **/
	public ArrayList<Integer> qIdList;

	/** query and text relevant values **/
	public ArrayList<Integer> relList;

	public HashMap <Integer, Integer> rankList;
	
	public ArrayList <Double> cosineList;
	
	public ArrayList <String> sentenceList;
	
	//Variables used by processCas
  private static HashMap<String, Integer> queryList = new HashMap<String, Integer>();
  private static HashMap<String, Integer> docList = new HashMap<String, Integer>();
  private static ArrayList<Double> cosineSimilarityValues = new ArrayList<Double>();
  private static double cosineSimilarity = 0.0;
  private static double relCos = 0.0;
  private static int rank = 1;
		
	public void initialize() throws ResourceInitializationException {
		qIdList = new ArrayList<Integer>();
		relList = new ArrayList<Integer>();
		rankList = new HashMap<Integer, Integer>();
		cosineList = new ArrayList<Double>();
		sentenceList = new ArrayList<String>();
	}

	private int computeRank() {
	  rank = 1;
	  if(cosineSimilarityValues != null) {
      for(double values : cosineSimilarityValues) {
        if(relCos < values)
          rank++;
      }
    }
	  return rank;
	}
	
	/**
	 * TODO :: 1. construct the global word dictionary 2. keep the word
	 * frequency for each sentence
	 */
	@Override
	public void processCas(CAS aCas) throws ResourceProcessException {

		JCas jcas;
		try {
			jcas =aCas.getJCas();
		} catch (CASException e) {
			throw new ResourceProcessException(e);
		}

		FSIterator<?> it = jcas.getAnnotationIndex(Document.type).iterator();
		if (it.hasNext()) {
			Document doc = (Document) it.next();
			//System.out.println("R: " + doc.getText());
			
			//Make sure that your previous annotators have populated this in CAS
			FSList fsTokenList = doc.getTokenList();
			ArrayList<Token> tokenList = Utils.fromFSListToCollection(fsTokenList, Token.class);
			if(doc.getRelevanceValue() == 99) {
			  cosineSimilarityValues.clear();
			  queryList.clear();
			  docList.clear();
			  relCos = -1.0;
			  cosineSimilarity = 0.0;
			  rank = 1;
			  for(Token token : tokenList) {
	        queryList.put(token.getText(), token.getFrequency());
	      }
			}
			else {
			  docList = new HashMap<String, Integer>();
			  for(Token token : tokenList) {
          docList.put(token.getText(), token.getFrequency());
        }
			  cosineSimilarity = computeCosineSimilarity(queryList, docList);
			  //System.out.println(cosineSimilarity);
			}
			
			if(doc.getRelevanceValue() == 1) {
			  qIdList.add(doc.getQueryID());
			  relList.add(doc.getRelevanceValue());
			  cosineList.add(cosineSimilarity);
			  sentenceList.add(doc.getText());
			  relCos = cosineSimilarity;
			  rankList.put(doc.getQueryID(), computeRank());
			  //System.out.println("Rank: " + rank);
			}
			else if (relCos == -1.0 && doc.getRelevanceValue() != 99)
		     cosineSimilarityValues.add(cosineSimilarity);
			else {
			   //System.out.println("relCos: " + relCos);
			   if(relCos < cosineSimilarity)
			     rankList.put(doc.getQueryID(), ++rank);
			   //System.out.println("Rank: " + rank);
			}
			
		}
	}

	/**
	 * TODO 1. Compute Cosine Similarity and rank the retrieved sentences 2.
	 * Compute the MRR metric
	 */
	@Override
	public void collectionProcessComplete(ProcessTrace arg0)
			throws ResourceProcessException, IOException {

		super.collectionProcessComplete(arg0);

		// TODO :: compute the cosine similarity measure
    for(int i = 0; i < qIdList.size(); i++) {
      System.out.println("cosine=" + (double)Math.round(cosineList.get(i) * 10000)/10000 + "\trank=" + rankList.get(qIdList.get(i)) + "\tqid=" + qIdList.get(i) + "\trel=" + relList.get(i) + "\t" + sentenceList.get(i));
    }
		
		
		// TODO :: compute the rank of retrieved sentences
		
		
		
		// TODO :: compute the metric:: mean reciprocal rank
		double metric_mrr = compute_mrr();
		System.out.println(" (MRR) Mean Reciprocal Rank :: " + metric_mrr);
	}

	/**
	 * 
	 * @return cosine_similarity
	 */
	private double computeCosineSimilarity(Map<String, Integer> queryVector,
		Map<String, Integer> docVector) {
		double cosine_similarity=0.0;
		double dot_product = 0.0;
		double queryVectorVal = 0.0;
		double docVectorVal = 0.0;
		for(String key : queryVector.keySet()) {
		  queryVectorVal += (queryVector.get(key) * queryVector.get(key));
		  if(docVector.containsKey(key))
		    dot_product += queryVector.get(key) * docVector.get(key);
		}
		for(String key : docVector.keySet()) {
		  docVectorVal += (docVector.get(key) * docVector.get(key));
		}
		cosine_similarity = dot_product / (Math.sqrt(queryVectorVal) * Math.sqrt(docVectorVal));

		return cosine_similarity;
	}

	/**
	 * 
	 * @return mrr
	 */
	private double compute_mrr() {
		double metric_mrr=0.0;
		double value = 0.0;
		for(int i = 0; i < qIdList.size(); i++) {
		  value = value + (1.0 / rankList.get(qIdList.get(i)));
		}	
		metric_mrr = value * (1.0 / qIdList.size());
		return (double)Math.round(metric_mrr * 10000)/10000;
	}

}
