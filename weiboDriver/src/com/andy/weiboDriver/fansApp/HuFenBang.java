package com.andy.weiboDriver.fansApp;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.andy.weiboDriver.util.Threads;
import com.andy.weiboDriver.webDriver.WebDriverUtil;
import com.andy.weiboDriver.webDriver.WeiboSina;

public class HuFenBang {
	private static Logger logger = Logger.getLogger(  HuFenBang.class);
	

	/*
	 * TODO,只是复制过来了，还没有修改代码
	 * 一键关注页面：http://www.weibo01.com/reward_website/earn_score 一键关注代码： <button
	 * class="mutualFollowBtn1"
	 * style="margin:0px; vertical-align:middle; float:right;"
	 * onclick="batchFollowWeibo();">一键关注</button>
	 */
	// http://tuitu.sinaapp.com/weibo/?ref=appmy
	public static void main(String[] args) {
		WebDriver fd = new FirefoxDriver();
		String username = "yitest0805@sina.com";
		String password = "andy0805";
		new WeiboSina().login(fd, username, password);
		HuFenBang tu = new HuFenBang();
		// 先弄积分再继续推
		tu.getScoreFlow(fd);
		logger.info(10);
	}

	public boolean getScoreFlow(WebDriver fd) {
		boolean flag = oneKeyScore(fd);
		startSpread(fd);
		logger.info("完成一个app关注");
		return flag;
	}

	private void startSpread(WebDriver fd) {
		String url = "http://www.weibo01.com/reward_website/my_reward";
		WebDriverUtil.getUrl(fd, url,By.id("ifollowManage"));
		WebElement ifollowManageWe = fd.findElement(By.id("ifollowManage"));
		WebElement  startWe = ifollowManageWe.findElements(By.cssSelector("td.moneyManageBtn")).get(0);
		if (startWe.getText().contains("关闭")) {
			WebElement  startA = startWe.findElement(By.tagName("a"));
			startA.click();
		}
	}

	// 一键关注全部，然后翻页
	private boolean oneKeyScore(WebDriver fd) {
//		String url1 = "http://apps.weibo.com/fansreward";
//		WebDriverUtil.getUrl(fd, url1);
		String url2 = "http://www.weibo01.com/reward_website/earn_score";
		WebDriverUtil.getUrl(fd, url2,By.id("earnFollowScoreArea"));
		WebElement scoreAreaWe = WebDriverUtil.findElement4Wait(fd, By.id("earnFollowScoreArea"), 3);
		WebElement followAllBtnWe = WebDriverUtil.findElement4Wait(scoreAreaWe, By.cssSelector("button.mutualFollowBtn1"), 3);
		followAllBtnWe.click();
		while (true) {
			WebElement overWe = WebDriverUtil.getElementOrNot(fd,By.id("messageContent"));
			if (null != overWe && overWe.isDisplayed() && overWe.getText().contains("多了")) {
				logger.info("今天关注的太多了，明天再试试吧");
				return false;
			}
				Threads.sleep(1000);
//				followAllBtnWe = fd.findElement(By.id("loading"));
				//判断弹出窗口是否一直存在
				if (!overWe.isDisplayed()) {
					break;
				}

		}
		fd.findElement(By.cssSelector("div[id=\"earnPageNav\"] >  a.jumpPageBtn")).click();
		return true;
	}
}
