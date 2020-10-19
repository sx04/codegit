package com.cetcbigdata.varanus.controller;

import com.cetcbigdata.varanus.core.DispatcherListCrawler;
import com.cetcbigdata.varanus.service.TaskBasicInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created with IDEA author:Matthew Date:2019-4-9 Time:10:29
 */

@Controller
public class IndexController {

	@Autowired
	private TaskBasicInfoService taskBasicInfoService;

	@GetMapping("index.html")
	public String index(Model model) {
		return "index";
	}

	@GetMapping("start/task")
	@ResponseBody
	public void start(){
		taskBasicInfoService.tt();
	}



}
