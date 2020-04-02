package com.polaris.container.dubbo.filter;

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
    	RpcContext.getContext().setAttachment(GlobalContext.TRACE_ID, GlobalContext.getTraceId());
    	RpcContext.getContext().setAttachment(GlobalContext.SPAN_ID, GlobalContext.getSpanId());
    	return invoker.invoke(invocation);
	}

}
