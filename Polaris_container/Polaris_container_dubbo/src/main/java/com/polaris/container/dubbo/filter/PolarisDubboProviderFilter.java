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
import com.polaris.core.util.UuidUtil;

@Activate(group = "provider")
public class PolarisDubboProviderFilter implements Filter  {

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		try {
			Map<String, String> attachementMap = RpcContext.getContext().getAttachments();
			GlobalContext.setTraceId(attachementMap.remove(GlobalContext.TRACE_ID));
			GlobalContext.setParentId(attachementMap.remove(GlobalContext.SPAN_ID));
			GlobalContext.setSpanId(UuidUtil.generateUuid());
			return invoker.invoke(invocation);
		} finally {
			GlobalContext.removeContext();
		}
	}

}
