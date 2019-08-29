/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.polaris.dubbo.filter;

import java.util.Map;

import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.polaris.core.GlobalContext;
import com.polaris.core.util.StringUtil;
import com.polaris.dubbo.config.DubboConfig;
import com.polaris.dubbo.fallback.DubboFallbackRegistry;

/**
 * <p>Dubbo service provider filter for Sentinel. Auto activated by default.</p>
 *
 * If you want to disable the provider filter, you can configure:
 * <pre>
 * &lt;dubbo:provider filter="-sentinel.dubbo.provider.filter"/&gt;
 * </pre>
 *
 * @author leyou
 * @author Eric Zhao
 */
@Activate(group = "provider")
public class SentinelDubboProviderFilter extends AbstractDubboFilter implements Filter {

    public SentinelDubboProviderFilter() {
        RecordLog.info("Sentinel Dubbo provider filter initialized");
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
    	
    	//传递参数
    	for (Map.Entry<String, String> entry : RpcContext.getContext().getAttachments().entrySet()) {
    		if (StringUtil.isNotEmpty(entry.getValue())) {
    			GlobalContext.setContext(entry.getKey(), entry.getValue());
    		}
        }
    	
        // Get origin caller.
        String application = DubboUtils.getApplication(invocation, "");

        Entry interfaceEntry = null;
        Entry methodEntry = null;
        try {
            String resourceName = getResourceName(invoker, invocation, DubboConfig.getDubboProviderPrefix());
            String interfaceName = invoker.getInterface().getName();
            ContextUtil.enter(resourceName, application);
            interfaceEntry = SphU.entry(interfaceName, EntryType.IN);
            methodEntry = SphU.entry(resourceName, EntryType.IN, 1, invocation.getArguments());

            Result result = invoker.invoke(invocation);
            if (result.hasException()) {
                Tracer.trace(result.getException());
            }
            return result;
        } catch (BlockException e) {
            return DubboFallbackRegistry.getProviderFallback().handle(invoker, invocation, e);
        } catch (RpcException e) {
            Tracer.trace(e);
            throw e;
        } finally {
            if (methodEntry != null) {
                methodEntry.exit(1, invocation.getArguments());
            }
            if (interfaceEntry != null) {
                interfaceEntry.exit();
            }
            ContextUtil.exit();
        }
    }
}
