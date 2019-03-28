package com.polaris.dubbo.filter;

import java.util.Map;

import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;
import com.polaris.dubbo.Constant;
import com.polaris.comm.util.StringUtil;

@Activate(group = Constants.PROVIDER)
public class ProviderFilter implements Filter {
	@Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
    	
        try {
        	
        	//传递参数
        	for (Map.Entry<String, String> entry : RpcContext.getContext().getAttachments().entrySet()) {
        		if (StringUtil.isNotEmpty(entry.getValue())) {
        			Constant.setContext(entry.getKey(), entry.getValue());
        		}
            }
            return invoker.invoke(invocation);
        } finally {
        	Constant.removeContext();
        }
    }
}
