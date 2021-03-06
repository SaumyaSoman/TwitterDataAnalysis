package com.twitter.saveTweet;


import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.DefaultHttpClientConnection;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;




public class TwitterSearch {
	
	
	private String twitter_consumer_key = "jl4WPblZ3JOwPwn6Sxgw";
	private String twitter_consumer_secret = "T1nP0XPu592jyQZybNU9rr2AI4PDiT2bJREla7PB2o8";	
	
	public String encode(String value) 
	{
        String encoded = null;
        try {
            encoded = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException ignore) {
        }
        StringBuilder buf = new StringBuilder(encoded.length());
        char focus;
        for (int i = 0; i < encoded.length(); i++) {
            focus = encoded.charAt(i);
            if (focus == '*') {
                buf.append("%2A");
            } else if (focus == '+') {
                buf.append("%20");
            } else if (focus == '%' && (i + 1) < encoded.length()
                    && encoded.charAt(i + 1) == '7' && encoded.charAt(i + 2) == 'E') {
                buf.append('~');
                i += 2;
            } else {
                buf.append(focus);
            }
        }
        return buf.toString();
    }
	
	private static String computeSignature(String baseString, String keyString) throws GeneralSecurityException, UnsupportedEncodingException 
	{
	    SecretKey secretKey = null;

	    byte[] keyBytes = keyString.getBytes();
	    secretKey = new SecretKeySpec(keyBytes, "HmacSHA1");

	    Mac mac = Mac.getInstance("HmacSHA1");
	    mac.init(secretKey);

	    byte[] text = baseString.getBytes();

	    return new String(Base64.encodeBase64(mac.doFinal(text))).trim();
	}
	
	
	// This is the search example, using a GET call
	// INPUT: the search query (q), the user's access_token and the user's access_token_secret
	// OUTPUT: if successful, twitter API will return a json object of tweets
		
