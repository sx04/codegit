package com.cetcbigdata.spider.feign;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cetcbigdata.spider.entity.Url;

@FeignClient(name = "UrlFactory")
public interface UrlFeignClient {

	@RequestMapping(value = "/url/{nameKey}/list")
	public List<Url> getUrl(@PathVariable("nameKey") String nameKey);
}
