$(function() {
	var namespace_id = "";
	
	// init date tables
	var groupTable = $("#group_list").dataTable({
		"paging": false, // 禁止分页
		"deferRender": true,
		"processing" : true,
		"serverSide": true,
		"ajax": {
			"url": base_url + "/group/findList?namespace="+$('#namespace_id').val()
		},
		"searching": false,
		"ordering": false,
		//"scrollX": true,	// X轴滚动条，取消自适应
		"columns": [
			{ "data": 'group', "visible" : true},
			{ "data": '操作' ,
				"render": function ( data, type, row ) {
					return function(){
						// html
						var html = '';
						html = '<p id="'+ row.id +'" '+
						' group="'+ row.group +'" '+
						'>'+
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
	
	// 删除
	$("#group_list").on('click', '.delete',function() {
		var group = $(this).parent('p').attr("group");

		ComConfirm.show("确认删除应用?", function(){
			$.ajax({
				type : 'POST',
				url : base_url + '/group/remove',
				data : {"namespace":$('#namespace').val(), "group":group},
				dataType : "json",
				success : function(data){
					if (data.code == 200) {
						groupTable.fnDraw();
					} else {
						if (data.msg) {
							ComAlert.show(2, data.msg);
						} else {
							ComAlert.show(2, '删除失败');
						}
					}
				},
			});
		});
	});
	
	$("#namespace_id").change(function(){
		namespace_id = $('#namespace_id').val();
		groupTable.fnDraw();
	});

	// jquery.validate 自定义校验 “英文字母开头，只含有英文字母、数字和下划线”
	jQuery.validator.addMethod("myValid01", function(value, element) {
		//var length = value.length;
		//var valid = /^[a-zA-Z][a-zA-Z0-9-]*$/;
		//return this.optional(element) || valid.test(value);
		return true;
	}, "限制以字母开头，由字母、数字和中划线组成");

	$('.add').on('click', function(){
		$("#addModal .form input[name='namespace']").val( $('#namespace').val() );
		$('#addModal').modal({backdrop: false, keyboard: false}).modal('show');
	});
	var addModalValidate = $("#addModal .form").validate({
		errorElement : 'span',
		errorClass : 'help-block',
		focusInvalid : true,
		rules : {
			group : {
				required : true,
				rangelength:[1,100],
				myValid01 : true
			}
		},
		messages : {
			group : {
				required :"请输入名称",
				rangelength:"长度限制为1~100",
				myValid01: "限制以字母开头，由字母、数字和中划线组成"
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
			$.post(base_url + "/group/save",  $("#addModal .form").serialize(), function(data, status) {
				if (data.code == "200") {
					groupTable.fnDraw();
				} else {
					if (data.msg) {
						ComAlert.show(2, data.msg);
					} else {
						ComAlert.show(2, "新增失败");
					}
				}
			});
		}
	});
	$("#addModal").on('hide.bs.modal', function () {
		$("#addModal .form")[0].reset();
		addModalValidate.resetForm();
		$("#addModal .form .form-group").removeClass("has-error");
	});

});
