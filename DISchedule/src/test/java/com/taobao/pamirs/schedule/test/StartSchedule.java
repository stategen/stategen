package com.taobao.pamirs.schedule.test;

import java.util.Properties;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.unitils.UnitilsJUnit4;

import com.taobao.pamirs.schedule.strategy.TBScheduleManagerFactory;

/**
 * 调度测试
 * 
 * @author xuannan
 *
 */
//@SpringApplicationContext({ "schedule.xml" })
public class StartSchedule extends UnitilsJUnit4 {

	@Test
	public void testRunData() throws Exception {
	    ApplicationContext ctx = new ClassPathXmlApplicationContext(
                "schedule.xml");

        TBScheduleManagerFactory scheduleManagerFactory = new TBScheduleManagerFactory();

        Properties p = new Properties();
        p.put("zkConnectString", "192.168.3.117:2181");
        p.put("rootPath", "/taobao-pamirs-schedule/xuannan");
        p.put("zkSessionTimeout", "60000");
        p.put("userName", "ScheduleAdmin");
        p.put("password", "password");
        p.put("isCheckParentPath", "true");
        scheduleManagerFactory.setApplicationContext(ctx);
        scheduleManagerFactory.init(p);
        //scheduleManagerFactory.setZkConfig(p);
        
		Thread.sleep(100000000000000L);
	}
}
