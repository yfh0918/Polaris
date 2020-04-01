package com.polaris.container.dubbo.filter;

import java.util.HashMap;
import java.util.Map;

import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;

import com.polaris.core.GlobalContext;

@Activate(group = "consumer")
public class PolarisDubboConsumerFilter implements Filter {

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
	  	//传递参数
    	Map<String,Object> globalContext = GlobalContext.getContext();
    	Map<String, String> attachments = new HashMap<>();
    	for (Map.Entry<String, Object> entry : globalContext.entrySet()) {
    		if (!entry.getKey().equals(GlobalContext.PARENT_ID) &&
    			!entry.getKey().equals(GlobalContext.TRACE_ID) &&
    			!entry.getKey().equals(GlobalContext.MODULE_ID) &&
    			!entry.getKey().equals(GlobalContext.REQUEST) &&
    			!entry.getKey().equals(GlobalContext.RESPONSE)) {
    			attachments.put(entry.getKey(), entry.getValue().toString());
    		}
    	}
    	RpcContext.getContext().setAttachments(attachments);
    	RpcContext.getContext().setAttachment(GlobalContext.PARENT_ID, GlobalContext.getModuleId());
    	RpcContext.getContext().setAttachment(GlobalContext.TRACE_ID, GlobalContext.getTraceId());

    	return invoker.invoke(invocation);
	}

}
