<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>统一配置中心</title>

<#import "/common/common.macro.ftl" as netCommon>
<@netCommon.commonStyle />
<link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.css">

</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && "off" == cookieMap["adminlte_settings"].value >sidebar-collapse</#if> ">
	<div class="wrapper">
		
		<@netCommon.commonHeader />

		<@netCommon.commonLeft />

		<!-- Content Wrapper. Contains page content -->
		<div class="content-wrapper">
			<!-- Content Header (Page header) -->
			<section class="content-header">
				<h1>配置管理 <small></small></h1>
			</section>

			<!-- Main content -->
			<section class="content">
			
                <div class="row">
                    <div class="col-xs-3">
                        <div class="input-group">
                            <span class="input-group-addon">ZK</span>
                            <select class="form-control" id="nodeZK" >
								<#list ConfNodeZK as zk>
									<option value="${zk.zkName}" >${zk.zkName}</option>
								</#list>
                            </select>
                        </div>
                    </div>
                    <div class="col-xs-3">
                        <div class="input-group">
                            <span class="input-group-addon">应用</span>
                            <select class="form-control" id="nodeGroup" >
								<#list ConfNodeGroup as group>
									<option value="${group.groupName}" >${group.groupTitle}</option>
								</#list>
                            </select>
                        </div>
                    </div>
                    <div class="col-xs-4">
                        <div class="input-group">
                            <span class="input-group-addon">KEY</span>
                            <input type="text" class="form-control" id="nodeKey"  placeholder="支持使用%进行模糊查询" value="${nodeKey}" autocomplete="on" >
                        </div>
                    </div>
                    
                </div>
                
                <div class="row">

                    <div class="col-xs-1">
                        <button class="btn btn-block btn-info" id="searchBtn">搜索</button>
                    </div>
                    <div class="col-xs-2">
                        <button class="btn btn-block btn-success" id="add" type="button">新增配置</button>
                    </div>
                    <div class="col-xs-2">
                        <button class="btn btn-block btn-success" id="synBtn" type="button">同步配置</button>
                    </div>
                    
                    <div class="col-xs-2">
                        <button class="btn btn-block btn-success" id="copy" type="button">复制配置</button>
                    </div>

                </div>
                
				<!-- 全部配置 -->
				<div class="box box-info2">
	                <div class="box-body">
	                  	<table id="conf_list" class="table table-bordered table-hover table-responsive table-condensed" style="word-break:break-all; word-wrap:break-all; width: 100%;">
		                    <thead>
		                      	<tr>
                                    <th>ZK</th>
                                    <th>GROUP</th>
                                    <th>KEY</th>
			                        <th>GROUP_KEY</th>
			                        <th>VALUE</th>
			                        <th>VALUE(zk)</th>
			                        <th>描述</th>
			                        <th>操作</th>
		                      	</tr>
							</thead>
		                    <tbody>
		                    	<#if fileterData?exists>
		                    		<#list fileterData as item>
		                    			<tr>
					                        <td>${item.nodeKey}</td>
					                        <td <#if item.znodeValue != item.znodeValueReal>style="color:red;font: italic bold"</#if> >${item.znodeValue}</td>
					                        <td <#if item.znodeValue != item.znodeValueReal>style="color:red;font: italic bold"</#if> >${item.znodeValueReal}</td>
					                        <td style="width: 30%">${item.znodeDesc}</td>
					                        <td>
					                        <#if item.delFlg == '0'>
					                        	<div class="input-group">
						                      		<button class="btn btn-primary btn-xs update" type="button" nodeKey="${item.nodeKey}" znodeValue="${item.znodeValue}" znodeDesc="${item.znodeDesc}" >更新</button>&nbsp;
						                      		<button class="btn btn-danger btn-xs delete" type="button" nodeKey="${item.nodeKey}">删除</button>
					                        	</div>
					                        </#if>
					                        <#if item.delFlg == '1'>
					                        	<div class="input-group">
						                      		<button class="btn btn-danger btn-xs recovery" type="button" nodeKey="${item.nodeKey}">恢复</button>
					                        	</div>
					                        </#if>
					                        </td>
				                      	</tr>
		                    		</#list>
		                    	</#if>
		                    </tbody>
	                  	</table>
					</div><!-- /.box-body -->
				</div><!-- /.box -->
				
			</section>
			<!-- /.content -->
			
		</div>
		<!-- /.content-wrapper -->

	</div>
	<!-- ./wrapper -->
	
	<!-- 新增.模态框 -->
	<div class="modal fade" id="addModal" tabindex="-1" role="dialog"  aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
	            	<h4 class="modal-title" >新增配置</h4>
	         	</div>
	         	<div class="modal-body">
					<form class="form-horizontal form" role="form" >
                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">ZK名称</label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="nodeZK" placeholder="请输入ZK名称" maxlength="100" readonly></div>
                        </div>
                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">应用</label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="nodeGroup" placeholder="请输入应用" maxlength="100" readonly></div>
                        </div>
						<div class="form-group">
							<label for="firstname" class="col-sm-2 control-label">KEY</label>
							<div class="col-sm-10"><input type="text" class="form-control" name="nodeKey" placeholder="请输入KEY" maxlength="100" ></div>
						</div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label">VALUE</label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="nodeValue" placeholder="请输入VALUE" maxlength="1000" ></div>
                        </div>
						<div class="form-group">
							<label for="lastname" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10">
                                <textarea class="textarea" name="nodeDesc" maxlength="512" placeholder="请输入描述" style="width: 100%; height: 100px; font-size: 14px; line-height: 18px; border: 1px solid #dddddd; padding: 10px;"></textarea>
							</div>
						</div>
						<div class="form-group">
							<div class="col-sm-offset-2 col-sm-10">
								<button type="submit" class="btn btn-primary"  >保存</button>
								<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
							</div>
						</div>
					</form>
	         	</div>
			</div>
		</div>
	</div>
	
	<!-- 更新.模态框 -->
	<div class="modal fade" id="updateModal" tabindex="-1" role="dialog"  aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
	            	<h4 class="modal-title" >更新配置</h4>
	         	</div>
	         	<div class="modal-body">
					<form class="form-horizontal form" role="form" >
                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">ZK名称</label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="nodeZK" placeholder="请输入ZK名称" maxlength="100" readonly></div>
                        </div>
                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">应用</label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="nodeGroup" placeholder="请输入应用" maxlength="100" readonly></div>
                        </div>
                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">KEY</label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="nodeKey" placeholder="请输入KEY" maxlength="100" readonly></div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label">VALUE</label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="nodeValue" placeholder="请输入VALUE" maxlength="1000" ></div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label">描述</label>
                            <div class="col-sm-10">
                                <textarea class="textarea" name="nodeDesc" maxlength="512" placeholder="请输入描述" style="width: 100%; height: 100px; font-size: 14px; line-height: 18px; border: 1px solid #dddddd; padding: 10px;"></textarea>
                            </div>
                        </div>
						<div class="form-group">
							<div class="col-sm-offset-2 col-sm-10">
								<button type="submit" class="btn btn-primary"  >更新</button>
								<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
							</div>
						</div>
					</form>
	         	</div>
			</div>
		</div>
	</div>

	<!-- 复制.模态框 -->
	<div class="modal fade" id="copyModal" tabindex="-1" role="dialog"  aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
	            	<h4 class="modal-title" >复制配置</h4>
	         	</div>
	         	<div class="modal-body">
					<form class="form-horizontal form" role="form" >
                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">ZK名称</label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="nodeZK" placeholder="请输入ZK名称" maxlength="100" readonly></div>
                        </div>
                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">应用</label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="nodeGroup" placeholder="请输入应用" maxlength="100" readonly></div>
                        </div>
						<div class="form-group">
							<label for="firstname" class="col-sm-2 control-label">复制源</label>
							<div class="col-sm-10"><input type="text" class="form-control" name="nodeGroupCopy" placeholder="请输入复制源" maxlength="100" ></div>
						</div>

						<div class="form-group">
							<div class="col-sm-offset-2 col-sm-10">
								<button type="submit" class="btn btn-primary"  >复制</button>
								<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
							</div>
						</div>
					</form>
	         	</div>
			</div>
		</div>
	</div>
	<script>
		var base_url = '${request.contextPath}';
	</script>
	<@netCommon.commonScript/>
    <script src="${request.contextPath}/static/adminlte/plugins/datatables/jquery.dataTables.min.js"></script>
    <script src="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.min.js"></script>
    <script src="${request.contextPath}/static/plugins/jquery/jquery.validate.min.js"></script>
    <script src="${request.contextPath}/static/js/conf.1.js"></script>
	
</body>
</html>
