package com.polaris.dubbo.filter;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;

import com.polaris.core.log.ExtendedLogger;
import com.polaris.dubbo.Constant;

@Activate(group = CommonConstants.CONSUMER)
public class ConsumerFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) {

    	//传递参数
    	RpcContext.getContext().setAttachments(Constant.getContext());
    	RpcContext.getContext().setAttachment(ExtendedLogger.PARENT_ID, ExtendedLogger.getModuleId());
    	
    	//服务调用
		return invoker.invoke(invocation);
    }
}
