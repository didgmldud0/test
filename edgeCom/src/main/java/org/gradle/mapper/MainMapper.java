package org.gradle.mapper;

import java.util.HashMap;
import java.util.List;
 
public interface MainMapper {
	/**
	 * 사이트 리스트를 가져온다.
	 * @return
	 * @throws Exception
	 */
	public List<HashMap<String, Object>> selectSiteList() throws Exception;
	public List<HashMap<String, Object>> selectEdgeList() throws Exception;
	public List<HashMap<String, Object>> selectDeviceCodeList() throws Exception;
	public List<HashMap<String, Object>> selectEventLog(HashMap<String,Object> param) throws Exception;
	public HashMap<String, Object> selectValve(String valve) throws Exception;
	
	public int insertValveLog(HashMap<String,Object> param) throws Exception;
	public int insertValve(HashMap<String,Object> param) throws Exception;
	
	public String selectCtrlYn() throws Exception;
	public int selectStatus() throws Exception;
	
	
	public int updateStatus(HashMap<String,Object> param) throws Exception;
	public int updateEventLog(HashMap<String,Object> param) throws Exception;
}