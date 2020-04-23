package com.polaris.container.gateway.request;

import java.io.File;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import com.google.common.util.concurrent.RateLimiter;
import com.polaris.container.gateway.GatewayConstant;
import com.polaris.container.gateway.support.HttpRequestFilterSupport;
import com.polaris.core.Constant;
import com.polaris.core.config.ConfHandlerListener;
import com.polaris.core.config.Config.Type;
import com.polaris.core.config.provider.ConfHandlerProviderFactory;
import com.polaris.core.pojo.Result;
import com.polaris.core.thread.InheritableThreadLocalExecutor;
import com.polaris.core.util.PropertyUtil;
import com.polaris.core.util.StringUtil;
import com.polaris.extension.cache.Cache;
import com.polaris.extension.cache.CacheFactory;

import cn.hutool.core.io.FileUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author:Tom.Yu
 *
 * Description:
 * cc拦截
 */
/**
 * @author:Tom.Yu
 *
 * Description:
 * cc拦截
 */
public class CCHttpRequestFilter extends HttpRequestFilter {
	private static Logger logger = LoggerFactory.getLogger(CCHttpRequestFilter.class);
	private final static String FILE_NAME = "cc.txt";
	
    //控制总的流量
	public static volatile RateLimiter totalRateLimiter;
	public static volatile int int_all_rate = 0;
	public static volatile int int_all_timeout=30;//最大等待30秒返回
	
	//无需验证的IP
	public static volatile Set<String> ccSkipIp = new HashSet<>();
	
	//ip维度，每秒钟的访问数量
	public static volatile LoadingCache<String, AtomicInteger> secIploadingCache;
	public static volatile LoadingCache<String, AtomicInteger> minIploadingCache;
	public static volatile int[] int_ip_rate = {10,60};
	
	//被禁止的IP是否要持久化磁盘 
	public static volatile boolean isBlackIp = false;
	public static volatile Cache blackIpCache = CacheFactory.getCache("cc.black.ip");//被禁止的ip
	public static volatile Cache statisticsIpCache = CacheFactory.getCache("cc.statistics.ip");//统计用的缓存
	public static volatile Integer blockSeconds = 60;
	public static volatile boolean ipPersistent = false;
	public static volatile String ipSavePath = "";
	private static ThreadPoolExecutor threadPool = new InheritableThreadLocalExecutor(
            1, 1, 10, TimeUnit.SECONDS,
            new LinkedBlockingDeque<Runnable>(10000), //默认10000，超过放弃
            new ThreadPoolExecutor.AbortPolicy());
	private DateTimeFormatter dataFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private DateTimeFormatter dataFormat2=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
	
	static {
		
		//IP维度，每秒钟的访问数量
		secIploadingCache = CacheBuilder.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(1, TimeUnit.SECONDS)
                .build(new CacheLoader<String, AtomicInteger>() {
                    @Override
                    public AtomicInteger load(String key) throws Exception {
                    	return new AtomicInteger(0);
                    }
                });
		
		//IP维度，每秒钟的访问数量
		minIploadingCache = CacheBuilder.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build(new CacheLoader<String, AtomicInteger>() {
                    @Override
                    public AtomicInteger load(String key) throws Exception {
                    	return new AtomicInteger(0);
                    }
                });

		//先获取
		loadFile(ConfHandlerProviderFactory.get(Type.EXT).get(FILE_NAME));
		
		//后监听
		ConfHandlerProviderFactory.get(Type.EXT).listen(FILE_NAME, new ConfHandlerListener() {
			@Override
			public void receive(String content) {
				loadFile(content);
			}
    	});
    }
    
