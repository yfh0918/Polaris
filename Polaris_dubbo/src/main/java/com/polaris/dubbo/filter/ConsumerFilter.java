package com.polaris.dubbo.filter;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.polaris.dubbo.Constant;
import com.polaris.comm.util.LogUtil;

@Activate(group = Constants.CONSUMER)
public class ConsumerFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) {

    	//传递参数
    	RpcContext.getContext().setAttachments(Constant.getContext());
    	RpcContext.getContext().setAttachment(LogUtil.PARENT_ID, LogUtil.getModuleId());
    	
    	//服务调用
		return invoker.invoke(invocation);
    }
}
