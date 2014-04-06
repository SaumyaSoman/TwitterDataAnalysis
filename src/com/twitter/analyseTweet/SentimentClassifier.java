package com.twitter.analyseTweet;

import java.io.File;
import java.io.IOException;

import com.aliasi.classify.ConditionalClassification;
import com.aliasi.classify.LMClassifier;
import com.aliasi.util.AbstractExternalizable;
import com.twitter.saveTweet.InsertData;


 public class SentimentClassifier {  
    String[] categories;  
    LMClassifier classifier;  
    public SentimentClassifier() {  
    try {  
    	classifier= (LMClassifier) AbstractExternalizable.readObject(new File("classifier.txt"));  
       categories = classifier.categories();  
    }  
    catch (ClassNotFoundException e) {  
       e.printStackTrace();  
    }  
    catch (IOException e) {  
       e.printStackTrace();  
    }  
    }  
    
    
    public String classify(String text) {  
    ConditionalClassification classification = classifier.classify(text);  
    return classification.bestCategory();  
    }  
    
    public static void main(String args[]){
    	SentimentClassifier a=new SentimentClassifier();
    	System.out.println("wowwww..."+a.classify("NYT \"Chris Christie’s health is ‘normal’ His willpower is 0,character is weak, slight case of megalomania, aside from that absolutely normal"));
  
    
    }
 } 

