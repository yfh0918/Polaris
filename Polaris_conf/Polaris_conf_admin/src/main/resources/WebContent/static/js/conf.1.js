$(function(){

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
				obj.namespace = $('#namespace').val();
				obj.group = $('#group').val();
				return obj;
			}
		},
		"searching": false,
		"ordering": false,
		//"scrollX": true,	// X轴滚动条，取消自适应
		"columns": [
			{ "data": 'key', "visible" : true},
			{ "data": 'value', "visible" : true},
			{ "data": '操作' ,
				"render": function ( data, type, row ) {
					return function(){
						// html
						var html = '';
						html = '<p id="'+ row.id +'" '+
						' namespace="'+ row.namespace +'" '+
						' group="'+ row.group +'" '+
						' key="'+ row.key +'" '+
						' value="'+ row.value +'" '+
						'>'+
						'<button class="btn btn-warning btn-xs update" type="button">编辑</button>  '+
						'<button class="btn btn-danger btn-xs delete" type="button">删除</button>  '+
						'</p>';
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
	
	$("#namespace").change(function(){
		$.post(
				base_url + "/conf/findGroup",
				{
					"namespace" : $('#namespace').val()
				},
				function(data, status) {
					var html = '<option value="" ></option>' 
					for ( var i = 0; i <data.length; i++){
						html = html + '<option value="'+data[i]+'" >'+data[i]+'</option>'
					}
					document.getElementById("group").innerHTML=html;
					confTable.fnDraw();
				}
			);
		
	});
	
	$("#group").change(function(){
		confTable.fnDraw();
	});
	$("#conf_list").on('click', '.tecTips',function() {
		var tips = $(this).attr("tips");
		ComAlertTec.show(tips);
	});
	
	// 删除
	$("#conf_list").on('click', '.delete',function() {
		var namespace = $(this).parent('p').attr("namespace");
		var group = $(this).parent('p').attr("group");
		var key = $(this).parent('p').attr("key");
		ComConfirm.show("确定要删除配置：" + key, function(){
			$.post(
				base_url + "/conf/delete",
				{
					"namespace" : namespace,
					"group" : group,
					"key" : key
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

	
	// 新增
	$("#add").click(function(){
		$("#addModal .form input[name='namespace']").val( $('#namespace').val() );
		$("#addModal .form input[name='group']").val( $('#group').val() );
		$('#addModal').modal('show');
	});
	var addModalValidate = $("#addModal .form").validate({
		errorElement : 'span',  
        errorClass : 'help-block',
        focusInvalid : true,  
        rules : {
        	key : {
        		required : true ,
                minlength: 1,
                maxlength: 100,
                myValid01: false
            },  
            value : {
            	required : false
            }
        }, 
        messages : {
        	key : {
        		required :'请输入"KEY".'  ,
                minlength:'"KEY"不应低于1位',
                maxlength:'"KEY"不应超过100位'
            },  
            value : {	}
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

	
	// 更新
	$("#conf_list").on('click', '.update',function() {
		$("#updateModal .form input[name='namespace']").val( $(this).parent('p').attr("namespace") );
		$("#updateModal .form input[name='group']").val( $(this).parent('p').attr("group") );
		$("#updateModal .form input[name='key']").val( $(this).parent('p').attr("key") );
		$("#updateModal .form input[name='value']").val( $(this).parent('p').attr("value") );
		$('#updateModal').modal('show');
	});
	var updateModalValidate = $("#updateModal .form").validate({
		errorElement : 'span',  
        errorClass : 'help-block',
        focusInvalid : true,
		rules : {
			key : {
				required : true ,
				minlength: 1,
				maxlength: 100
			},
			value : {
				required : false
			}
		},
		messages : {
			key : {
				required :'请输入"KEY".'  ,
				minlength:'"KEY"不应低于1位',
				maxlength:'"KEY"不应超过100位'
			},
			value : {	}
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

	
});