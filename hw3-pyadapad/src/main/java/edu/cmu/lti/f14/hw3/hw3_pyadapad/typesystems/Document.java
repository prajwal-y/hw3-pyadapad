

/* First created by JCasGen Fri Oct 11 01:58:03 EDT 2013 */
package edu.cmu.lti.f14.hw3.hw3_pyadapad.typesystems;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Wed Oct 22 04:24:38 EDT 2014
 * XML source: /home/prajwal/git/hw3-pyadapad/hw3-pyadapad/src/main/resources/descriptors/retrievalsystem/RetrievalEvaluator.xml
 * @generated */
public class Document extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Document.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated  */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Document() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Document(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Document(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Document(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: relevanceValue

  /** getter for relevanceValue - gets 
   * @generated
   * @return value of the feature 
   */
  public int getRelevanceValue() {
    if (Document_Type.featOkTst && ((Document_Type)jcasType).casFeat_relevanceValue == null)
      jcasType.jcas.throwFeatMissing("relevanceValue", "edu.cmu.lti.f14.hw3.hw3_pyadapad.typesystems.Document");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Document_Type)jcasType).casFeatCode_relevanceValue);}
    
  /** setter for relevanceValue - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setRelevanceValue(int v) {
    if (Document_Type.featOkTst && ((Document_Type)jcasType).casFeat_relevanceValue == null)
      jcasType.jcas.throwFeatMissing("relevanceValue", "edu.cmu.lti.f14.hw3.hw3_pyadapad.typesystems.Document");
    jcasType.ll_cas.ll_setIntValue(addr, ((Document_Type)jcasType).casFeatCode_relevanceValue, v);}    
   
    
  //*--------------*
  //* Feature: queryID

  /** getter for queryID - gets 
   * @generated
   * @return value of the feature 
   */
  public int getQueryID() {
    if (Document_Type.featOkTst && ((Document_Type)jcasType).casFeat_queryID == null)
      jcasType.jcas.throwFeatMissing("queryID", "edu.cmu.lti.f14.hw3.hw3_pyadapad.typesystems.Document");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Document_Type)jcasType).casFeatCode_queryID);}
    
  /** setter for queryID - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setQueryID(int v) {
    if (Document_Type.featOkTst && ((Document_Type)jcasType).casFeat_queryID == null)
      jcasType.jcas.throwFeatMissing("queryID", "edu.cmu.lti.f14.hw3.hw3_pyadapad.typesystems.Document");
    jcasType.ll_cas.ll_setIntValue(addr, ((Document_Type)jcasType).casFeatCode_queryID, v);}    
   
    
  //*--------------*
  //* Feature: text

  /** getter for text - gets 
   * @generated
   * @return value of the feature 
   */
  public String getText() {
    if (Document_Type.featOkTst && ((Document_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "edu.cmu.lti.f14.hw3.hw3_pyadapad.typesystems.Document");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Document_Type)jcasType).casFeatCode_text);}
    
  /** setter for text - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setText(String v) {
    if (Document_Type.featOkTst && ((Document_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "edu.cmu.lti.f14.hw3.hw3_pyadapad.typesystems.Document");
    jcasType.ll_cas.ll_setStringValue(addr, ((Document_Type)jcasType).casFeatCode_text, v);}    
   
    
  //*--------------*
  //* Feature: tokenList

  /** getter for tokenList - gets 
   * @generated
   * @return value of the feature 
   */
  public FSList getTokenList() {
    if (Document_Type.featOkTst && ((Document_Type)jcasType).casFeat_tokenList == null)
      jcasType.jcas.throwFeatMissing("tokenList", "edu.cmu.lti.f14.hw3.hw3_pyadapad.typesystems.Document");
    return (FSList)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Document_Type)jcasType).casFeatCode_tokenList)));}
    
  /** setter for tokenList - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setTokenList(FSList v) {
    if (Document_Type.featOkTst && ((Document_Type)jcasType).casFeat_tokenList == null)
      jcasType.jcas.throwFeatMissing("tokenList", "edu.cmu.lti.f14.hw3.hw3_pyadapad.typesystems.Document");
    jcasType.ll_cas.ll_setRefValue(addr, ((Document_Type)jcasType).casFeatCode_tokenList, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    