package org.gradle.common;

import java.util.HashMap;

public class CommonResponse {
	public static HashMap<String,Object> result(int success,String message,Object res){
		HashMap<String, Object> result=new HashMap<String, Object>();
		result.put("result",success);
		result.put("msg",message);
		result.put("data",res);
		return result;
	}
}
