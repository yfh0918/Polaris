package com.polaris.core.naming.request;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.polaris.core.Constant;
import com.polaris.core.exception.NamingException;
import com.polaris.core.naming.NamingClient;
import com.polaris.core.pojo.ServerHost;
import com.polaris.core.util.StringUtil;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NamingRequest {
    String protocol() default ServerHost.HTTP;
    String group() default "";
    String value();
    String context() default "";
    
    public static class Convert {
        public static String url(NamingRequest request) {
            if (request == null) {
                throw new NamingException("NamingRequest is not setted");
            }
            StringBuilder strB = new StringBuilder();
            strB.append(request.protocol());
            strB.append(ServerHost.DOUBLE_SLASH);
            if (StringUtil.isNotEmpty(request.group())) {
                strB.append(request.group() + Constant.SERVICE_INFO_SPLITER);
            }
            strB.append(NamingClient.getServer(request.value()));
            if (StringUtil.isNotEmpty(request.context())) {
                if (!request.context().startsWith(ServerHost.SLASH)) {
                    strB.append(ServerHost.SLASH);
                }
                strB.append(request.context());
            }
            return strB.toString();
        }
    }
}
