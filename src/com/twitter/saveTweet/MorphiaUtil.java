package com.twitter.saveTweet;

import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class MorphiaUtil {


    protected final Log log = LogFactory.getLog(getClass());
    private static Mongo mongo;
    private static Datastore datastore;

    static {
        try {
            // Create the database connection
            mongo =  new Mongo("localhost");
            datastore = new Morphia().createDatastore(mongo,"twitterData");
        } catch (UnknownHostException e) {
            System.err.println("Caught Unknown host exception:"+e);
            e.printStackTrace();
        } catch (MongoException e) {
            System.err.println("Initial Datastore creation failed:"+e);
            e.printStackTrace();
        }
    }

    public static Datastore getDatastore() {
        return datastore;
    }
} 