package edu.cmu.lti.f14.hw3.hw3_pyadapad.annotators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import edu.cmu.lti.f14.hw3.hw3_pyadapad.typesystems.Document;
import edu.cmu.lti.f14.hw3.hw3_pyadapad.typesystems.Token;
import edu.cmu.lti.f14.hw3.hw3_pyadapad.utils.Utils;

public class DocumentVectorAnnotator extends JCasAnnotator_ImplBase {

  @Override
  public void process(JCas jcas) throws AnalysisEngineProcessException {

    FSIterator<Annotation> iter = jcas.getAnnotationIndex().iterator();
    if (iter.isValid()) {
      iter.moveToNext();
      Document doc = (Document) iter.get();
      createTermFreqVector(jcas, doc);
      doc.addToIndexes();
    }

  }

  /**
   * A basic white-space tokenizer, it deliberately does not split on punctuation!
   *
   * @param doc
   *          input text
   * @return a list of tokens.
   */

  List<String> tokenize0(String doc) {
    List<String> res = new ArrayList<String>();
    for (String s : doc.split("\\s+"))
      res.add(s);
    return res;
  }

  /**
   * 
   * @param jcas
   * @param doc
   */

  private void createTermFreqVector(JCas jcas, Document doc) {

    String docText = doc.getText();

    // Tokenizing using Dumb Tokenizer
    ArrayList<String> tokens = (ArrayList<String>) tokenize0(docText);
    // Using Stanford lemmatizer instead of dumb tokenizer
    // List<String> tokens = Arrays.asList(StanfordLemmatizer.stemText(docText).split(" "));
    HashMap<String, Integer> tokenCount = new HashMap<String, Integer>();
    for (String token : tokens) {
      if (tokenCount.containsKey(token))
        tokenCount.put(token, tokenCount.get(token) + 1);
      else
        tokenCount.put(token, 1);
    }
    Collection<Token> tokenCollection = new ArrayList<Token>();
    for (String token : tokens) {
      Token t = new Token(jcas);
      t.setText(token);
      t.setFrequency(tokenCount.get(token));
      tokenCollection.add(t);
    }
    doc.setTokenList(Utils.fromCollectionToFSList(jcas, tokenCollection));
  }

}
