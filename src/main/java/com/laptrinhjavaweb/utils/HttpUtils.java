package com.laptrinhjavaweb.utils;

import java.io.BufferedReader;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpUtils {
	String value;
	public HttpUtils(String value){
		this.value=value;
	
	}
	
	public  <T> T toModel(Class<T> tclass){
		try {
			return new ObjectMapper().readValue(value, tclass);
		} catch (IOException e) {
		
			e.printStackTrace();
		}
		return null;
	}
	
	public static HttpUtils of(BufferedReader reader){
		StringBuilder sb = new StringBuilder();
		String line="";
		try {
			while((line = reader.readLine())!=null){
				sb.append(line);
			}
		} catch (IOException e) {
		
			e.printStackTrace();
			return null;
		}
		return new HttpUtils(sb.toString());
		
	}
}

	