	public JSONObject searchTweets(String q, String access_token, String access_token_secret)
	{
		JSONObject jsonresponse = new JSONObject();
		
		String oauth_token = access_token;
		String oauth_token_secret = access_token_secret;

		// generate authorization header
		String get_or_post = "GET";
		String oauth_signature_method = "HMAC-SHA1";
		
		String uuid_string = UUID.randomUUID().toString();
		uuid_string = uuid_string.replaceAll("-", "");
		String oauth_nonce = uuid_string; // any relatively random alphanumeric string will work here
		
		// get the timestamp
		Calendar tempcal = Calendar.getInstance();
		long ts = tempcal.getTimeInMillis();// get current time in milliseconds
		String oauth_timestamp = (new Long(ts/1000)).toString(); // then divide by 1000 to get seconds

		// the parameter string must be in alphabetical order
		// this time, I add 3 extra params to the request, "lang", "result_type" and "q".
		String parameter_string = "lang=en&oauth_consumer_key=" + twitter_consumer_key + "&oauth_nonce=" + oauth_nonce + "&oauth_signature_method=" + oauth_signature_method + 
			"&oauth_timestamp=" + oauth_timestamp + "&oauth_token=" + encode(oauth_token) + "&oauth_version=1.0&q=" + encode(q) + "&result_type=mixed" +"&rpp=100";	
		System.out.println("parameter_string=" + parameter_string);
		String twitter_endpoint = "https://api.twitter.com/1.1/search/tweets.json";
		String twitter_endpoint_host = "api.twitter.com";
		String twitter_endpoint_path = "/1.1/search/tweets.json";
		String signature_base_string = get_or_post + "&"+ encode(twitter_endpoint) + "&" + encode(parameter_string);
		System.out.println("signature_base_string=" + signature_base_string);
		
		// this time the base string is signed using twitter_consumer_secret + "&" + encode(oauth_token_secret) instead of just twitter_consumer_secret + "&"
		String oauth_signature = "";
		try {
			oauth_signature = computeSignature(signature_base_string, twitter_consumer_secret + "&" + encode(oauth_token_secret));  // note the & at the end. Normally the user access_token would go here, but we don't know it yet for request_token
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		String authorization_header_string = "OAuth oauth_consumer_key=\"" + twitter_consumer_key + "\",oauth_signature_method=\"HMAC-SHA1\",oauth_timestamp=\"" + oauth_timestamp + 
				"\",oauth_nonce=\"" + oauth_nonce + "\",oauth_version=\"1.0\",oauth_signature=\"" + encode(oauth_signature) + "\",oauth_token=\"" + encode(oauth_token) + "\"";
		System.out.println("authorization_header_string=" + authorization_header_string);


		 HttpParams params = new SyncBasicHttpParams();
		 HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		 HttpProtocolParams.setContentCharset(params, "UTF-8");
		 HttpProtocolParams.setUserAgent(params, "HttpCore/1.1");
		 HttpProtocolParams.setUseExpectContinue(params, false);

		 HttpProcessor httpproc = new ImmutableHttpProcessor(new HttpRequestInterceptor[] {
	                // Required protocol interceptors
	                new RequestContent(),
	                new RequestTargetHost(),
	                // Recommended protocol interceptors
	                new RequestConnControl(),
	                new RequestUserAgent(),
	                new RequestExpectContinue()});

		 HttpRequestExecutor httpexecutor = new HttpRequestExecutor();
		 HttpContext context = new BasicHttpContext(null);
		 HttpHost host = new HttpHost(twitter_endpoint_host,443);
		 DefaultHttpClientConnection conn = new DefaultHttpClientConnection();

		 context.setAttribute(ExecutionContext.HTTP_CONNECTION, conn);
		 context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, host);

		 try {
			 try {
				 SSLContext sslcontext = SSLContext.getInstance("TLS");
				 sslcontext.init(null, null, null);
				 SSLSocketFactory ssf = sslcontext.getSocketFactory();
				 Socket socket = ssf.createSocket();
				 socket.connect(
				   new InetSocketAddress(host.getHostName(), host.getPort()), 0);
				 conn.bind(socket, params);
				 
				 // the following line adds 3 params to the request just as the parameter string did above. They must match up or the request will fail.
				 BasicHttpEntityEnclosingRequest request2 = new BasicHttpEntityEnclosingRequest("GET", twitter_endpoint_path + "?lang=en&result_type=mixed&rpp=100&q=" + encode(q));
				 request2.setParams(params);
				 request2.addHeader("Authorization", authorization_header_string); // always add the Authorization header
				 
				 System.out.println("try" +request2.toString());
				 httpexecutor.preProcess(request2, httpproc, context);
				 HttpResponse response2 = httpexecutor.execute(request2, conn, context);
				 response2.setParams(params);
				 httpexecutor.postProcess(response2, httpproc, context);

				 if(response2.getStatusLine().toString().indexOf("500") != -1)
				 {
					 jsonresponse.put("response_status", "error");
					 jsonresponse.put("message", "Twitter auth error.");
				 }
				 else
				 {
					 // if successful, the response should be a JSONObject of tweets
					
					 JSONObject jo = new JSONObject(EntityUtils.toString(response2.getEntity()));
					 if(jo.has("errors"))
					 {
						 jsonresponse.put("response_status", "error");
						 String message_from_twitter = jo.getJSONArray("errors").getJSONObject(0).getString("message");
						 if(message_from_twitter.equals("Invalid or expired token") || message_from_twitter.equals("Could not authenticate you"))
							 jsonresponse.put("message", "Twitter auth error.");
						 else
							 jsonresponse.put("message", jo.getJSONArray("errors").getJSONObject(0).getString("message"));
					 }
					 else
					 {
						 
						 jsonresponse.put("twitter_jo", jo); // this is the full result object from Twitter
					 }
					 
					 conn.close();
				 }   
			 }
			 catch(HttpException he) 
			 {	
				// System.out.println(he.getMessage());
				 jsonresponse.put("response_status", "error");
				 jsonresponse.put("message", "searchTweets HttpException message=" + he.getMessage());
			 } 
			 catch(NoSuchAlgorithmException nsae) 
			 {	
				// System.out.println(nsae.getMessage());
				 jsonresponse.put("response_status", "error");
				 jsonresponse.put("message", "searchTweets NoSuchAlgorithmException message=" + nsae.getMessage());
			 } 					
			 catch(KeyManagementException kme) 
			 {	
				// System.out.println(kme.getMessage());
				 jsonresponse.put("response_status", "error");
				 jsonresponse.put("message", "searchTweets KeyManagementException message=" + kme.getMessage());
			 } 	
			 finally {
				 conn.close();
			 }
		 } 
		 catch(JSONException jsone)
		 {
			jsone.printStackTrace();
		 }
		 catch(IOException ioe)
		 {
			 ioe.printStackTrace();
		 }
		 return jsonresponse;
	}



}
