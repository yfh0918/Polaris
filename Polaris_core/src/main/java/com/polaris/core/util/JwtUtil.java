package com.polaris.core.util;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import com.polaris.core.config.ConfClient;

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
	
    /**
     * 用户登录成功后生成Jwt
     * 使用Hs256算法  私匙使用用户密码
     *
     * @param ttlMillis jwt过期时间
     * @param claims      登录成功的user对象
     * @return
     */
	public static String createJWT(Map<String, Object> user) {
		long ttlMillis = Long.parseLong(user.remove(JWT_TTL_MILLIS_KEY).toString());
		return createJWT(ttlMillis, user);
	}
	public static String createJWT(long ttlMillis, Map<String, Object> user) {
        //创建payload的私有声明（根据特定的业务需要添加，如果要拿这个做验证，一般是需要和jwt的接收方提前沟通好验证方式的）
        //生成签名的时候使用的秘钥secret,这个方法本地封装了的，一般可以从本地配置文件中读取，切记这个秘钥不能外露哦。它就是你服务端的私钥，在任何场景都不应该流露出去。一旦客户端得知这个secret, 那就意味着客户端是可以自我签发jwt了。
        String key = ConfClient.get("jwt.sign.key", RANDOM_UUID);
        return createJWT(ttlMillis, user, key);
	}
	public static String createJWT(Map<String, Object> user, String key) {
		long ttlMillis = Long.parseLong(user.remove(JWT_TTL_MILLIS_KEY).toString());
		return createJWT(ttlMillis, user, key);
	}
    public static String createJWT(long ttlMillis, Map<String, Object> user, String key) {
        //指定签名的时候使用的签名算法，也就是header那部分，jjwt已经将这部分内容封装好了。
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        //生成JWT的时间
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //生成签发人
        String subject = user.get("username").toString();

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
        String key = ConfClient.get("jwt.sign.key", RANDOM_UUID);
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
        String additionalValue = id + userMap.get("username") + num;
        return createAdditionalJWTMap(userMap, additionalKey, additionalValue, tokenTime);
	}
	public static Map<String, Object> createAdditionalJWTMap(Map<String, Object> userMap, String additionalKey, String additionalValue, long tokenTime) {
		EncryptUtil en = EncryptUtil.getInstance(ConfClient.get("jwt.additional.key", EncryptUtil.getDefaultKey()));
        try {
			userMap.put(additionalKey, en.encrypt(additionalValue));
		} catch (Exception e) {
			userMap.put(additionalKey, "");
		}
        createJWTMap(userMap, tokenTime);
        return userMap;
	}
	public static void createJWTMap(Map<String, Object> userMap, long tokenTime) {
		userMap.put(JWT_TTL_MILLIS_KEY, tokenTime);
	}
	
	
}
