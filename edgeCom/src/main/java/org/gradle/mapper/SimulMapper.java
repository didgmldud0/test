package org.gradle.mapper;

import java.util.HashMap;
import java.util.List;
 
public interface SimulMapper {
	public HashMap<String, Object> selectControlData() throws Exception;
	public int updateValvestatus(HashMap<String,Object> param) throws Exception;
	public int deleteControlData(HashMap<String,Object> param) throws Exception;
	
	public int insertValueHistory(HashMap<String,Object> param) throws Exception;
}