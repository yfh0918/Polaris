package com.polaris.container.dubbo.filter;

import java.util.Map;

import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;

import com.polaris.core.GlobalContext;
import com.polaris.core.util.StringUtil;

@Activate(group = "provider")
public class PolarisDubboProviderFilter implements Filter  {

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
    	for (Map.Entry<String, String> entry : RpcContext.getContext().getAttachments().entrySet()) {
    		if (StringUtil.isNotEmpty(entry.getValue())) {
    			GlobalContext.setContext(entry.getKey(), entry.getValue());
    		}
        }
		return invoker.invoke(invocation);
	}

}
