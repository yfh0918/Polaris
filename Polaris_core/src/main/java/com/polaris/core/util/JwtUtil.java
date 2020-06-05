package com.polaris.core.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import com.polaris.core.Constant;
import com.polaris.core.config.ConfClient;
import com.polaris.core.util.EncryptUtil.Type;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * @Author:jimisun
 * @Description:
 * @Date:Created in 14:08 2018/8/15
 * @Modified By:
 */
public class JwtUtil {

	private final static String RANDOM_UUID = UUID.randomUUID().toString();
	private final static String JWT_TTL_MILLIS_KEY = "jwtTtlMillis";
	public static String JWT_KEY = "jwtKey";
	public static final String CLAIMS_KEY = "ClaimsKey";
	private static final String JWT_SIGN_KEY = "jwt.sign.key";
	private static final String JWT_ADDITIONAL_KEY = "jwt.additional.key";
	public static String USER_NAME = "username";
	private static String EMPTY = "";
	
    /**
     * 用户登录成功后生成Jwt
     * 使用Hs256算法  私匙使用用户密码
     *
     * @param ttlMillis jwt过期时间
     * @param claims      登录成功的user对象
     * @return
     */
	public static String createJWT(Map<String, Object> user) {
		return createJWT(user,SignatureAlgorithm.HS256);
	}
	public static String createJWT(Map<String, Object> user,SignatureAlgorithm signatureAlgorithm) {
		long ttlMillis = Long.parseLong(user.remove(JWT_TTL_MILLIS_KEY).toString());
		return createJWT(ttlMillis, user, signatureAlgorithm);
	}
	public static String createJWT(long ttlMillis, Map<String, Object> user) {
        return createJWT(ttlMillis, user, SignatureAlgorithm.HS256);
	}
	public static String createJWT(long ttlMillis, Map<String, Object> user,SignatureAlgorithm signatureAlgorithm) {
        String key = ConfClient.get(JWT_SIGN_KEY, RANDOM_UUID);
        return createJWT(ttlMillis, user, key,signatureAlgorithm);
	}
	public static String createJWT(Map<String, Object> user, String key) {
		return createJWT(user, key,SignatureAlgorithm.HS256);
	}
	public static String createJWT(Map<String, Object> user, String key,SignatureAlgorithm signatureAlgorithm) {
		long ttlMillis = Long.parseLong(user.remove(JWT_TTL_MILLIS_KEY).toString());
		return createJWT(ttlMillis, user, key,signatureAlgorithm);
	}
	public static String createJWT(long ttlMillis, Map<String, Object> user, String key) {
		return createJWT(ttlMillis,user,key,SignatureAlgorithm.HS256);
	}
    public static String createJWT(long ttlMillis, Map<String, Object> user, String key,SignatureAlgorithm signatureAlgorithm) {

        //生成JWT的时间
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //生成签发人
        String subject = user.get(USER_NAME).toString();

        //下面就是在为payload添加各种标准声明和私有声明了
        //这里其实就是new一个JwtBuilder，设置jwt的body
        JwtBuilder builder = Jwts.builder()
                //如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                .setClaims(user)
                //设置jti(JWT ID)：是JWT的唯一标识，根据业务需要，这个可以设置为一个不重复的值，主要用来作为一次性token,从而回避重放攻击。
                .setId(UUID.randomUUID().toString())
                //iat: jwt的签发时间
                .setIssuedAt(now)
                //代表这个JWT的主体，即它的所有人，这个是一个json格式的字符串，可以存放什么userid，roldid之类的，作为什么用户的唯一标志。
                .setSubject(subject)
                //设置签名使用的签名算法和签名使用的秘钥
                .signWith(signatureAlgorithm, key);
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            //设置过期时间
            builder.setExpiration(exp);
        }
        return builder.compact();
    }


    /**
     * Token的解密
     * @param token 加密后的token
     * @param user  用户的对象
     * @return
     */
    public static Claims parseJWT(String token) {
        //签名秘钥，和生成的签名的秘钥一模一样
        String key = ConfClient.get(JWT_SIGN_KEY, RANDOM_UUID);
        return parseJWT(token, key);
    }
    public static Claims parseJWT(String token, String key) {

        //得到DefaultJwtParser
        Claims claims = Jwts.parser()
                //设置签名的秘钥
                .setSigningKey(key)
                //设置需要解析的jwt
                .parseClaimsJws(token).getBody();
        return claims;
    }

	//获取含有附加信息的jwt
	public static Map<String, Object> createAdditionalJWTMap(Map<String, Object> userMap, String additionalKey, String id, String num, long tokenTime) {
        String additionalValue = id + userMap.get(USER_NAME) + num;
        return createAdditionalJWTMap(userMap, additionalKey, additionalValue, tokenTime);
	}
	public static Map<String, Object> createAdditionalJWTMap(Map<String, Object> userMap, String additionalKey, String additionalValue, long tokenTime) {
		EncryptUtil en = EncryptUtil.getInstance(ConfClient.get(JWT_ADDITIONAL_KEY, EncryptUtil.getDefaultKey()), Type.DES);
        try {
			userMap.put(additionalKey, en.encrypt(additionalValue));
		} catch (Exception e) {
			userMap.put(additionalKey, EMPTY);
		}
        createJWTMap(userMap, tokenTime);
        return userMap;
	}
	public static void createJWTMap(Map<String, Object> userMap, long tokenTime) {
		userMap.put(JWT_TTL_MILLIS_KEY, tokenTime);
	}
	public static String encode(Map<String, Object> jwtMap) {
		String jwtInfo = JacksonUtil.toJson(jwtMap);
    	try {
        	return URLEncoder.encode(jwtInfo,Constant.UTF_CODE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	return jwtInfo;
	}
	public static String decode(String jwtInfo) {
    	try {
        	jwtInfo = URLDecoder.decode(jwtInfo,Constant.UTF_CODE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	return jwtInfo;
	}
	
}
