package com.lgallardo.qbittorrentclient;

public class JSONParserStatusCodeException extends Exception {
	
	public int code = 0;
	
	   public JSONParserStatusCodeException(String message) {
	        super(message);
	    }

	   public JSONParserStatusCodeException(int statusCode) {
	        super(""+statusCode);
	        code = statusCode;        
	        
	    }
	   
	   public int getCode(){
		   return code;
	   }

}