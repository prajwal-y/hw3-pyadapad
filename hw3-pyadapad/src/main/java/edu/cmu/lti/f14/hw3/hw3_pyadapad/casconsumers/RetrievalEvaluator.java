package edu.cmu.lti.f14.hw3.hw3_pyadapad.casconsumers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
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

/**
 * This class is responsible for 
 * @author pyadapad
 *
 */
public class RetrievalEvaluator extends CasConsumer_ImplBase {

  public ArrayList<Integer> qIdList; //Query ID List
  public ArrayList<Integer> relList; //The relevant query List
  public HashMap<Integer, Integer> rankList; //The Query rank list
  public ArrayList<Double> similarityList; //The Similarity values list
  public ArrayList<String> sentenceList; //The query text list

  // Variables used by processCas
  private static HashMap<String, Integer> queryList = new HashMap<String, Integer>();
  private static HashMap<String, Integer> docList = new HashMap<String, Integer>();
  private static ArrayList<Double> similarityValues = new ArrayList<Double>();
  private static double cosineSimilarity = 0.0;
  /** Implementations of dice coefficient and Jaccard distance are commented out**/
  // private static double diceSimilarity = 0.0;
  // private static double jaccardDistance = 0.0;
  private static double relCos = 0.0;

  private static int rank = 1;

  private static File outFile;

  private static PrintWriter writer = null;

  public void initialize() throws ResourceInitializationException {
    qIdList = new ArrayList<Integer>();
    relList = new ArrayList<Integer>();
    rankList = new HashMap<Integer, Integer>();
    similarityList = new ArrayList<Double>();
    sentenceList = new ArrayList<String>();
    try {
      outFile = new File(((String) getConfigParameterValue("output_file")).trim());
      if (!outFile.exists())
        outFile.createNewFile();
      writer = new PrintWriter(new BufferedWriter(new FileWriter(outFile, false)));
    } catch (FileNotFoundException e) {
      System.out.println("FileNotFoundException occurred: " + e.getMessage());
    } catch (IOException e) {
      System.out.println("IOException occurred: " + e.getMessage());
    }
  }

  private int computeRank() {
    rank = 1;
    if (similarityValues != null) {
      for (double values : similarityValues) {
        if (relCos < values)
          rank++;
      }
    }
    return rank;
  }

  /**
   * This method processes the queries and documents and stores all the
   * required values (Similarity values and rank)
   */
  @Override
  public void processCas(CAS aCas) throws ResourceProcessException {

    JCas jcas;
    try {
      jcas = aCas.getJCas();
    } catch (CASException e) {
      throw new ResourceProcessException(e);
    }

    FSIterator<?> it = jcas.getAnnotationIndex(Document.type).iterator();
    if (it.hasNext()) {
      Document doc = (Document) it.next();
      FSList fsTokenList = doc.getTokenList();
      ArrayList<Token> tokenList = Utils.fromFSListToCollection(fsTokenList, Token.class);
      if (doc.getRelevanceValue() == 99) {
        similarityValues.clear();
        queryList.clear();
        docList.clear();
        relCos = -1.0;
        cosineSimilarity = 0.0;
        /** Implementations of dice coefficient and Jaccard distance are commented out**/
        // diceSimilarity = 0.0;
        // jaccardDistance = 0.0;
        rank = 1;
        for (Token token : tokenList) {
          queryList.put(token.getText(), token.getFrequency());
        }
      } else {
        docList = new HashMap<String, Integer>();
        for (Token token : tokenList) {
          docList.put(token.getText(), token.getFrequency());
        }
        cosineSimilarity = computeCosineSimilarity(queryList, docList);
        /** Implementations of dice coefficient and Jaccard distance are commented out**/
        // diceSimilarity = computeDiceCoefficient(queryList, docList);
        // jaccardDistance = computeJaccardDistance(queryList, docList);
      }

      if (doc.getRelevanceValue() == 1) {
        qIdList.add(doc.getQueryID());
        relList.add(doc.getRelevanceValue());
        similarityList.add(cosineSimilarity);
        /** Implementations of dice coefficient and Jaccard distance are commented out**/
        // similarityList.add(diceSimilarity);
        // similarityList.add(jaccardDistance);
        sentenceList.add(doc.getText());
        relCos = cosineSimilarity;
        // relCos = diceSimilarity;
        // relCos = jaccardDistance;
        rankList.put(doc.getQueryID(), computeRank());
      } else if (relCos == -1.0 && doc.getRelevanceValue() != 99) {
        similarityValues.add(cosineSimilarity);
        /** Implementations of dice coefficient and Jaccard distance are commented out**/
        // similarityValues.add(diceSimilarity);
        // similarityValues.add(jaccardDistance);
      } else {
        if (relCos < cosineSimilarity)
          rankList.put(doc.getQueryID(), ++rank);
        /** Implementations of dice coefficient and Jaccard distance are commented out**/
        /*
         * if(relCos < diceSimilarity) rankList.put(doc.getQueryID(), ++rank);
         */
        /*
         * if(relCos < jaccardDistance) rankList.put(doc.getQueryID(), ++rank);
         */
      }

    }
  }

