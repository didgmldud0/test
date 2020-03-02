package org.gradle;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.gradle.common.http.Http;
import org.gradle.common.http.HttpRespon;
import org.gradle.mapper.MainMapper;
import org.gradle.mapper.PlcMapper;
import org.gradle.mapper.SimulMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
@WebListener
public class DaemonListener implements ServletContextListener, Runnable
{
	private static final Logger logger = LoggerFactory.getLogger(DaemonListener.class);
	
	public static final int STATUS_INIT=0;
	public static final int STATUS_AI03_70=1;
	
	
	public static int STATUS=0;
			
	@Autowired
    private MainMapper mainMapper;
	
	@Autowired
    private PlcMapper plcMapper;
	
	@Autowired
    private SimulMapper simulMapper;
	
    /** 작업을 수행할 thread */
    private Thread thread;
    private boolean isShutdown = false;
    /** context */
    private ServletContext sc;
    /** 작업을 수행한다 */
    public void startDaemon() {
        if (thread == null) {
            thread = new Thread(this, "Daemon thread for background task");
        }
        if (!thread.isAlive()) {
            thread.start();
        }
        
    	// 이벤트로그 전송
        new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					try {
						HashMap<String, Object> param=new HashMap<>();
						param.put("procYn", "N");
						final List<HashMap<String, Object>> eventList = mainMapper.selectEventLog(param);
						
						ObjectMapper mapper = new ObjectMapper();
						String clientFilterJson = "";
						try {
						    clientFilterJson = mapper.writeValueAsString(eventList);
						} catch (IOException e) {
						    e.printStackTrace();
						}
						if(eventList.size()>0){
							Http.post("http://localhost:8080/eventLog", clientFilterJson,new HttpRespon() {
								
								@Override
								public void success(String result) {
									for(HashMap<String, Object> map : eventList){
										try {
											mainMapper.updateEventLog(map);
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								}
								
								@Override
								public void fail(String result) {
									// TODO Auto-generated method stub
									
								}
							});
						}
						Thread.sleep(2500);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
        
    }
    public void inertEventLog(String name,int valve,String devCode,int status,double devCodeValue){
    	HashMap<String,Object> map=new HashMap<>();
    	map.put("devCode", devCode);
    	map.put("status", status);
    	map.put("name", name);
    	map.put("value", valve);
    	map.put("devCodeValue", devCodeValue);

    	if(status==1){
    		map.put("alarmName", "실린더 압력이 높습니다.");
    		try {
    			plcMapper.insertEventLog(map);
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}else if(status==2){
    		map.put("alarmName", "실린더 압력이 위험합니다.");
    		try {
    			plcMapper.insertEventLog(map);
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    	
    	
    	if(name.equals("b_valve") && valve==0){
    		map.put("alarmName", "원료주입 중단");
    	}else if(name.equals("b_valve") && valve==1){
    		map.put("alarmName", "원료주입 시작");
    	}else if(name.equals("p_valve") && valve==1){
    		map.put("alarmName", "가스방출시작");
    	}else if(name.equals("p_valve") && valve==0){
    		map.put("alarmName", "가스방출중단");
    	}if(name.equals("p_valve") && valve>1){
    		map.put("alarmName", "가스방출 밸브 "+valve+"로 조정");
    	}
    	
    	try {
    		mainMapper.insertValveLog(map);
			mainMapper.insertValve(map);
			plcMapper.insertEventLog(map);
			mainMapper.updateStatus(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
//    public void setValve(String name,int valve){
//    	HashMap<String,Object> map=new HashMap<>();
//    	map.put("name", name);
//    	map.put("value", valve);
//    	try {
//			mainMapper.insertValveLog(map);
//			mainMapper.insertValve(map);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    }
    
    /** 스레드가 실제로 작업하는 부분 */
    public void run() {
        Thread currentThread = Thread.currentThread();
        while (currentThread == thread && !this.isShutdown) {
            try {
            	
            	HashMap<String, Object> bvalveMap = mainMapper.selectValve("b_valve");
            	HashMap<String, Object> pvalveMap = mainMapper.selectValve("p_valve");
            	
            	HashMap<String, Object> plc =  plcMapper.selectLastPlcHistory();
            	
            	int bvalve = (int) bvalveMap.get("status");
            	int pvalve = (int) pvalveMap.get("status");
            	Double ai03 = (double) plc.get("ai_03");
            	Double ai02 = (double) plc.get("ai_03");
            	
            	System.out.println(bvalve);
            	System.out.println(pvalve);
            	System.out.println(ai03);
            	
//            	if(STATUS==STATUS_AI03_70){
        		if(ai03<30){
        			logger.info("ai03<30");
        			if(bvalve!=1)
                	{
        				inertEventLog("b_valve",1,"ai_03",0,ai03);
        			}
        			STATUS=STATUS_INIT;
        		}
//            		if(bvalve!=0)
//            		{
//            			setValve("b_valve",0);
//            		}
//            		continue;
//            	}
            	
            	logger.info("=====================================================");
            	if(ai03<50){
            		logger.info("ai03>30 && ai03<40");
            		if(pvalve!=1)
            		{
            			inertEventLog("p_valve",1,"ai_03",0,ai03);
            		}
            	}else if(ai03>50 && ai03<60){
            		logger.info("ai03>40 && ai03<50");
            		if(pvalve!=2)
            		{
            			inertEventLog("p_valve",2,"ai_03",0,ai03);
            		}
            	}else if(ai03>70 && ai03<80){
            		logger.info("ai03>50 && ai03<60");
            		boolean isChange=false;
            		if(pvalve!=3)
            		{
            			isChange=true;
            			inertEventLog("p_valve",3,"ai_03",0,ai03);
            		}
            		if(bvalve!=0)
            		{
            			isChange=true;
            			inertEventLog("b_valve",0,"ai_03",0,ai03);
            		}
            	}else if(ai03>80 && ai03<90){
            		logger.info("ai03>60 && ai03<70");
            		boolean isChange=false;
            		if(pvalve!=5)
            		{
            			isChange=true;
            			inertEventLog("p_valve",4,"ai_03",1,ai03);
            		}
            		if(bvalve!=0)
            		{
            			isChange=true;
            			inertEventLog("b_valve",0,"ai_03",1,ai03);
            		}
            			
            	}else if(ai03>90 || ai02>90){
            		logger.info("ai03>90");
            		boolean isChange=false;
            		if(pvalve!=5)
            		{
            			isChange=true;
            			inertEventLog("p_valve",5,"ai_03",2,ai03);
            		}
            		if(bvalve!=0)
            		{
            			isChange=true;
            			inertEventLog("b_valve",0,"ai_03",2,ai03);
            		}
            			
            	}
            	
            	logger.info("=====================================================");
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        System.out.println ("== DaemonListener end. ==");
    }
    /** 컨텍스트 초기화 시 데몬 스레드를 작동한다 */
    public void contextInitialized (ServletContextEvent event) {
        System.out.println ("== DaemonListener.contextInitialized has been called. ==");
        sc = event.getServletContext();
        startDaemon();
    }
    /** 컨텍스트 종료 시 thread를 종료시킨다 */
    public void contextDestroyed (ServletContextEvent event) {
        System.out.println ("== DaemonListener.contextDestroyed has been called. ==");
        this.isShutdown = true;
        try
        {
            thread.join();
            thread = null;
        }
        catch (InterruptedException ie)
        {
            ie.printStackTrace();
        }
    }
}