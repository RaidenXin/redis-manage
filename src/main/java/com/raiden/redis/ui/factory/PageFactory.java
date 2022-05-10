package com.raiden.redis.ui.factory;


import com.raiden.redis.ui.page.IPageService;
import com.raiden.redis.ui.page.impl.TablePage;

/**
 * 页面创建工厂
 *
 */
public class PageFactory {

	public static IPageService createPageService(String itemName) {
		IPageService pageService = null;
		switch(itemName) {
			case "个人中心":
				pageService = new TablePage();
				break;
			case "健康统计":
				pageService = new TablePage();
				break;
			case "增加记录":
				pageService = new TablePage();
				break;
			default:
				pageService = new TablePage();
				break;
		}
		return pageService;
	}
}