    private static void loadFile(String content) {
    	if (StringUtil.isEmpty(content)) {
    		logger.error(FILE_NAME + " is null");
    		return;
    	}
    	String[] contents = content.split(Constant.LINE_SEP);
    	int blockSecondsTemp = 60;
    	boolean isBlackIpTemp = false;
    	int[] IP_RATE = {10,60};
    	int ALL_RATE = 300;
    	int int_all_timeout_temp = 30;
    	boolean ipPersistentTemp = false;
    	String ipSavePathTemp = null;
    	
    	Set<String> tempCcSkipIp = new HashSet<>();
    	
    	for (String conf : contents) {
    		if (StringUtil.isNotEmpty(conf) && !conf.startsWith("#")) {
    			conf = conf.replace("\n", "");
    			conf = conf.replace("\r", "");

				String[] kv = PropertyUtil.getKeyValue(conf);
				// skip.ip
    			if (kv[0].equals("cc.skip.ip")) {
    				try {
    					String[] ips = kv[1].split(",");
    					for (String ip : ips) {
    						tempCcSkipIp.add(ip);
    					}
    				} catch (Exception ex) {
    				}
    			}
    			// ip.rate
    			if (kv[0].equals("cc.ip.rate")) {
    				try {
    					String[] rates = kv[1].split(",");
    					if (rates.length == 1) {
    						IP_RATE = new int[]{Integer.parseInt(rates[0]),60};
    					} else {
    						IP_RATE = new int[]{Integer.parseInt(rates[0]),Integer.parseInt(rates[1])};
    					}
    				} catch (Exception ex) {
    				}
    			}
    			// all.rate
    			if (kv[0].equals("cc.all.rate")) {
    				try {
    					ALL_RATE = Integer.parseInt(kv[1]);
    				} catch (Exception ex) {
    				}
    			}
    			// all.rate
    			if (kv[0].equals("cc.all.timeout")) {
    				try {
    					int_all_timeout_temp = Integer.parseInt(kv[1]);
    				} catch (Exception ex) {
    				}
    			}
    			// 被禁止IP的时间-seconds
    			if (kv[0].equals("cc.ip.block")) {
    				try {
    					isBlackIpTemp = Boolean.parseBoolean(kv[1]);
    				} catch (Exception ex) {
    				}
    			}
    			if (kv[0].equals("cc.ip.block.seconds")) {
    				try {
        				blockSecondsTemp = Integer.parseInt(kv[1]);
    				} catch (Exception ex) {
    				}
    			}
    			
    			// 被禁止IP的是否持久化
    			if (kv[0].equals("cc.ip.persistent")) {
    				try {
    					ipPersistentTemp = Boolean.parseBoolean(kv[1]);
    				} catch (Exception ex) {
    				}
    			}
    			
    			// 持久化地址
    			if (kv[0].equals("cc.ip.persistent.path")) {
    				ipSavePathTemp = kv[1];
    			}

    		}
    	}
    	
    	//无需验证的IP
    	ccSkipIp = tempCcSkipIp;
    	
    	//总访问量
    	if (int_all_rate != ALL_RATE) {
    		int_all_rate = ALL_RATE;
    		totalRateLimiter = RateLimiter.create(int_all_rate);//控制总访问量
    	}
    	int_all_timeout = int_all_timeout_temp;
		
		//IP地址维度的
		int_ip_rate = IP_RATE;//单个IP的访问访问量
		
		//被限制IP的访问时间
		isBlackIp = isBlackIpTemp;
        blockSeconds = blockSecondsTemp;
        ipPersistent = ipPersistentTemp;
        ipSavePath = ipSavePathTemp;
    }
    
	@Override
    public boolean doFilter(HttpRequest originalRequest, HttpObject httpObject, ChannelHandlerContext channelHandlerContext) {
        if (httpObject instanceof HttpRequest) {
            logger.debug("filter:{}", this.getClass().getName());
            String realIp = GatewayConstant.getRealIp((DefaultHttpRequest) httpObject);
            
            //控制总流量，超标直接返回
            HttpRequest httpRequest = (HttpRequest)httpObject;
            String url = getUrl(httpRequest);
            
            //view black ip list
            if (viewBlackLog(url)) {
            	return true;
            }
            
            //判断是否是无需验证的IP
            if (ccSkipIp.contains(realIp)) {
            	return false;
            }
            
        	//是否黑名单
        	if (isBlackIp && blackIpCache.get(realIp) != null){
        		String message = realIp + " access has exceeded ";
            	this.setResult(HttpRequestFilterSupport.createResultDto(Constant.RESULT_FAIL,message).toJSONString());
                hackLog(logger, realIp, "cc", message);
        		return true;
        	}

        	//cc攻击
            if (ccHack(url, realIp)) {
            	String message = httpRequest.uri() + " " + realIp + " access  has exceeded ";
            	this.setResult(HttpRequestFilterSupport.createResultDto(Constant.RESULT_FAIL,message).toJSONString());
                hackLog(logger, realIp, "cc", message);
            	return true;
            }
            
            //统计
            saveStatisticsLog(url, realIp);
            
        }
        return false;
    }
	
	private boolean viewBlackLog(String url) {
        if (url.equals("/gateway/cc/ip")) {
        	@SuppressWarnings("rawtypes")
			Result<List> dto = new Result<>();
        	dto.setCode(Constant.RESULT_SUCCESS);
    		List<String> dataList = new ArrayList<>();
        	try {
            	for (Object key : blackIpCache.getKeys()) {
            		Object obj = blackIpCache.get(key);
            		if (obj != null) {
            			dataList.add(key + " " +obj.toString());
            		}
            	}
        	} catch (Exception ex) {}
        	dto.setData(dataList);
        	this.setResult(dto.toJSONString());
        	return true;
        }
        return false;
	}
	
