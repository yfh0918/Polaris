package com.polaris.extension.sentinel;

import java.util.List;

import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.polaris.core.config.ConfClient;
import com.polaris.core.util.StringUtil;

/**
*
* 项目名称：Polaris_comm
* 类名称：MainSupport
* 类描述：
* 创建人：yufenghua
* 创建时间：2018年5月9日 上午8:55:18
* 修改人：yufenghua
* 修改时间：2018年5月9日 上午8:55:18
* 修改备注：
* @version
*
*/
public class NacosDataSourceInit {
	
	 public void init() throws Exception {

		// remoteAddress 代表 Nacos 服务端的地址
		// groupId 和 dataId 对应 Nacos 中相应配置
		String remoteAddress = ConfClient.getConfigRegistryAddress();
		String groupId = getGroup();
		
        // data source for FlowRule
		ReadableDataSource<String, List<FlowRule>> flowRuleDataSource = new NacosDataSource<>(remoteAddress, groupId, "FlowRule.json",
		    source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {}));
		FlowRuleManager.register2Property(flowRuleDataSource.getProperty());
		
        // data source for DegradeRule
		ReadableDataSource<String, List<DegradeRule>> degradeRuleDataSource = new NacosDataSource<>(remoteAddress, groupId, "DegradeRule.json",
			    source -> JSON.parseObject(source, new TypeReference<List<DegradeRule>>() {}));
		DegradeRuleManager.register2Property(degradeRuleDataSource.getProperty());
			
        // data source for SystemRule
		ReadableDataSource<String, List<SystemRule>> systemRuleDataSource = new NacosDataSource<>(remoteAddress, groupId, "SystemRule.json",
			    source -> JSON.parseObject(source, new TypeReference<List<SystemRule>>() {}));
		SystemRuleManager.register2Property(systemRuleDataSource.getProperty());
		
		// data source for ParamFlowRule
		ReadableDataSource<String, List<ParamFlowRule>> paramFlowRuleDataSource = new NacosDataSource<>(remoteAddress, groupId, "ParamFlowRule.json",
			    source -> JSON.parseObject(source, new TypeReference<List<ParamFlowRule>>() {}));
		ParamFlowRuleManager.register2Property(paramFlowRuleDataSource.getProperty());

	}
	 
	// 获取分组信息
	private String getGroup() {
		StringBuilder group = new StringBuilder();
		if (StringUtil.isNotEmpty(ConfClient.getGroup())) {
			group.append(ConfClient.getGroup());
			group.append(":");
		}
		group.append(ConfClient.getAppName());
		return group.toString();
	}
}
