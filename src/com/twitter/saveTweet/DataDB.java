package com.twitter.saveTweet;

import org.json.JSONObject;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.emul.org.bson.types.ObjectId;
import com.mongodb.util.JSON;

@Entity("dataDB")
public class DataDB {

//	@Id
//	private String id;

	private String json;
	private String searchId;
	public String getJson() {
		return json;
	}
	public void setJson(String string) {
		this.json = string;
	}
	public String getSearchId() {
		return searchId;
	}
	public void setSearchId(String searchId) {
		this.searchId = searchId;
	}
	
	
}
