package com.polaris.sentinel;

import java.io.File;
import java.util.List;

import com.alibaba.csp.sentinel.datasource.FileRefreshableDataSource;
import com.alibaba.csp.sentinel.datasource.FileWritableDataSource;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.WritableDataSource;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import com.alibaba.csp.sentinel.transport.util.WritableDataSourceRegistry;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.polaris.core.util.PropertyUtils;

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
public class FileDataSourceInit {
	
	 public void init() throws Exception {

		//创建文件
        String rulePath = PropertyUtils.getAppPath() +File.separator + "sentinel" +File.separator + "rule";
        File folder = new File(rulePath);
        folder.mkdirs();
        File flow = new File(rulePath + File.separator + "FlowRule.json");
        if (!flow.exists()) {
        	flow.createNewFile();
        }

        File degrade = new File(rulePath + File.separator + "DegradeRule.json");
        if (!degrade.exists()) {
        	degrade.createNewFile();
        }

        File system = new File(rulePath + File.separator + "SystemRule.json");
        if (!system.exists()) {
        	system.createNewFile();
        }

        // data source for FlowRule
        ReadableDataSource<String, List<FlowRule>> flowRuleDataSource = new FileRefreshableDataSource<>(
        	flow, source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {})
        );
        FlowRuleManager.register2Property(flowRuleDataSource.getProperty());
        WritableDataSource<List<FlowRule>> flowRuleWriteDataSource = new FileWritableDataSource<>(flow, source -> JSON.toJSONString(source));
        WritableDataSourceRegistry.registerFlowDataSource(flowRuleWriteDataSource);
        
        // data source for DegradeRule
        ReadableDataSource<String, List<DegradeRule>> degradeRuleDataSource = new FileRefreshableDataSource<>(
        	degrade, source -> JSON.parseObject(source, new TypeReference<List<DegradeRule>>() {})
        );
        DegradeRuleManager.register2Property(degradeRuleDataSource.getProperty());
        WritableDataSource<List<DegradeRule>> degradeRuleWriteDataSource = new FileWritableDataSource<>(degrade, source -> JSON.toJSONString(source));
        WritableDataSourceRegistry.registerDegradeDataSource(degradeRuleWriteDataSource);


        // data source for SystemRule
        ReadableDataSource<String, List<SystemRule>> systemRuleDataSource = new FileRefreshableDataSource<>(
        		system, source -> JSON.parseObject(source, new TypeReference<List<SystemRule>>() {})
        );
        SystemRuleManager.register2Property(systemRuleDataSource.getProperty());
        WritableDataSource<List<SystemRule>> systemRuleWriteDataSource = new FileWritableDataSource<>(system, source -> JSON.toJSONString(source));
        WritableDataSourceRegistry.registerSystemDataSource(systemRuleWriteDataSource);
	}
}
