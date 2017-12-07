package de.itech.spaceshooter;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Connection {
	
	public static String getTopTen(String targetURL, String urlParameters) {		  
		  StringBuffer content = null;
		  try {
			  //Creating a Request
			  URL url = new URL("http://localhost:18181/Spaceshooter/GetTopTen");
			  HttpURLConnection con = (HttpURLConnection) url.openConnection();
			  con.setRequestMethod("GET");			  			 
			  BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			  String inputLine;
			  content = new StringBuffer();
		 	  while ((inputLine = in.readLine()) != null) {
			    content.append(inputLine);
			  }
			  in.close();
			  
			  con.disconnect();			  			 
		  } catch (Exception e) { System.out.println(e.toString()); };		  
		  if (content == null) { return ""; }
		  else { return content.toString(); }
	}
	
	public static void setPlayer(String player, String score) {
		try {			
			String url = "http://localhost:18181/Spaceshooter/SetNewPlayer?name="+player+"&score="+score;
			HashMap<String, String> params = new HashMap<String, String>();		    

		    // static class "HttpUtility" with static method "newRequest(url,method,callback)"		    
		    HttpUtility.newRequest(url,HttpUtility.METHOD_POST,params, new HttpUtility.Callback() {
		        @Override
		        public void OnSuccess(String response) { System.out.println("Server OnSuccess response="+response); }
		        @Override
		        public void OnError(int status_code, String message) { System.out.println("Server OnError status_code="+status_code+" message="+message); }
		    });			
		} catch (Exception e) { System.out.println(e.toString()); }
	}
	
	public static String getPlayer() {
		StringBuffer content = null;
		try {
			//Creating a Request
			URL url = new URL("http://localhost:18181/Spaceshooter/GetTopTen");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");			  			 
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			content = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				content.append(inputLine);
			}
			in.close();
					  
			con.disconnect();			  			 
		} catch (Exception e) { System.out.println(e.toString()); };		  
		if (content == null) { return ""; }
		else { return content.toString(); }		
	}
	
	public static ArrayList<String[]> getInputAsHashmap(String input) {	
		ArrayList<String[]> outputList = new ArrayList<String[]>();
		String inputCopy = input;
						
		while (inputCopy != "") {		
			inputCopy = inputCopy.substring(inputCopy.indexOf("name:") + 5);
			String name = inputCopy.substring(0,inputCopy.indexOf(","));			
			inputCopy = inputCopy.substring(inputCopy.indexOf(name) + name.length() + 7);
			String score = inputCopy.substring(0,inputCopy.indexOf('"'));
			if (inputCopy.indexOf("name:") == -1) {
				inputCopy = "";
			}	
			
			String[] player = { name, score };
			outputList.add(player);			
		}			
							
		return outputList;
	}			
}

//int responseCode = con.getResponseCode();			
//System.out.println("\nSending 'POST' request to URL : " + url);
//System.out.println("Post parameters : " + urlParameters);
//System.out.println("Response Code : " + responseCode);