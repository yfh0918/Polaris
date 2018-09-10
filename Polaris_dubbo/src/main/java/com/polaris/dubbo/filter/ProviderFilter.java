package com.polaris.dubbo.filter;

import java.util.Map;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
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
