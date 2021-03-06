package com.andy.weiboDriver.webDriver;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.andy.weiboDriver.fansApp.Qiuzf;
import com.andy.weiboDriver.fansApp.Tuimi;
import com.andy.weiboDriver.fansApp.Tuitu;
import com.andy.weiboDriver.util.FileUtil;
import com.andy.weiboDriver.util.Threads;
import com.andy.weiboDriver.util.XMLConfig;

public class Main {
	private static Logger logger = Logger.getLogger(  Main.class);
	
	public static void main(String[] args) throws ConfigurationException, InterruptedException {

		int caseNum = 0;
		if (null != args && args.length > 0) {
			caseNum = Integer.parseInt(args[0]);
		}
		List<Object> weiboList = XMLConfig.getConfig().getList("weibo.weibo_username");
		String firefoxRun = XMLConfig.getConfig().getString("firefoxRun");
		int weiboNum = weiboList.size();
		WebDriver fd = null;
		if("false".equals(firefoxRun)){
			fd = new HtmlUnitDriver();
		}else{
			fd = new FirefoxDriver();
			
		}
		if(caseNum==0){
			 new DriverWeiboQQ().getMessageFlow(fd, weiboNum);
		}else if(caseNum==1){
			new WeiboSendAtPP().sendAtPPFlow(fd, weiboNum);
		} else if (caseNum == 2) {
			new DriverWeiboQQ().getMessageFlow(fd, weiboNum);
			new WeiboSendAtPP().sendAtPPFlow(fd, weiboNum);
		} else if (caseNum == 3) {
			iterateGetScore(fd, weiboNum);
		}

		fd.quit();
	}

	public static void iterateGetScore(WebDriver fd, int weiboNum) {
		while (true) {
				for (int i = 0; i < weiboNum; i++) {
					String path = System.getProperty("user.dir") + File.separator;
					Map<String, Integer> map = new HashMap<String,Integer>();
					int num = 0 ;
					int numT =0;
					String username = XMLConfig.getConfig().getString("weibo(" + i + ").weibo_username");
					String password = XMLConfig.getConfig().getString("weibo(" + i + ").weibo_password");
					String weiboUrl = XMLConfig.getConfig().getString("weibo(" + i + ").weibo_url");
					SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
					SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					logger.info(sf1.format(new Date()));
					path  += username + sf.format(new Date()) + ".txt";
					String fileMess = username + "\n";
					FileUtil.write2FileEnd(path, fileMess);
					new WeiboSina().login(fd, username, password);
					map= WebDriverUtil.getNumInfoAtUrl(fd,weiboUrl);
					num = map.get("关注");
					// 一键关注最多只到十页，有一页成功就退出
//					boolean flag = true;
					
					boolean flag = new Tuitu().getScoreFlow(fd);
					map= WebDriverUtil.getNumInfoAtUrl(fd,weiboUrl);
					numT = map.get("关注");
					fileMess = sf1.format(new Date())+ ":本次关注:" + (numT - num);
					logger.info(fileMess);
					FileUtil.write2FileEnd(path, fileMess);
					num = numT;
					// 关注过多，
					if (!flag)
						return;
					Threads.sleep(1000*60*10);
					
					new Qiuzf().getScoreFlow(fd);
					map= WebDriverUtil.getNumInfoAtUrl(fd,weiboUrl);
					numT = map.get("关注");
					fileMess = sf1.format(new Date())+ ":本次关注:" + (numT - num);
					logger.info(fileMess);
					FileUtil.write2FileEnd(path, fileMess);
					num = numT;
					Threads.sleep(1000*60*10);
					
					new Tuimi().getScoreFlow(fd);
					map= WebDriverUtil.getNumInfoAtUrl(fd,weiboUrl);
					numT = map.get("关注");
					fileMess = sf1.format(new Date())+ ":本次关注:" + (numT - num);
					logger.info(fileMess);
					FileUtil.write2FileEnd(path, fileMess);
					num = numT;
					Threads.sleep(1000*60*10);
				}
		}
	}
	



}
