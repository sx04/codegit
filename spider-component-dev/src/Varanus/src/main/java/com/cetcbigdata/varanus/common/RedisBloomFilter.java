package com.cetcbigdata.varanus.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class RedisBloomFilter {

	@Autowired
	private RedisTemplate redisTemplate;

	@PostConstruct
	public void ConstructSeed() {
		for (int i = 0; i < seeds.length; i++) {
			func[i] = new BloomHash(2 << 26, seeds[i]);
		}
	}
	private static final int[] seeds = new int[] { 3, 7, 11, 13, 31, 37, 55 };
	private BloomHash[] func = new BloomHash[seeds.length];

	/**
	 * 加入一个数据
	 * 
	 * @param value
	 */
	public void add(String bloomFilterKey,String value) {
		for (BloomHash f : func) {
			setBig(bloomFilterKey,f.hash(value), true);
		}
	}

	/**
	 * 判重
	 * 
	 * @param value
	 * @return
	 */
	public boolean contains(String bloomFilterKey,String value) {
		if (value == null) {
			return false;
		}
		boolean ret = true;
		for (BloomHash f : func) {
			ret = ret && getBig(bloomFilterKey,f.hash(value));
		}
		return ret;
	}
	/**
	 * 删除数据
	 *
	 */
	public void delete(String bloomFilterKey) {
		redisTemplate.delete(bloomFilterKey);
	}


	/**
	 * redis连接池初始化并返回一个redis连接
	 * 
	 * @return redis连接实例
	 */

	private boolean setBig(String bloomFilterKey,int offset, boolean value) {
		return redisTemplate.opsForValue().setBit(bloomFilterKey, offset, value);
	}

	private boolean getBig(String bloomFilterKey,int offset) {
		return redisTemplate.opsForValue().getBit(bloomFilterKey, offset);
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
		/*String value = "95cea659143842e3f787f96910cac2bb2f32d207";
		RedisBloomFilter filter = new RedisBloomFilter();
		System.out.println(filter.contains(value));
		filter.add(value);
		System.out.println(filter.contains(value));*/

	}
}