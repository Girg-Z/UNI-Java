package it.univpm.ticketmaster.helper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import it.univpm.ticketmaster.exception.HttpException;

public class HttpHelper{
    
    public static String get (String url) throws HttpException{
		String data = "";
		String line = "";
        try {
			
			URLConnection openConnection = new URL(url).openConnection();
			InputStream in = openConnection.getInputStream();
			
			 try {
			   InputStreamReader inR = new InputStreamReader( in );
			   BufferedReader buf = new BufferedReader( inR );
			  
			   while ( ( line = buf.readLine() ) != null ) {
				   data += line;
			   }
			 } finally {
			   in.close();
			 }
			
			
			int code = ((HttpURLConnection) openConnection).getResponseCode();
			if(code!=200){
				throw new HttpException(code);
			}
			
		} catch (Exception e) {
			throw new HttpException(e.getMessage());
        }
		return data;
    }
}