package org.gradle.main;

import java.util.HashMap;

import org.gradle.common.CommonResponse;
import org.gradle.mapper.MainMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
 
@Controller
@EnableAutoConfiguration
public class MainController {
 
	@Autowired
    private MainMapper mapper;
	
	@RequestMapping(value="/selectSiteList")
    @ResponseBody
    public HashMap<String, Object> selectSiteList() throws Exception{
        return CommonResponse.result(1, "성공", mapper.selectSiteList());
    }
    
    @RequestMapping(value="/selectEdgeList")
    @ResponseBody
    public HashMap<String, Object> selectEdgeList() throws Exception{
        return CommonResponse.result(1, "성공", mapper.selectEdgeList());
    }
    
    @RequestMapping(value="/selectDeviceCodeList")
    @ResponseBody
    public HashMap<String, Object> selectDeviceCodeList() throws Exception{
        return CommonResponse.result(1, "성공", mapper.selectDeviceCodeList());
    }
    
    @RequestMapping(value="/selectEventLog")
    @ResponseBody
    public HashMap<String, Object> selectEventLog(
    		@RequestParam(value="siteCode", defaultValue="0") String siteCode,
    		@RequestParam(value="edgeCode", defaultValue="0") String edgeCode,
    		@RequestParam(value="sDate", defaultValue="") String sDate,
    		@RequestParam(value="eDate", defaultValue="") String eDate,
    		@RequestParam(value="deviceCode", defaultValue="0") String deviceCode,
    		@RequestParam(value="page", defaultValue="0") int page,
    		@RequestParam(value="limit", defaultValue="100") int limit
    	) throws Exception{
    	HashMap<String, Object> param=new HashMap<>();
    	param.put("siteCode",siteCode);
    	param.put("edgeCode",edgeCode);
    	param.put("sDate",sDate);
    	param.put("eDate",eDate);
    	param.put("deviceCode",deviceCode);
    	param.put("page",page);
    	param.put("limit",limit);
    	
    	HashMap<String, Object> result=new HashMap<>();
    	result.put("list", mapper.selectEventLog(param));
    	
    	
        return CommonResponse.result(1, "성공",result );
    }
    
    @RequestMapping(value="/insertBvalue")
    @ResponseBody
    public HashMap<String, Object> insertBvalue(
    		@RequestParam(value="value", defaultValue="-1") int value
    ) throws Exception{
    	HashMap<String,Object> map=new HashMap<>();
    	map.put("name", "b_valve");
    	map.put("value", value);
    	mapper.insertValveLog(map);
        return CommonResponse.result(1, "성공", mapper.insertValve(map));
    }
    
    @RequestMapping(value="/insertPvalue")
    @ResponseBody
    public HashMap<String, Object> insertPvalue(@RequestParam(value="value", defaultValue="-1") int value) throws Exception{
    	HashMap<String,Object> map=new HashMap<>();
    	map.put("name", "p_valve");
    	map.put("value", value);
    	mapper.insertValveLog(map);
    	return CommonResponse.result(1, "성공", mapper.insertValve(map));
    }
}