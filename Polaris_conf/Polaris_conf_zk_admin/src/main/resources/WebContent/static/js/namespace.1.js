$(function() {

	// remove
	$('.remove').on('click', function(){
		var namespace = $(this).attr('namespace');

		ComConfirm.show("确认删除应用?", function(){
			$.ajax({
				type : 'POST',
				url : base_url + '/namespace/remove',
				data : {"namespace":namespace},
				dataType : "json",
				success : function(data){
					if (data.code == 200) {
						window.location.reload();
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

	// jquery.validate 自定义校验 “英文字母开头，只含有英文字母、数字和下划线”
	jQuery.validator.addMethod("myValid01", function(value, element) {
		//var length = value.length;
		//var valid = /^[a-zA-Z][a-zA-Z0-9-]*$/;
		//return this.optional(element) || valid.test(value);
		return true;
	}, "限制以字母开头，由字母、数字和中划线组成");

	$('.load').on('click', function(){

		$.ajax({
			type : 'POST',
			url : base_url + '/namespace/load',
			success : function(data){
				if (data.code == 200) {
					window.location.reload();
				} else {
					if (data.msg) {
						ComAlert.show(2, data.msg);
					} else {
						ComAlert.show(2, '导入配置失败');
					}
				}
			},
		});
	
	});

	$('.add').on('click', function(){
		$('#addModal').modal({backdrop: false, keyboard: false}).modal('show');
	});
	var addModalValidate = $("#addModal .form").validate({
		errorElement : 'span',
		errorClass : 'help-block',
		focusInvalid : true,
		rules : {
			zkName : {
				required : true,
				rangelength:[1,100],
				myValid01 : true
			},
			zkValue : {
				required : true,
				rangelength:[4, 100]
			}
		},
		messages : {
			namespace : {
				required :"请输入名称",
				rangelength:"名称长度限制为1~100",
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
			$.post(base_url + "/namespace/save",  $("#addModal .form").serialize(), function(data, status) {
				if (data.code == "200") {
					$('#addModal').modal('hide');
					setTimeout(function () {
						window.location.reload();
					}, 315);
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
