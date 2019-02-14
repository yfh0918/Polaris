package com.polaris.gateway.request;

import org.springframework.stereotype.Service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author:Tom.Yu <p>
 * Description:
 * <p>
 * Token拦截
 */
@Service
public class TokenHttpRequestFilter extends HttpRequestFilter {


//	private static LogUtil logger = LogUtil.getInstance(TokenHttpRequestFilter.class);
//    private volatile static List<String> CHECK_CONTEXT = new ArrayList<>();
//    
//    private volatile static Map<String, List<String>> UNCHECKED_PATHS_MAP = new HashMap<>();
//    private volatile static Map<String, List<String>> UNCHECKED_PATHS_PREFIX_MAP = new HashMap<>();
//    private volatile static Map<String, List<String>> UNCHECKED_PATHS_CONTAIN_MAP = new HashMap<>();
//    private volatile static Map<String, List<String>> REPEATE_SUBMIT_CHECK_PATH_MAP = new HashMap<>();
//    
//    private volatile static String LOGIN_PATH;
//    private volatile static String USER_KEY;
//    
//    private volatile static int REPEATE_SUBMIT_SECONDES = 5;//重复提交的时间间隔
//    private final static String FILE_NAME = "token.txt";
//
//    static {
//		//先获取
//		loadFile(ConfClient.getConfigValue(FILE_NAME));
//		
//		//后监听
//    	ConfClient.addListener(FILE_NAME, new ConfListener() {
//			@Override
//			public void receive(String content) {
//				loadFile(content);
//			}
//    	});
//    }
//    
//    private static void loadFile(String content) {
//    	String[] contents = content.split(Constant.LINE_SEP);
//    	List<String> CHECK_CONTEXT_TEMP = new ArrayList<>();
//    	String LOGIN_PATH_TEMP = null;
//    	String USER_KEY_TMEP = null;
//    	List<String> UNCHECKED_PATHS_TEMP = new ArrayList<>();
//    	List<String> UNCHECKED_PATHS_PREFIX_TEMP = new ArrayList<>();
//    	List<String> UNCHECKED_PATHS_CONTAIN_TEMP = new ArrayList<>();
//    	List<String> REPEATE_SUBMIT_CHECK_PATH_TEMP = new ArrayList<>();
//    	int REPEATE_SUBMIT_SECONDES_TEMP = 5;
// 
//    	Map<String, List<String>> UNCHECKED_PATHS_MAP_TMEP = new HashMap<>();
//    	Map<String, List<String>> UNCHECKED_PATHS_PREFIX_MAP_TMEP = new HashMap<>();
//    	Map<String, List<String>> UNCHECKED_PATHS_CONTAIN_MAP_TMEP = new HashMap<>();
//    	Map<String, List<String>> REPEATE_SUBMIT_CHECK_PATH_MAP_TMEP = new HashMap<>();
//    	
//
//    	for (String conf : contents) {
//    		if (StringUtil.isNotEmpty(conf) && !conf.startsWith("#")) {
//    			conf = conf.replace("\n", "");
//    			conf = conf.replace("\r", "");
//
//				String[] kv = ConfigHandlerProvider.getKeyValue(conf);
//
//    			// 需要验证token的uri
//    			if (kv[0].equals("CHECK_CONTEXT")) {
//    				CHECK_CONTEXT_TEMP.add(kv[1]);
//    			}
//    			
//    			// 登录PATH
//    			if (kv[0].equals("LOGIN_PATH")) {
//    				LOGIN_PATH_TEMP = kv[1];
//    			}
//    			    			
//    			// User-key
//    			if (kv[0].equals("USER_KEY")) {
//    				USER_KEY_TMEP = kv[1];
//    			}
//
//    			// 不需要验证token的uri
//    			if (kv[0].equals("UNCHECKED_PATHS")) {
//    				UNCHECKED_PATHS_TEMP.add(kv[1]);
//    			}
//
//    			// 以xx开头放过的URL
//    			if (kv[0].equals("UNCHECKED_PATHS_PREFIX")) {
//    				UNCHECKED_PATHS_PREFIX_TEMP.add(kv[1]);
//    			}
//    			
//    			// 包含放过的URL
//    			if (kv[0].equals("UNCHECKED_PATHS_CONTAIN")) {
//    				UNCHECKED_PATHS_CONTAIN_TEMP.add(kv[1]);
//    			}
//    			
//				// 重复提交的URL
//				if (kv[0].equals("REPEATE_SUBMIT_CHECK_PATH")) {
//					REPEATE_SUBMIT_CHECK_PATH_TEMP.add(kv[1]);
//				}
//				
//				// 提交重复时间
//				if (kv[0].equals("REPEATE_SUBMIT_CHECK_TIME")) {
//					REPEATE_SUBMIT_SECONDES_TEMP = Integer.parseInt(kv[1]);
//				}
//    		}
//    	}
//    	
//    	// 数据类型归类
//    	for (String checkpath : CHECK_CONTEXT_TEMP) {
//    		
//    		// UNCHECKED_PATHS
//			UNCHECKED_PATHS_MAP_TMEP.put(checkpath, new ArrayList<>());
//			loadList(checkpath, UNCHECKED_PATHS_TEMP, UNCHECKED_PATHS_MAP_TMEP.get(checkpath));
//    		
//    		// UNCHECKED_PATHS_PREFIX
//			UNCHECKED_PATHS_PREFIX_MAP_TMEP.put(checkpath, new ArrayList<>());
//			loadList(checkpath, UNCHECKED_PATHS_PREFIX_TEMP, UNCHECKED_PATHS_PREFIX_MAP_TMEP.get(checkpath));
//			
//			// UNCHECKED_PATHS_CONTAIN
//			UNCHECKED_PATHS_CONTAIN_MAP_TMEP.put(checkpath, new ArrayList<>());
//			loadList(checkpath, UNCHECKED_PATHS_CONTAIN_TEMP, UNCHECKED_PATHS_CONTAIN_MAP_TMEP.get(checkpath));
//			
//    		// REPEATE_SUBMIT_CHECK_PATH
//			REPEATE_SUBMIT_CHECK_PATH_MAP_TMEP.put(checkpath, new ArrayList<>());
//			loadList(checkpath, REPEATE_SUBMIT_CHECK_PATH_TEMP, REPEATE_SUBMIT_CHECK_PATH_MAP_TMEP.get(checkpath));
//
//    	}
//    	CHECK_CONTEXT = CHECK_CONTEXT_TEMP;
//    	LOGIN_PATH = LOGIN_PATH_TEMP;
//    	USER_KEY = USER_KEY_TMEP;
//    	UNCHECKED_PATHS_CONTAIN_MAP = UNCHECKED_PATHS_CONTAIN_MAP_TMEP;
//    	UNCHECKED_PATHS_MAP = UNCHECKED_PATHS_MAP_TMEP;
//    	UNCHECKED_PATHS_PREFIX_MAP = UNCHECKED_PATHS_PREFIX_MAP_TMEP;
//    	REPEATE_SUBMIT_CHECK_PATH_MAP = REPEATE_SUBMIT_CHECK_PATH_MAP_TMEP;
//    	REPEATE_SUBMIT_SECONDES = REPEATE_SUBMIT_SECONDES_TEMP;
//    	
//    }
//    
//    private static void loadList(String checkpath, List<String> input, List<String> out) {
//		Iterator<String> it = input.iterator();
//		while(it.hasNext()){
//		    String v = it.next();
//		    if (v.startsWith(checkpath)) {
//		    	out.add(v);
//		    	it.remove();
//		    }
//		}
//    }
    


