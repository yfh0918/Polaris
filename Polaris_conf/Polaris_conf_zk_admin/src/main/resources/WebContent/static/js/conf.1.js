$(function(){

	var synFlg = '0';//同步标志
	
	// init date tables
	var confTable = $("#conf_list").dataTable({
		"paging": false, // 禁止分页
		"deferRender": true,
		"processing" : true,
		"serverSide": true,
		"ajax": {
			url: base_url + "/conf/findList",
			type:"post",
			data : function ( d ) {
				var obj = {};
				synFlg = '0';
				obj.nodeZK = $('#nodeZK').val();
				obj.nodeGroup = $('#nodeGroup').val();
				obj.nodeKey = $('#nodeKey').val();
				return obj;
			}
		},
		"searching": false,
		"ordering": false,
		//"scrollX": true,	// X轴滚动条，取消自适应
		"columns": [
			{ "data": 'nodeZK', "visible" : false},
			{ "data": 'nodeGroup', "visible" : false},
			{ "data": 'nodeKey', "visible" : true},
			{ "data": 'groupKey', "visible" : false},
			{
				"data": 'nodeValue',
				"visible" : true,
				"render": function ( data, type, row ) {
					var temp =  row.nodeValue;
					var nodeValueR = row.nodeValueReal;
					if (row.delFlg == '1') {
						synFlg = '1';
						var html = "<span style='color: red'>"+ temp +"(删除未同步)</span>";
						return html;
					} else if (row.nodeValue == nodeValueR) {
						return "<span title='"+ row.nodeValue +"'>"+ temp +"</span>";;
					} else if (nodeValueR != null){
						synFlg = '1';
						var html = "<span style='color: red'>"+ temp +"(更新未同步)</span>";
						return html;
					} else {
						synFlg = '1';
						var html = "<span style='color: red'>"+ temp +"(新增未同步)</span>";
						return html;
					}
				}
			},
			{ "data": 'nodeValueReal', "visible" : false},
			{ "data": 'nodeDesc', "visible" : true},
			{ "data": '操作' ,
				"render": function ( data, type, row ) {
					return function(){
						// html
						var html = '';
                        var nodeValue = row.nodeValue?row.nodeValue:'';
                        var nodeValueReal = row.nodeValueReal?row.nodeValueReal:'';
                        var nodeDesc = row.nodeDesc?row.nodeDesc:'';
						if (row.delFlg == '1') {
							html = '<p id="'+ row.id +'" '+
							' nodeZK="'+ row.nodeZK +'" '+
							' nodeGroup="'+ row.nodeGroup +'" '+
							' nodeKey="'+ row.nodeKey +'" '+
							' nodeValue="'+ nodeValue +'" '+
							' nodeValueReal="'+ nodeValueReal +'" '+
							' nodeValue="'+ nodeValue +'" '+
							'>'+
							'<textarea name="nodeDesc" style="display:none;" >'+ nodeDesc +'</textarea>  '+
							'<button class="btn btn-warning btn-xs recovery" type="button">恢复</button>  '+
							'</p>';
						} else {
							html = '<p id="'+ row.id +'" '+
							' nodeZK="'+ row.nodeZK +'" '+
							' nodeGroup="'+ row.nodeGroup +'" '+
							' nodeKey="'+ row.nodeKey +'" '+
							' nodeValue="'+ nodeValue +'" '+
							' nodeValueReal="'+ nodeValueReal +'" '+
							' nodeValue="'+ nodeValue +'" '+
							'>'+
							'<textarea name="nodeDesc" style="display:none;" >'+ nodeDesc +'</textarea>  '+
							'<button class="btn btn-warning btn-xs update" type="button">编辑</button>  '+
							'<button class="btn btn-danger btn-xs delete" type="button">删除</button>  '+
							'</p>';
						}

						return html;
					};
				}
			}
		],
		"language" : {
			"sProcessing" : "处理中...",
			"sLengthMenu" : "每页 _MENU_ 条记录",
			"sZeroRecords" : "没有匹配结果",
			"sInfo" : "",
			"sInfoEmpty" : "无记录",
			"sInfoFiltered" : "(由 _MAX_ 项结果过滤)",
			"sInfoPostFix" : "",
			"sSearch" : "搜索:",
			"sUrl" : "",
			"sEmptyTable" : "表中数据为空",
			"sLoadingRecords" : "载入中...",
			"sInfoThousands" : ",",
			"oPaginate" : {
				"sFirst" : "首页",
				"sPrevious" : "上页",
				"sNext" : "下页",
				"sLast" : "末页"
			},
			"oAria" : {
				"sSortAscending" : ": 以升序排列此列",
				"sSortDescending" : ": 以降序排列此列"
			}
		}
	});
	
	$("#searchBtn").click(function(){
		confTable.fnDraw();
	});
	$("#nodeZK").change(function(){
		confTable.fnDraw();
	});
	$("#nodeGroup").change(function(){
		confTable.fnDraw();
	});
	$("#conf_list").on('click', '.tecTips',function() {
		var tips = $(this).attr("tips");
		ComAlertTec.show(tips);
	});
	
	// 删除
	$("#conf_list").on('click', '.delete',function() {
		var nodeZK = $(this).parent('p').attr("nodeZK");
		var nodeGroup = $(this).parent('p').attr("nodeGroup");
		var nodeKey = $(this).parent('p').attr("nodeKey");
		ComConfirm.show("确定要删除配置：" + nodeKey, function(){
			$.post(
				base_url + "/conf/delete",
				{
					"nodeZK" : nodeZK,
					"nodeGroup" : nodeGroup,
					"nodeKey" : nodeKey
				},
				function(data, status) {
					if (data.code == "200") {
						confTable.fnDraw();
					} else {
						ComAlert.show(2, data.msg);
					}
				}
			);
		});
	});
	
	// 恢复
	$("#conf_list").on('click', '.recovery',function() {
		
		var nodeZK = $(this).parent('p').attr("nodeZK");
		var nodeGroup = $(this).parent('p').attr("nodeGroup");
		var nodeKey = $(this).parent('p').attr("nodeKey");
		ComConfirm.show("确定要恢复配置：" + nodeKey, function(){
			$.post(
				base_url + "/conf/recovery",
				{
					"nodeZK" : nodeZK,
					"nodeGroup" : nodeGroup,
					"nodeKey" : nodeKey
				},
				function(data, status) {
					if (data.code == "200") {
						confTable.fnDraw();
					} else {
						ComAlert.show(2, data.msg);
					}
				}
			);
		});
	});
	
	// 同步
	$("#synBtn").click(function(){
		if (synFlg == '0') {
			ComAlert.show(1, '没有需要同步的配置');
		} else {
			var nodeZK = $('#nodeZK').val();
			var nodeGroup = $('#nodeGroup').val();
			var nodeKey = $('#nodeKey').val();
			ComConfirm.show("确定要同步配置：" + nodeZK +":"+ nodeGroup, function(){
				$.post(
					base_url + "/conf/synzk",
					{
						"nodeZK" : nodeZK,
						"nodeGroup" : nodeGroup,
						"nodeKey" : nodeKey
					},
					function(data, status) {
						if (data.code == "200") {
							confTable.fnDraw();
						} else {
							ComAlert.show(2, data.msg);
						}
					}
				);
			});
		}
	});
	
	// 复制配置
	$("#copyBtn").click(function(){

		var nodeZK = $('#nodeZK').val();
		var nodeGroup = $('#nodeGroup').val();
		var nodeKey = $('#nodeKey').val();
		ComConfirm.show("确定要复制配置：" + nodeZK +":"+ nodeGroup, function(){
			$.post(
				base_url + "/conf/copyzk",
				{
					"nodeZK" : nodeZK,
					"nodeGroup" : nodeGroup,
					"nodeKey" : nodeKey
				},
				function(data, status) {
					if (data.code == "200") {
						confTable.fnDraw();
					} else {
						ComAlert.show(2, data.msg);
					}
				}
			);
		});
	
	});

    // jquery.validate 自定义校验 “英文字母开头，只含有英文字母、数字和下划线”
    jQuery.validator.addMethod("myValid01", function(value, element) {
        var length = value.length;
        var valid = /^[a-z][a-z0-9.]*$/;
        return this.optional(element) || valid.test(value);
    }, "KEY只能由小写字母、数字和.组成,须以小写字母开头");

	// 复制
	$("#copy").click(function(){
		$("#copyModal .form input[name='nodeZK']").val( $('#nodeZK').val() );
		$("#copyModal .form input[name='nodeGroup']").val( $('#nodeGroup').val() );
		$('#copyModal').modal('show');
	});
	var copyModalValidate = $("#copyModal .form").validate({
		errorElement : 'span',  
        errorClass : 'help-block',
        focusInvalid : true,  
        rules : {
        	nodeGroupCopy : {
        		required : true ,
                minlength: 4,
                maxlength: 100,
                myValid01: false
            }
        }, 
        messages : {
        	nodeGroupCopy : {
        		required :'请输入"复制源的应用名称".'  ,
                minlength:'不应低于4位',
                maxlength:'不应超过100位'
            }
        }, 
		highlight : function(element) {  
            $(element).closest('.form-group').addClass('has-error');  
        },
        success : function(label) {  
            label.closest('.form-group').removeClass('has-error');  
            label.remove();  
        },
        errorPlacement : function(error, element) {  
            element.parent('div').append(error);  
        },
        submitHandler : function(form) {
    		$.post(base_url + "/conf/copy", $("#copyModal .form").serialize(), function(data, status) {
    			if (data.code == "200") {
    				confTable.fnDraw();
					$('#copyModal').modal('hide');
    			} else {
    				ComAlert.show(2, data.msg);
    			}
    		});
		}
	});
	$("#copyModal").on('hide.bs.modal', function () {
		$("#copyModal .form")[0].reset()
	});
	
	// 新增
	$("#add").click(function(){
		$("#addModal .form input[name='nodeZK']").val( $('#nodeZK').val() );
		$("#addModal .form input[name='nodeGroup']").val( $('#nodeGroup').val() );
		$('#addModal').modal('show');
	});
	var addModalValidate = $("#addModal .form").validate({
		errorElement : 'span',  
        errorClass : 'help-block',
        focusInvalid : true,  
        rules : {
        	nodeKey : {
        		required : true ,
                minlength: 4,
                maxlength: 100,
                myValid01: false
            },  
            nodeValue : {
            	required : false
            },
            nodeDesc : {
            	required : false
            }
        }, 
        messages : {
        	nodeKey : {
        		required :'请输入"KEY".'  ,
                minlength:'"KEY"不应低于4位',
                maxlength:'"KEY"不应超过100位'
            },  
            nodeValue : {	},
            nodeDesc : {	}
        }, 
		highlight : function(element) {  
            $(element).closest('.form-group').addClass('has-error');  
        },
        success : function(label) {  
            label.closest('.form-group').removeClass('has-error');  
            label.remove();  
        },
        errorPlacement : function(error, element) {  
            element.parent('div').append(error);  
        },
        submitHandler : function(form) {
    		$.post(base_url + "/conf/add", $("#addModal .form").serialize(), function(data, status) {
    			if (data.code == "200") {
    				confTable.fnDraw();
					$('#addModal').modal('hide');
    			} else {
    				ComAlert.show(2, data.msg);
    			}
    		});
		}
	});
	$("#addModal").on('hide.bs.modal', function () {
		$("#addModal .form")[0].reset()
	});
	
	// 更新
	$("#conf_list").on('click', '.update',function() {
		$("#updateModal .form input[name='nodeZK']").val( $(this).parent('p').attr("nodeZK") );
		$("#updateModal .form input[name='nodeGroup']").val( $(this).parent('p').attr("nodeGroup") );
		$("#updateModal .form input[name='nodeKey']").val( $(this).parent('p').attr("nodeKey") );
		$("#updateModal .form input[name='nodeValue']").val( $(this).parent('p').attr("nodeValue") );
		$("#updateModal .form textarea[name='nodeDesc']").val( $(this).parent('p').find("textarea[name='nodeDesc']").val() );
		$('#updateModal').modal('show');
	});
	var updateModalValidate = $("#updateModal .form").validate({
		errorElement : 'span',  
        errorClass : 'help-block',
        focusInvalid : true,
		rules : {
			nodeKey : {
				required : true ,
				minlength: 4,
				maxlength: 100
			},
			nodeValue : {
				required : false
			},
			nodeDesc : {
				required : false
			}
		},
		messages : {
			nodeKey : {
				required :'请输入"KEY".'  ,
				minlength:'"KEY"不应低于1位',
				maxlength:'"KEY"不应超过100位'
			},
			nodeValue : {	},
			nodeDesc : {	}
		},
		highlight : function(element) {  
            $(element).closest('.form-group').addClass('has-error');  
        },
        success : function(label) {  
            label.closest('.form-group').removeClass('has-error');  
            label.remove();  
        },
        errorPlacement : function(error, element) {  
            element.parent('div').append(error);  
        },
        submitHandler : function(form) {
    		$.post(base_url + "/conf/update", $("#updateModal .form").serialize(), function(data, status) {
    			if (data.code == "200") {
    				confTable.fnDraw();
					$('#updateModal').modal('hide');
    			} else {
    				ComAlert.show(2, data.msg);
    			}
    		});
		}
	});
	$("#updateModal").on('hide.bs.modal', function () {
		$("#updateModal .form")[0].reset()
	});
	
});