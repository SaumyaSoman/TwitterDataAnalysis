package com.twitter.saveTweet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class Controller {
	
	public static void main(String[] args) throws JSONException {

		TwitterSearch twitter = new TwitterSearch();
		String searchId="Chris Christie";
		JSONObject data=twitter.searchTweets(searchId, "82623281-5WGmk6LVgs33ANlbvrqDSFhwKVbM2ST3JLuUP6SCf", "KBn2u4l34dSDYNuoNr8ktC61jUuOyvzrBJnCCumqgfhHl");
		InsertData dao=new InsertData();
		JSONObject twitterJson = (JSONObject) data.getJSONObject("twitter_jo");
		if(twitterJson.getJSONArray("statuses")!=null){
			JSONArray statusArray=twitterJson.getJSONArray("statuses");
			System.out.println(statusArray.length());
			for (int i=0;i<statusArray.length(); i++) {
				JSONObject status=statusArray.getJSONObject(i);
				System.out.println(status.toString());
				dao.save(searchId,status.toString());
			}
		}
		
		
		
	}
	

}