    @Override
    public boolean doFilter(HttpRequest originalRequest, HttpObject httpObject, ChannelHandlerContext channelHandlerContext) {
//        if (httpObject instanceof HttpRequest) {
//        	
//            //获取request
//            HttpRequest httpRequest = (HttpRequest) httpObject;
//
//            //从Cookie中取Token
//            String token = RequestUtil.getCookie(ConfClient.get("gateway.token.key"));
//            String userInfo = null;
//            if (StringUtil.isNotEmpty(token)) {
//            	// 当前用户已登录，放行
//                userInfo = AccessTokenUtil.getUserInfoByToken(token);
//                if (StringUtil.isNotEmpty(userInfo)) {
//            		httpRequest.headers().set(USER_KEY, userInfo);
//                }
//            }
//            
//            //获取url
//            String uri = httpRequest.uri();
//
//            //必须检查的路径
//            String checkpath = null;
//            for (int i0 = 0; i0 < CHECK_CONTEXT.size(); i0++) {
//                if (uri.startsWith(CHECK_CONTEXT.get(i0))) {
//                    checkpath = CHECK_CONTEXT.get(i0);
//                    break;
//                }
//            }
//            if (checkpath == null) {
//            	return false;
//            }
//
//            //获取url
//            String url;
//            int index = uri.indexOf("?");
//            if (index > 0) {
//                url = uri.substring(0, index);
//            } else {
//                url = uri;
//            }
//            
//            //放过
//            if (UNCHECKED_PATHS_MAP.get(checkpath).contains(url)) {
//                return false;
//            }
//            
//            //放过
//            List<String> list = UNCHECKED_PATHS_PREFIX_MAP.get(checkpath);
//            if (list != null) {
//            	for (int i0 = 0; i0 < list.size(); i0++) {
//                    if (url.startsWith(list.get(i0))) {
//                        return false;
//                    }
//                }
//            }
//            
//            //放过
//            list = UNCHECKED_PATHS_CONTAIN_MAP.get(checkpath);
//            if (list != null) {
//                for (int i0 = 0; i0 < list.size(); i0++) {
//                	String value = list.get(i0);
//                	value = value.substring(checkpath.length() + "**".length());
//                    if (url.contains(value)) {
//                        return false;
//                    }
//                }
//            }
//
//            //从Cookie中取Token
//            if (SysUtils.isObjectNull(token) || StringUtil.isEmpty(userInfo)) {
//                redirectLogin(httpRequest);
//                return false;
//            }
//
//            //重复提交(验证key)
//    		list = REPEATE_SUBMIT_CHECK_PATH_MAP.get(checkpath);
//    		if (list != null) {
//                for (int i0 = 0; i0 < list.size(); i0++) {
//                    if (uri.startsWith(list.get(0))) {
//                        String serverToken = RedisUtil.get(token);
//                        if (StringUtil.isNotEmpty(serverToken)) {
//                            logger.info("uri:{},"+USER_KEY+":{},cause:{}", list.get(0), userInfo, "重复提交");
//                            return true;
//                        }
//                        RedisUtil.set(token, UuidUtil.generateUuid(), REPEATE_SUBMIT_SECONDES);
//                        break;
//                    }
//                }
//    		}
//        }

        return false;
    }

//    private void redirectLogin(HttpRequest httpRequest) {
//        httpRequest.setUri(LOGIN_PATH);
//        HttpFilterAdapterImpl.replaceHost(httpRequest);
//    }
}

