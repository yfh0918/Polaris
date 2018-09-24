<!DOCTYPE html>
<html>
<head>
  	<title>统一配置中心</title>
  	<#import "/common/common.macro.ftl" as netCommon>
	<@netCommon.commonStyle />
	<!-- DataTables -->
  	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.css">
  	<!-- daterangepicker -->
  	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/daterangepicker/daterangepicker-bs3.css">
</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && "off" == cookieMap["adminlte_settings"].value >sidebar-collapse</#if> ">
<div class="wrapper">
	<!-- header -->
	<@netCommon.commonHeader />
	<!-- left -->
	<@netCommon.commonLeft />
	
	<!-- Content Wrapper. Contains page content -->
	<div class="content-wrapper">
		<!-- Content Header (Page header) -->
		<section class="content-header">
			<h1>应用管理</h1>
		</section>

		<!-- Main content -->
	    <section class="content">
			
			<div class="row">
				<div class="col-xs-12">
					<div class="box">
					
						<div class="col-xs-3">
	                        <div class="input-group">
	                            <span class="input-group-addon">命名空间</span>
	                            <select class="form-control" id="namespace_id" >
									<#list namespaceList as namespace>
										<option value="${namespace}" >${namespace}</option>
									</#list>
	                            </select>
	                        </div>
	                    </div>
	                    
                        <div class="box-header">
                            <button class="btn btn-info btn-xs pull-left2 add" >+新增应用</button>
                        </div>
			            <div class="box-body">
			              	<table id="group_list" class="table table-bordered table-hover table-responsive table-condensed" width="100%" >
				                <thead>
					            	<tr>
                                        <th>应用名</th>
                                        <th>操作</th>
					                </tr>
				                </thead>
				                <tbody>
				                </tbody>
							</table>
						</div>
					</div>
				</div>
			</div>
	    </section>
	</div>

    <!-- 新增.模态框 -->
    <div class="modal fade" id="addModal" tabindex="-1" role="dialog"  aria-hidden="true">
        <div class="modal-dialog ">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title" >新增应用</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal form" role="form" >
                    	<div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">命名空间</label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="namespace" maxlength="100" readonly></div>
                        </div>
                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label">应用名<font color="red">*</font></label>
                            <div class="col-sm-10"><input type="text" class="form-control" name="group" placeholder="请输入名称" maxlength="100" ></div>
                        </div>
                        <hr>
                        <div class="form-group">
                            <div class="col-sm-offset-3 col-sm-6">
                                <button type="submit" class="btn btn-primary"  >保存</button>
                                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

</div>

<@netCommon.commonScript />
<!-- DataTables -->
<script src="${request.contextPath}/static/adminlte/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.min.js"></script>
<script src="${request.contextPath}/static/plugins/jquery/jquery.validate.min.js"></script>
<script src="${request.contextPath}/static/js/group.1.js"></script>
</body>
</html>
