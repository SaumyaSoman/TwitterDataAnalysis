package com.twitter.analyseTweet;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.twitter.saveTweet.DataDB;
import com.twitter.saveTweet.InsertData;

public class AnalyseTweet {

	public static void main(String args[]) throws JSONException{
		
		String searchId1="Barbara Buono";
		String searchId2="Chris Christie";
		InsertData db=new InsertData();
		List<DataDB> dataList=db.getTweets(searchId1);
		int score1=0;
		int score2=0;
		boolean id1=false;
		boolean id2=false;
		int positiveTweetCount1=0;
		int positiveTweetCount2=0;
		int negativeTweetCount1=0;
		int negativeTweetCount2=0;
		for(DataDB data:dataList){
			JSONObject json=new JSONObject(data.getJson());
			String tweet=json.getString("text");
			SentimentClassifier classifier=new SentimentClassifier();
			String type=classifier.classify(tweet);
			int retweetCount=json.getInt("retweet_count");
			int favCount=json.getInt("favorite_count");
			if(tweet.contains(searchId1) && !tweet.contains(searchId2)){
				
				if(type.equalsIgnoreCase("pos") || type.equalsIgnoreCase("neu")){
					id1=true;
					positiveTweetCount1++;
				}else if(type.equalsIgnoreCase("neg")){
					id2=true;
					negativeTweetCount1++;
				}
				
				
			}else if(tweet.contains(searchId2) && !tweet.contains(searchId1)){
				if(type.equalsIgnoreCase("pos") || type.equalsIgnoreCase("neu")){
					id2=true;
					positiveTweetCount2++;
				}else if(type.equalsIgnoreCase("neg")){
					id1=true;
					negativeTweetCount2++;
				}
				
			}
//			else if(tweet.contains(searchId1) && tweet.contains(searchId2)){
//				if(type.equalsIgnoreCase("pos")){
//					
//				}else if(type.equalsIgnoreCase("neg")){
//					
//				}else if(type.equalsIgnoreCase("neu")){
//					
//				}
//			}
			if(id1=true){
				score1=score1+favCount;
				score1=score1+retweetCount;
			}else if (id2==true){
				score2=score2+favCount;
				score2=score2+retweetCount;
			}
			//System.out.println(tweet);
		}
		int total=score1+score2+positiveTweetCount1+negativeTweetCount1+negativeTweetCount2+positiveTweetCount2;
		int total1=(score1+positiveTweetCount1+negativeTweetCount2)/total;
		int total2=(score2+positiveTweetCount2+negativeTweetCount1)/total;
		int percentage=0;
		if(total1>=total2){
			percentage=((total1-total2)/total)*100;
		}else{
			percentage=((total1-total1)/total)*100;
		}
		System.out.println("percentage..."+percentage);
	}
}
