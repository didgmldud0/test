package org.gradle.mapper;

import java.util.HashMap;
import java.util.List;
 
public interface RcuMapper {
 
	public List<HashMap<String, Object>> rcu(HashMap<String,Object> map) throws Exception;
	public List<HashMap<String, Object>> rcuHistory(HashMap<String,Object> map) throws Exception;
	public List<HashMap<String, Object>> selectDeviceCode() throws Exception;
	public List<HashMap<String, Object>> selectDeviceInfo() throws Exception;
	
}