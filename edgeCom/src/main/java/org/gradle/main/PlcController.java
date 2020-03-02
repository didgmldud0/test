package org.gradle.main;

import java.util.HashMap;

import org.gradle.common.CommonResponse;
import org.gradle.mapper.MainMapper;
import org.gradle.mapper.PlcMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
 
@Controller
@EnableAutoConfiguration
public class PlcController {
 
	@Autowired
    private PlcMapper mapper;
	
	@Autowired
    private MainMapper mainMapper;
	
    @RequestMapping(value="/plc")
    @ResponseBody
    public HashMap<String, Object> plc(@RequestParam HashMap<String,Object> param) throws Exception{
    	HashMap<String, Object> result = new HashMap<String, Object>();
    	result.put("map", mapper.plc(param));
    	result.put("code", mapper.selectAiName());
    	result.put("ctrlYn", mainMapper.selectCtrlYn());
    	result.put("status", mainMapper.selectStatus());
    	
    	HashMap<String, Object> bvalveMap = mainMapper.selectValve("b_valve");
    	HashMap<String, Object> pvalveMap = mainMapper.selectValve("p_valve");
    	
    	result.put("bvalve", bvalveMap.get("status"));
    	result.put("pvalve", pvalveMap.get("status"));
    	
    	
    
    	
    	return CommonResponse.result(1, "성공",result);
    }
    
    @RequestMapping(value="/plcHistory")
    @ResponseBody
    public HashMap<String, Object> plcHistory(@RequestParam(value="columnCode", defaultValue="false") String columnCode) throws Exception{
    	System.out.println(columnCode);
    	HashMap<String,Object> map = new HashMap<String, Object>();
    	map.put("columnCode", columnCode);
    	map.put("code", mapper.selectAiName());
    	return CommonResponse.result(1, "성공",mapper.plcHistory(map));
    }
    
    @RequestMapping(value="/lastPlcHistory")
    @ResponseBody
    public HashMap<String, Object> lastPlcHistory() throws Exception{
    	HashMap<String,Object> map = new HashMap<String, Object>();
    	map.put("list", mapper.selectLastPlcHistory());
    	map.put("code", mapper.selectAiName());
    	return CommonResponse.result(1, "성공",map);
    }
}