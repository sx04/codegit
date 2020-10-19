package com.cetcbigdata.spider.feign;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cetcbigdata.spider.entity.Proxy;

@FeignClient(name = "IpPool")
public interface IpFeignClient {

	@RequestMapping(value = "/ip/get")
	public List<Proxy> getIp();
}
