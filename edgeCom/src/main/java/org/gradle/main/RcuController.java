package org.gradle.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gradle.common.CommonResponse;
import org.gradle.mapper.RcuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
 
@Controller
@EnableAutoConfiguration
public class RcuController {
 
	@Autowired
    private RcuMapper mapper;
	
	@RequestMapping(value="/rcu")
	@ResponseBody
	public HashMap<String, Object> rcu(
			@RequestParam(value="eki[]") ArrayList<Integer> eki,
			@RequestParam(value="dki[]") ArrayList<Integer> dki,
			@RequestParam(value="type[]") ArrayList<String> type) throws Exception{	
		ArrayList<Object> list=new ArrayList<Object>();
		
		for(int i=0; i<eki.size(); i++)
		{
			HashMap<String,Object> map=new HashMap<String, Object>();
			map.put("eki", eki.get(i));
			map.put("dki", dki.get(i));
			map.put("type", type.get(i));
			list.add(map);
		}
		HashMap<String,Object> param=new HashMap<String, Object>();
		param.put("list", list);
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("list", mapper.rcu(param));
		result.put("deviceInfo", mapper.selectDeviceInfo());
		result.put("deviceCode", mapper.selectDeviceCode());
		
		return CommonResponse.result(1, "성공",result);
	}
	
	@RequestMapping(value="/rcuHistory")
	@ResponseBody
	public HashMap<String, Object> rcuHistory(
			@RequestParam(value="eki", defaultValue="false") int eki,
			@RequestParam(value="dki", defaultValue="false") int dki,
			@RequestParam(value="type", defaultValue="false") String type
		) throws Exception{
		HashMap<String,Object> map = new HashMap<String, Object>();
		map.put("eki", eki);
		map.put("dki", dki);
		map.put("type", type);
		return CommonResponse.result(1, "성공",mapper.rcuHistory(map));
	}
}