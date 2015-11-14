/*
 *   Copyright (c) 2014-2015 Luis M. Gallardo D.
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the GNU Lesser General Public License v3.0
 *   which accompanies this distribution, and is available at
 *   http://www.gnu.org/licenses/lgpl.html
 *
 */

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