	public boolean doSentinel(String url, String realIp) {
    	Entry entry = null;
    	try {
            SphU.entry(url, EntryType.IN, 1, realIp);
            return false;
        } catch (BlockException e) {
        	
        	//热点参数目前只有IP维度限流，限流的场合直接加入黑名单
        	if (e instanceof ParamFlowException) {
        		saveBlackLog(realIp,"sentinel block "+((ParamFlowException)e).getLimitParam());//拒绝
        	}
        	return true;
        } finally {
        	if (entry != null) {
                entry.exit(1,realIp);
            }
        }
    }
    
    public static String getUrl(HttpRequest httpRequest) {
    	//获取url
        String uri = httpRequest.uri();
        String url;
        int index = uri.indexOf("?");
        if (index > 0) {
            url = uri.substring(0, index);
        } else {
            url = uri;
        }
        return url;
    }
    
    public boolean ccHack(String url, String realIp) {
       	
    	//IP每秒访问
		try {
			AtomicInteger secRateLimiter = (AtomicInteger) secIploadingCache.get(realIp);
	        int count = secRateLimiter.incrementAndGet();
	        if (count > int_ip_rate[0]) {
	        	saveBlackLog(realIp, count + " visits per second");//拒绝
	    		return true;//拒绝
	        } 
		} catch (ExecutionException e) {
			logger.error(e.toString());
        	return true;
		}
		
		//IP每分访问
		try {
	        AtomicInteger minRateLimiter = (AtomicInteger) minIploadingCache.get(realIp);
	        int count = minRateLimiter.incrementAndGet();
	        if (count > int_ip_rate[1]) {
	        	saveBlackLog(realIp, count + " visits per minute");//拒绝
	    		return true;//拒绝
	        } 
		} catch (ExecutionException e) {
			logger.error(e.toString());
        	return true;
		}
		
        //总量控制
		try {
	        if (!totalRateLimiter.tryAcquire(1, int_all_timeout, TimeUnit.SECONDS)) {
	            return true;
	        }
		} catch (Exception ex) {
			logger.error(ex.toString());
        	return true;
		}
		
		//sentinel控制
		if (doSentinel(url, realIp)) {
			return true;
		}

        return false;
    }
    
    public void saveStatisticsLog(String url, String realIp) {
    	if (ipPersistent && StringUtil.isNotEmpty(ipSavePath)) {
    		try {
    			threadPool.execute(new Runnable() {
    				@Override
    				public void run() {
    					LocalDateTime time = LocalDateTime.now();
    		    		String fileNamePrefix = dataFormat.format(time);
    					String path = ipSavePath + File.separator + fileNamePrefix+"_statistics.txt";
    					try {
							Files.asCharSink(FileUtil.touch(path), Charset.defaultCharset(), FileWriteMode.APPEND).write(dataFormat2.format(time) + " " + realIp + " " +url);
							Files.asCharSink(FileUtil.touch(path), Charset.defaultCharset(), FileWriteMode.APPEND).write(FileUtil.getLineSeparator());
    					} catch (Exception e) {
							logger.error(e.getMessage());
						} 
    				}
    			});
    		} catch (Exception ex) {
    			logger.error("Error:",ex);
    		}
		}
    }
    
    public void saveBlackLog(String realIp, String reason) {
    	if (isBlackIp) {
    		logger.info("ip:{} is blocked ,caused by:{}",realIp, reason);
    		blackIpCache.put(realIp, reason ,blockSeconds);//拒绝
    		
    		//持久化
    		if (ipPersistent && StringUtil.isNotEmpty(ipSavePath)) {
    			try {
    				threadPool.execute(new Runnable() {
    					@Override
    					public void run() {
    						LocalDateTime time = LocalDateTime.now();
    		    			String fileNamePrefix = dataFormat.format(time);
    						String path = ipSavePath + File.separator + fileNamePrefix+"_black.txt";
    						try {
								Files.asCharSink(FileUtil.touch(path), Charset.defaultCharset(), FileWriteMode.APPEND).write(dataFormat2.format(time) + " " + realIp + " " +reason);
								Files.asCharSink(FileUtil.touch(path), Charset.defaultCharset(), FileWriteMode.APPEND).write(FileUtil.getLineSeparator());
							} catch (Exception e) {
								logger.error(e.getMessage());
							} 
    					}
        			});
    			}catch (Exception ex) {
        			logger.error("Error:",ex);
        		}
    			
    		}
    	}
    }
}


