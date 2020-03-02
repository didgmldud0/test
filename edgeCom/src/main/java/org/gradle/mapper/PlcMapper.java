package org.gradle.mapper;

import java.util.HashMap;
import java.util.List;
 
public interface PlcMapper {
 
	public HashMap<String, Object> plc(HashMap<String,Object> map) throws Exception;
	public List<HashMap<String, Object>> plcHistory(HashMap<String,Object> map) throws Exception;
	public List<HashMap<String, Object>> selectAiName() throws Exception;
	
	public HashMap<String, Object> selectLastPlcHistory() throws Exception;
	public int insertEventLog(HashMap<String,Object> param) throws Exception;
}