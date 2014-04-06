package com.twitter.saveTweet;

import java.util.ArrayList;
import java.util.List;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.mongodb.util.JSON;

public class InsertData {

	public void save(String searchId, String json)
			throws IllegalArgumentException {
		try {
			Datastore datastore = MorphiaUtil.getDatastore();
			DataDB data=new DataDB();
			data.setSearchId(searchId);
			data.setJson(json);
			datastore.save(data);

		} catch (Exception e) {
			e.printStackTrace();

		}
	}
	
	public List<DataDB> getTweets(String searchId){
		List<DataDB> dataList=null;
		try {
			Datastore datastore = MorphiaUtil.getDatastore();
			//Query<DataDB> query= datastore.find(DataDB.class, "searchId", searchId);
			Query<DataDB> query= datastore.find(DataDB.class);
			dataList=query.asList();		

		} catch (Exception e) {
			e.printStackTrace();

		}
		return dataList;
	}
	
}