  /**
   * This method saves the outputs to the specified file in the specified format
   */
  @Override
  public void collectionProcessComplete(ProcessTrace arg0) throws ResourceProcessException,
          IOException {

    super.collectionProcessComplete(arg0);

    DecimalFormat df = new DecimalFormat("#0.0000");
    
    // Saving output
    for (int i = 0; i < qIdList.size(); i++) {
      writer.println("cosine=" + df.format(similarityList.get(i))
              + "\trank=" + rankList.get(qIdList.get(i)) + "\tqid=" + qIdList.get(i) + "\trel="
              + relList.get(i) + "\t" + sentenceList.get(i));
    }

    // Saving MRR
    double metric_mrr = compute_mrr();
    writer.println("MRR=" + metric_mrr);
    
    writer.close();
  }

  /**
   * Computes cosine similarity for two vectors
   * @return cosine_similarity
   */
  private double computeCosineSimilarity(Map<String, Integer> queryVector,
          Map<String, Integer> docVector) {
    double cosine_similarity = 0.0;
    double dot_product = 0.0;
    double queryVectorVal = 0.0;
    double docVectorVal = 0.0;
    for (String key : queryVector.keySet()) {
      queryVectorVal += (queryVector.get(key) * queryVector.get(key));
      if (docVector.containsKey(key))
        dot_product += queryVector.get(key) * docVector.get(key);
    }
    for (String key : docVector.keySet()) {
      docVectorVal += (docVector.get(key) * docVector.get(key));
    }
    cosine_similarity = dot_product / (Math.sqrt(queryVectorVal) * Math.sqrt(docVectorVal));

    return cosine_similarity;
  }

  /**
   * Compute Dice coefficient for two vectors
   * @return Sørensen–Dice_coefficient
   */
  private double computeDiceCoefficient(Map<String, Integer> queryVector,
          Map<String, Integer> docVector) {
    double dice_coefficient = 0.0;
    double dot_product = 0.0;
    double queryVectorVal = 0.0;
    double docVectorVal = 0.0;
    for (String key : queryVector.keySet()) {
      queryVectorVal += (queryVector.get(key) * queryVector.get(key));
      if (docVector.containsKey(key))
        dot_product += queryVector.get(key) * docVector.get(key);
    }
    for (String key : docVector.keySet()) {
      docVectorVal += (docVector.get(key) * docVector.get(key));
    }
    dice_coefficient = (2 * dot_product) / (queryVectorVal + docVectorVal);

    return dice_coefficient;
  }

  /**
   * Compute Jaccard distance for two vectors
   * @return Jaccard distance
   */
  private double computeJaccardDistance(Map<String, Integer> queryVector,
          Map<String, Integer> docVector) {
    double jaccard_distance = 0.0;
    double dot_product = 0.0;
    double queryVectorVal = 0.0;
    double docVectorVal = 0.0;
    for (String key : queryVector.keySet()) {
      queryVectorVal += (queryVector.get(key) * queryVector.get(key));
      if (docVector.containsKey(key))
        dot_product += queryVector.get(key) * docVector.get(key);
    }
    for (String key : docVector.keySet()) {
      docVectorVal += (docVector.get(key) * docVector.get(key));
    }
    jaccard_distance = (dot_product) / (queryVectorVal + docVectorVal - dot_product);

    return jaccard_distance;
  }

  /**
   * Compute MRR for the set of ranks
   * @return mrr
   */
  private double compute_mrr() {
    double metric_mrr = 0.0;
    double value = 0.0;
    for (int i = 0; i < qIdList.size(); i++) {
      value = value + (1.0 / rankList.get(qIdList.get(i)));
    }
    metric_mrr = value * (1.0 / qIdList.size());
    return (double) Math.round(metric_mrr * 10000) / 10000;
  }

}
