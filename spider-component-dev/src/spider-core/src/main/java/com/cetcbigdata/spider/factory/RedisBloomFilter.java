package com.cetcbigdata.spider.factory;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisBloomFilter {

	@Autowired
	private RedisTemplate redisTemplate;

	private static final int[] seeds = new int[] { 3, 7, 11, 13, 31, 37, 55 };
	
	private static String FILTER_KEY="redis:bloom:filter";
	
	@Value("${crawler.bloomfilter}")
	private String bloomFilter;
	
	private static final int EXPIRE_TIME = 3600;

	private BloomHash[] func = new BloomHash[seeds.length];
	
	@PostConstruct
	public void getProperties() {
		if (!StringUtils.isBlank(bloomFilter)) {
			this.FILTER_KEY = bloomFilter;
		}
	}
	
	
	public RedisBloomFilter() {
		for (int i = 0; i < seeds.length; i++) {
			func[i] = new BloomHash(2 << 26, seeds[i]);
		}
		/*
		 * //设置RedisBloomFilter.FILTER_KEY redis队列的过期时间 int tempCount=1;
		 * if(redisTemplate.opsForValue()!=null) { tempCount
		 * =(int)redisTemplate.opsForValue().get("redis:bloom:filter")-1; }
		 * System.out.println("tempCount>>>"+tempCount);
		 * redisTemplate.opsForValue().set("redis:bloom:filter", tempCount);
		 * redisTemplate.expire("redis:bloom:filter",3600,TimeUnit.SECONDS);
		 */
	}

	/**
	 * 加入一个数据
	 * 
	 * @param value
	 */
	public void add(String value) {
		for (BloomHash f : func) {
			setBig(f.hash(value), true);
		}
	}

	/**
	 * 判重
	 * 
	 * @param value
	 * @return
	 */
	public boolean contains(String value) {
		if (value == null) {
			return false;
		}
		boolean ret = true;
		for (BloomHash f : func) {
			ret = ret && getBig(f.hash(value));
		}
		return ret;
	}

	/**
	 * redis连接池初始化并返回一个redis连接
	 * 
	 * @return redis连接实例
	 */

	private boolean setBig(int offset, boolean value) {
		return redisTemplate.opsForValue().setBit(FILTER_KEY, offset, value);
	}

	private boolean getBig(int offset) {
		return redisTemplate.opsForValue().getBit(FILTER_KEY, offset);
	}

	/**
	 * 一个简单的hash算法类，输出int类型hash值
	 * 
	 * @author zhiyong.xiongzy
	 *
	 */
	public static class BloomHash {

		private int cap;
		private int seed;

		public BloomHash(int cap, int seed) {
			this.cap = cap;
			this.seed = seed;
		}

		public int hash(String value) {
			int result = 0;
			int len = value.length();
			for (int i = 0; i < len; i++) {
				result = seed * result + value.charAt(i);
			}
			return (cap - 1) & result;
		}
	}

	public static void main(String[] args) {
		String value = "95cea659143842e3f787f96910cac2bb2f32d207";
		RedisBloomFilter filter = new RedisBloomFilter();
		System.out.println(filter.contains(value));
		filter.add(value);
		System.out.println(filter.contains(value));

	}
}