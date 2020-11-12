// Added by FW Team

/*********************************************
 * Ajax global setting
 *********************************************/
$(document).ajaxSend(function(xhr, settings) {
	$(".ui-state-default.ui-state-active.loading").remove();
	$(".dev-error").hide();
	$component.blockUI();
});
$(document).ajaxComplete(function(xhr, status) {
	$component.unblockUI();
});
$(document).ajaxError(function(event, xhr, errorThrown) {
	$component.checkAjaxError(xhr);
});



$component.string = {};
$component.string.getNumberOnly = function(str) {
	return str.replace(/[^0-9]/g,"");
}
$component.string.toNumber = function(str) {
	if (str == null || str.length == 0) {
		return 0;
	}
	str = str.replace(/[^0-9\+\-\.]/g,"");
	return Number(str);
};

$component.log = function() {
	if (console) {
		if (console.log.apply) {
			console.log.apply(console, arguments);
		} else {
			for (var i = 0; i < arguments.length; i++) {
				console.log(arguments[i]);
			}
		}
	}
};

/*********************************************
 * input pattern check
 *********************************************/
$component.inputPattern = function(){
	$("input[pattern]").on("keypress", function(e){
		var patt = new RegExp($(this).attr("pattern"));
		var res = patt.test(e.key);
		if (!res) {
			e.preventDefault();
		}
	});
}


/**
 *	타이틀 태그, 화면 타이틀 세팅 함수
 *	@param {string} replaceWord1 #{1}대체할단어
 *	@param {string} replaceWord2 #{2}대체할단어
 *	@returns {null}
 *	@exemple
 	$component.pageTitle('로그인');
 */
$component.pageTitle = function() {

	var titleFormat = config.pageTitleFormat

	for (var i = 0; i < arguments.length; i++) {

		if (i === 0) {
			$('.page-title').text(arguments[i]);
		}

		if (titleFormat.indexOf('#{' + (i+1) + '}') > -1) {
			titleFormat = titleFormat.replaceAll('#{' + (i+1) + '}', arguments[i]);
		}
	}

	document.title  = titleFormat;
}


/**
 *	Toast UI Pagination을 이용한 클라이언트 페이징 기능
 *	@param {object} options 페이징 생성 옵션
 *	@returns {object} pagination 페이징 객체
 *	@exemple
	var pagination = $component.pagination({
		id 			 : 'pagination',
		page		 : data.page,
	    totalItems	 : data.totalItems,
	    itemsPerPage : data.itemsPerPage,
	    afterMove 	 : function(evt) {
			console.log(evt);
		},
		click 	     : function(evt) {
			console.log(evt);
		}
	});
 */
$component.pagination = function(options) {

	var opt = $.extend({
		id			 : 'pagination',
		page		 : 1,
		totalItems	 : 1,
		itemsPerPage : 10,
		visiblePages : 10,
		afterMove 	 : function(evt) {
		},
		click 	 : function(evt) {
		},
		usageStatistics: false
	}, options);

	var id = opt.id.replace(/#/gi,'');

	if (!document.getElementById(id)) {
		throw new Error(id + ' is not defined');
	}

	var pagination = new tui.Pagination(id, {
		page: opt.page,
        totalItems: opt.totalItems,
        itemsPerPage: opt.itemsPerPage,
        visiblePages: opt.visiblePages
    });

	pagination.on('afterMove', function(evt) {
		opt.afterMove(evt);
		return false;
	});

	pagination.on('beforeMove', function(evt) {
		opt.click(evt);
		return false;
	});

	return pagination;
}

$component.dialog = {}

/**
 *	JQuery ui와 ajax를 이용한 외부 다이얼로그 오픈 기능
 *	@param {object} options 다이얼로그 생성 옵션
 *	@returns {object} 생성된 다이얼로그 wrap 제이쿼리 객체
 *	@exemple
	$component.dialog.open({
		id : 'userDialog',
		url : '/svc/sample/shc/popup.do',
		title: '회원 다이얼로그',
		width: 1000,
		data : {
			name : 'test',
			age : '20'
		},
		callback: function(data) {
			console.log(data)
		}
	});
 */
$component.dialog.open = function(options) {

	var $div;

    var opt = $.extend({
    	id : $component.getNewId(),
    	url : '',
    	title: '',
        modal: true,
        draggable: true,
        resizable: false,
        closeOnEscape: true,
        position : {
			my : 'center',
			at : 'center',
			of : window
		},
        width: options.width || 'auto',
        height: options.height || 'auto',
        minWidth: 150,
        minHeight: 150,
        buttons: [
        ],
        callback: function(data) {
        },
        create: function(event, ui) {
        },
        open: function(event, ui) {
        },
        beforeClose: function(event, ui) {
        },
        close: function(event, ui) {
        },
        focus: function(event, ui) {
        },
        dragStart: function(event, ui) {
        },
        drag: function(event, ui) {
        },
        dragStop: function(event, ui) {
        },
        resizeStart: function(event, ui) {
        },
        resize: function(event, ui) {
        },
        resizeStop: function(event, ui) {
        }
    }, options);

    if (options.url === undefined){
    	throw new Error('url is not defined');
    } else if (options.id !== undefined && $('#'+ options.id.replace(/#/gi, '')).length > 0) {
    	$('#'+ options.id.replace(/#/gi, '')).dialog('moveToTop');
        return;
    }

    opt.data = $.extend({dId:opt.id.replace(/#/gi, '')}, $component.removeEmptyJsonKey(opt.data));

    opt.open = function(event, ui) {
        if ($.isFunction(options.open)) {
        	options.open.call(this, event, ui);
        }
    };

    opt.close = function(event, ui) {
        if ($.isFunction(options.close)) {
        	options.close.call(this, event, ui);
        }

        $('#' + opt.id.replace(/#/gi, '')).remove();
    };


    $div = $(document.createElement('div'));
    $div.attr('id', opt.id).hide();

    var success = false;
    $component.get({
        async: false,
        url: opt.url,
        data: opt.data,
        dataType: "html",
        success: function(html){
        	success = true;
	        $('body').append($div);
	        $div.html(html);
        }
    });

    if (success) {
        $div.dialog(opt);
        var $dialog = $(".ui-dialog");
        $dialog.addClass("modal-content");
        $dialog.find(".ui-dialog-content").addClass("modal-body");

        return $div;
    }
    else {
    	return null;
    }
}

/**
 *	JQuery ui를 이용한 다이얼로그 닫기 기능
 *	@param {object or string} options 다이얼로그 옵션 또는 닫을 다이얼로그 id
 *	@returns {null}
 *	@exemple

	$component.dialog.close('userDialog');

	$component.dialog.close({
		id: 'userDialog',
		beforeClose: function(e) {
			var options = $(this).dialog("option");
			options.callback('test');
		}
	});
 */
$component.dialog.close = function(options) {

	if (typeof options == 'string') {
		if($('#'+ options.replace(/#/gi, '')).length === 0) {
			throw new Error(options + ' is not defined');
	    }

		$('#' + options.replace(/#/gi, '')).dialog(opt).dialog('close');

		return;
	}

    var opt = $.extend({
    	id : '',
    	beforeClose: function(event, ui) {
    	}
    }, options);

    if (options.id === undefined) {
    	throw new Error('id is not defined');
    } else if ($('#'+ options.id.replace(/#/gi, '')).length === 0) {
    	throw new Error(options.id + ' is not defined');
    }

    $('#' + opt.id.replace(/#/gi, '')).dialog(opt).dialog('close');
}

/**
 *	JQuery ui를 이용한 다이얼로그 옵션 가져오기 기능
 *	@param {string} 다이얼로그 id
 *	@returns {Object} option 객체
 *	@exemple

	var options = $component.dialog.option('userDialog');
 */
$component.dialog.option = function(id) {

	if($('#'+ id.replace(/#/gi, '')).length === 0) {
		throw new Error(options + ' is not defined');
    }

	return $('#'+ id.replace(/#/gi, '')).dialog('option');
}

$component.multipart = {}
$component.multipart.refresh = function($filezone) {
	$('.-list-wrap-old',$filezone).empty();
	$('.-list-wrap-new',$filezone).empty();

	$filezone.data('option').removeFiles();
	$filezone.data('option').getFileList();
}

$component.multipart.getFile = function($filezone) {
	return $filezone.data('file');
}

$component.multipart.getFiles = function($filezone) {
	return $filezone.data('files');
}

$component.multipart.single = function($filezone, options) {
	var opt = $.extend({
		data: {
		},
		editable: true,
		name: "file",
		label: "첨부",
		placeholder: '',
		download: function(file) {
		},
		change: function(file) {
		}
	}, options);

	var inputFile = '<input type="file" name="' + opt.name + '" class="file" style="display:none;">';
	var isChanged = false;
	var useRemove = $.isFunction(opt.remove);

	var fileTemplate = '<div class="-file">'
		fileTemplate+= (opt.editable || opt.editable == 'true') ? '<button type="button" class="-remove" style="display:none;">×</button>' : '';
		fileTemplate+= '<span>' + opt.placeholder + '</span>';
		fileTemplate+= (opt.editable || opt.editable == 'true') ? '<button class="-add" type="button">' + opt.label + '</button>' : '';
		fileTemplate+= '</div>';

	$filezone.empty().addClass('-filezone-single').append(fileTemplate);

	// 파일추가 트리거
	if (opt.editable || opt.editable == 'true') {
		$('.-add', $filezone).click(function() {

			var agent = navigator.userAgent.toLowerCase();

			if ($('.file', $filezone).length > 0) {
				if ( (navigator.appName == 'Netscape' && navigator.userAgent.search('Trident') != -1) || (agent.indexOf("msie") != -1) ){
					$('.file', $filezone).replaceWith($(inputFile).clone(true));
				} else {
				    $('.file', $filezone).val("");
				}
			} else {
				$('.-file', $filezone).append(inputFile);
			}

			$('.file', $filezone).trigger('click');
		});
	}

	// 파일추가 이벤트
	$('.-file', $filezone).on('change','.file',function() {

		// 파일을 dom에서 제거시 $(this).detach();
		var $detachFile = $(this);
		var file = $detachFile[0].files[0];
		var $this = $(this);
		var result = true;

		if ($.isFunction(opt.change)) {
			result = opt.change(file);
		}

		if(result == true || result == 'true') {

			isChanged = true;

			$('.-file span', $filezone).replaceWith('<span>' + file.name + ' (' +$component.getFileSizeString(file.size) + ')'  + '</span>');
			$('.-file .-remove', $filezone).show();

			$filezone.data('file', file);
		}
	});

	// 추가된 파일 삭제 이벤트
	$('.-file', $filezone).on('click', '.-remove', function(){

		var file = $filezone.data('file');

		if ($filezone.data('exist') == 'true') {

			if (isChanged) {
				isChanged = false;
				file = $filezone.data('oldFile');
				$('.-file span', $filezone).replaceWith('<span class="-download">' + file.fileNm + ' (' +$component.getFileSizeString(file.fileSize) + ')'  + '</span>');

				if(!useRemove) {
					$('.-remove', $filezone).hide();
				}

			} else {
				if(useRemove) {
					opt.remove(file, function() {
						reset();
					});
				}
			}
		} else {
			reset();
		}

		$('.file', $filezone).remove();

		function reset() {
			$filezone.data('exist','');
			$filezone.data('file', '');
			$('.-file span', $filezone).replaceWith('<span>' + opt.placeholder + '</span>');
			$('.-remove', $filezone).hide();
		}
	});


	// 초기 파일 세팅
	!function() {

		var file;

		if ($.isJsonString(opt.data)) {
			file = JSON.parse(opt.data);
		} else {
			file = opt.data;
		}

		if (file.fileNm !== undefined) {
			$filezone.data('file', file);
			$filezone.data('oldFile', file);
			$filezone.data('exist', 'true');

			if (file.fileSize !== undefined) {
				$('.-file span', $filezone).replaceWith('<span class="-download">' + file.fileNm + ' (' +$component.getFileSizeString(file.fileSize) + ')'  + '</span>');
			} else {
				$('.-file span', $filezone).replaceWith('<span class="-download">' + file.fileNm + '</span>');
			}

			if(useRemove) {
				$('.-file .-remove', $filezone).show();
			}
		}
	}();

	// 기존 파일 다운로드 이벤트
	if (options.download !== undefined) {
		$('.-file', $filezone).on('click','.-download',function(e) {
			e.preventDefault();
			opt.download($filezone.data("file"));
		});
	}
}

$component.multipart.multi = function($filezone, options) {

	var opt = $.extend({
		editable: true,
		showInfo: true,
		listUrl: '',
		label: '파일추가',
		exts: [],
		sizeLimit : 0,
		download: function(file) {
		},
		remove: function(file, callback) {
		}
	}, options);

	var files = [];
	var totalSize = 0;
	var inputFile = '<input type="file" name="file" class="file" multiple="multiple" style="display:none;">';

	var addButtonTemplate = (opt.editable || opt.editable == 'true') ? '<button class="-add" type="button">' +opt.label+ '</button>' : '';

	var infoTemplate = '<div class="-info">';
		infoTemplate+= '<span class="-count">첨부파일 : <strong>0</strong>건</span>';
		infoTemplate+= (opt.exts.length > 0) ? '<span class="-exts">허용확장자 : <strong></strong></span>' : '';
		infoTemplate+= (opt.sizeLimit > 0) ? '<span class="-sizeLimit">허용크기 : <strong></strong></span>' : '';
		infoTemplate+= '</div>';

	var oldFileTemplate = '<div class="-file">'
		oldFileTemplate+= '{{#editable}}<button type="button" class="-remove">×</button>{{/editable}}';
		oldFileTemplate+= '<span class="-download">{{fileNm}} ({{formatFileSize}})</span>';
		oldFileTemplate+= '</div>';

	var newFileTemplate = '<div class="-file">';
		newFileTemplate+= '<button type="button" class="-remove">×</button>';
		newFileTemplate+= '<span>{{fileName}} ({{formatFileSize}})</span>';
		newFileTemplate+= '</div>';

	$filezone.empty().addClass('-filezone');
	$filezone.append(addButtonTemplate);
	$filezone.append((opt.showInfo == true || opt.showInfo == 'true') ? infoTemplate : '');
	$filezone.append('<div class="-list-wrap-old"></div>');
	$filezone.append('<div class="-list-wrap-new"></div>');

	$filezone.data("files", files);
	$filezone.data("option", opt);

	opt.removeFiles = function() {
		files.length = 0;
	}

	// 파일리스트 불러오기
	opt.getFileList = function() {
		$component.get({
			url: opt.listUrl,
			success: function(data) {

				var $file;
				totalSize = 0;

				$.each(data, function(i, file) {

					totalSize+= file.fileSize;

					file.formatFileSize = $component.getFileSizeString(file.fileSize);
					file.editable = (opt.editable || opt.editable == 'true') ? true : false;

					$file = $(Mustache.render(oldFileTemplate, file));
					$file.data("file", file);

					$('.-list-wrap-old', $filezone).append($file);
				});

				// 기존 파일 다운로드 이벤트
				if (options.download !== undefined) {
					$('.-list-wrap-old .-download', $filezone).click(function(e) {
						e.preventDefault();
						opt.download($(this).closest('div').data("file"));
					});
				}

				// 기존 파일 삭제 이벤트
				if (options.remove !== undefined && (opt.editable || opt.editable == 'true')) {
					$('.-list-wrap-old .-remove', $filezone).click(function(e) {
						e.preventDefault();

	                    var $f = $(this).closest('div');

	                    opt.remove($f.data("file"), function() {
		                    totalSize -= $f.data('file').fileSize;
	                        $f.remove();
	                        refreshInfo();
	                    });
					});
				}

				refreshInfo();
			}
		});
	}

	// 파일추가 트리거
	if (opt.editable || opt.editable == 'true') {
		$('.-add', $filezone).click(function() {
			$('.-list-wrap-old .file', $filezone).length || $('.-list-wrap-old', $filezone).append(inputFile);
			$('.-list-wrap-old .file', $filezone).trigger('click');
		});
	}

	// 파일추가 이벤트
	$('.-list-wrap-old', $filezone).on('change','.file',function() {

		var $detachFile = $(this).detach();
		var $file;

		$.each($detachFile[0].files, function(i,file) {
			var isExists = false;
			var isAllow = (opt.exts.length === 0) ? true : false;
			var fileExt = file.name.slice(file.name.lastIndexOf(".") + 1).toLowerCase();

			for (var i = 0; i < files.length; i++) {
				if (files[i].name == file.name && files[i].size == file.size) {
					isExists = true;
					break;
				}
			}

			if (!isExists) {

				$.each(opt.exts, function(i,extension) {
					if (fileExt == extension.replace('.','').toLowerCase()) {
						isAllow = true;
						return false;
					}
				});

				if (!isAllow) {
					return true;
				}

				if (opt.sizeLimit != 0 && totalSize + file.size > opt.sizeLimit) {
					return true;
				}

				totalSize+=file.size;

				files.push(file);

				file.fileName = file.name
				file.formatFileSize = $component.getFileSizeString(file.size);

				$file = $(Mustache.render(newFileTemplate, file));
				$file.data("file", file);
				//$file.append($detachFile);

				$('.-list-wrap-new', $filezone).append($file);
			}
		});

		refreshInfo();
	});

	// 추가된 파일 삭제 이벤트
	$('.-list-wrap-new', $filezone).on('click', '.-remove', function(){

		var $file = $(this).closest('div');

		totalSize -= $file.data('file').size;

		$.each(files, function(idx, file) {
			if ($file.data("file") == file) {
				files.splice(idx, 1);
			}
		});

		$file.remove();
		refreshInfo();
	});

	// url 있을경우
	if (opt.listUrl) {
		opt.getFileList();
	} else {
		refreshInfo();
	}


	function refreshInfo() {
		var cnt = $('.-file', $filezone).length;
		$('.-count strong', $filezone).text(cnt);

		if (opt.sizeLimit > 0) {

			if (totalSize < 0) {
				totalSize = 0;
			}

			$('.-sizeLimit strong', $filezone).text($component.getFileSizeString(totalSize) + ' / ' + $component.getFileSizeString(opt.sizeLimit));
		}

		if (opt.exts.length > 0) {
			$('.-exts strong', $filezone).text(opt.exts.join(", ").replace(/\./g,'').toLowerCase());
		}
	}
}

$component.multipart.post = function($form, options){
	var defaults = {
		url: "",
		data:{},
		fileParameter: "file",
		files: [],
		success: function(data, status, xhr) {
		}
	};
	var opt = $.extend(defaults, options);

	var formData = new FormData($form[0]);
	var data = $.extend({}, opt.data);
	$.each(data, function(key, value){
		if (value === "" || value === null) {
		}
		else {
			formData.append(key, value);
		}
	});

	if ($.type(opt.files) !== 'array') {
		formData.append(opt.fileParameter, opt.files);
	} else {
		$.each(opt.files, function(idx, file){
			formData.append(opt.fileParameter, file);
		});
	}


	var progress = '<div class="progress">' +
					'<div class="progress-bar progress-bar-striped progress-bar-animated" role="progressbar" aria-valuenow="30" aria-valuemin="0" aria-valuemax="100" style="width:0%"></div>'+
					'</div>';

	$.ajax({
		url: opt.url,
		type: "post",
		timeout: 60000 * 5,
		data: formData,
		cache: false,
		processData: false,
		contentType: false,
		xhr: function() {
			var myXhr = $.ajaxSettings.xhr();
			if (myXhr.upload) {
				myXhr.upload.addEventListener("progress", function(event){

					var position =  event.loaded || event.position;
					var percent = Math.ceil(position * 100 / event.total);

					$(".progress .progress-bar", '.blockUI').css("width", percent + "%");

				}, false);
			}
			return myXhr;
		},
        beforeSend: function(xhr, settings) {
        	$component.blockUI(progress);
        },
		success: function(data, status, xhr) {
			if ($.isFunction(opt.success)) {
				opt.success(data, status, xhr);
			}
		},
		complete: function(xhr, status) {
			$component.unblockUI();
		}
	});
};

$component.submit = function($outline, options){
	if (arguments.length == 1) {
		options = $outline;
		$outline = $();
	}

	var opt = $.extend({
		url: '',
		method: 'post',
		data : {},
		id: '__myform',
		target: null,
	},options);

	var data = $component.toJson($outline);
	data = $.extend(data, opt.data);
	data = $component.removeEmptyJsonKey(data);
	opt.param = data;
	opt.action = opt.url;

    var formId = opt.id;
    var form = $('form[id=' + formId + ']');

    if (form.length == 0) {
        form = $(document.createElement('form'))
            .attr('id', formId)
            .attr('name', formId)
            .attr('action', opt.action)
            .attr('method', opt.method)
            .attr('target', opt.target)
            .hide();
        $('body').append(form);
    } else {
        form.attr('action', opt.action)
            .attr('method', opt.method)
            .attr('target', opt.target);
    }

    form.empty();

    // 파라미터 Object로 Form에 hidden필드를 생성
    var param = opt.param;
    if($.isArray(param)) {
        $.each(param, function() {
            $('<input>').attr({type:'hidden', name:this.name, value:this.value}).appendTo(form);
        });
    }
    else {
        for(var name in param) {
            $('<input>').attr({type:'hidden', name:name, value:param[name]}).appendTo(form);
        }
    }

    form.submit();
}

$component.get = function($outline, options){
	if (arguments.length == 1) {
		options = $outline;
		$outline = $();
	}
	options.type = "get";
	options.method = "get";
	return $component.ajax($outline, options);
};

$component.post = function($outline, options){
	if (arguments.length == 1) {
		options = $outline;
		$outline = $();
	}
	options.type = "post";
	options.method = "post";
	return $component.ajax($outline, options);
};

$component.put = function($outline, options){
	if (arguments.length == 1) {
		options = $outline;
		$outline = $();
	}
	options.type = "put";
	options.method = "put";
	return $component.ajax($outline, options);
};


$component.delete = function($outline, options){
	if (arguments.length == 1) {
		options = $outline;
		$outline = $();
	}
	options.type = "delete";
	options.method = "delete";
	return $component.ajax($outline, options);
};

$component.ajax = function($outline, options){
	if (arguments.length == 1) {
		options = $outline;
		$outline = $();
	}

	var opt = $.extend({
		data: {},
		url: "",
		cache: false,
		success: function(data, status, xhr) {
		}
	}, options);

	var data = $component.toJson($outline);
	data = $.extend(data, opt.data);
	data = $component.removeEmptyJsonKey(data);
	opt.data = $.param(data,true);
    return $.ajax(opt);
};


$component.checkAjaxError = function(xhr) {
	var jsonData = null;
	try {
		jsonData = $.parseJSON(xhr.responseText);
	} catch (e) {
		cfn_ajaxResponseCheck(xhr, true);
		return;
	}

	if ($.isArray(jsonData)) {
		for (var i = 0; i < jsonData.length; i++) {
			if ($.isBlank(jsonData[i].field)) {
				cfn_ajaxResponseCheck(xhr, true);
				return;
			}
			else {
				if ($.isNotBlank(jsonData[i].message)) {
					$(".dev-error." + jsonData[i].field).text(jsonData[i].message);
				}
				$(".dev-error." + jsonData[i].field).show();
			}
		}
	}
	else {
		cfn_ajaxResponseCheck(xhr, true);
	}
}

/**
 * $outline 내부의 값을 JSON으로 전송한다.
 */
$component.postJson = function($outline, options){
	if (arguments.length == 1) {
		options = $outline;
		$outline = $();
	}

	options.type = "post";
	options.method = "post";

	return $component.ajaxJson($outline, options);
};

$component.putJson = function($outline, options){
	if (arguments.length == 1) {
		options = $outline;
		$outline = $();
	}

	options.type = "put";
	options.method = "put";

	return $component.ajaxJson($outline, options);
};

$component.ajaxJson = function($outline, options){
	if (arguments.length == 1) {
		options = $outline;
		$outline = $();
	}

	var opt = $.extend({
		data: {},
		url: "",
		cache: false,
		success: function(data, status, xhr) {
		}
	}, options);

	var data = $component.toJson($outline);
	data = $.extend(data, opt.data);
	data = $component.removeEmptyJsonKey(data);
	opt.data = JSON.stringify(data);
	opt = $.extend( opt, {
		contentType: "application/json; charset=utf-8",
	});

    return $.ajax(opt);
};

/**
 * $outline 내부에 있는 submit 가능한
 * 요소를 JSON 형태의 값으로 바꿔준다.
 */
$component.toJson = function($outline) {
    var o = {};
    var a = $("input,select,textarea", $outline).serializeArray();

    $.each(a, function() {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};

$component.getNewId = function() { // UUID v4 generator in JavaScript (RFC4122 compliant)
	return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
		var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 3 | 8);
		return v.toString(16);
	});
};

$component.getFileSizeString = function(size) {
	var unit = "Bytes";

	if (size <= 0) {
		return '0 Bytes';
	}

	if (Math.round(size / 1024 / 1024 / 1024) > 1) {
		size = size / 1024 / 1024 / 1024;
		unit = "GB";
	} else if (Math.round(size / 1024 / 1024) > 1) {
		size = size / 1024 / 1024;
		unit = "MB";
	} else if (Math.round(size / 1024) > 1) {
		size = size / 1024;
		unit = "KB";
	}
	size = $component.numberFormat(size, "#,###.0");
	return size + " " + unit;
}

$component.numberFormat = function(num, fmt) {
	if (num === "" || num === null) {
		return "";
	}
	if (fmt == null || fmt == undefined) {
		fmt = "#,##0.##########";
	}
	return $.formatNumber(num, {format:fmt});
};

$component.toast = {};

$component.toast.success = function(message, options) {
	$component.toast.show("success", message, options);
};

$component.toast.info = function(message, options) {
	$component.toast.show("info", message, options);
};

$component.toast.warning = function(message, options) {
	$component.toast.show("warning", message, options);
};

$component.toast.error = function(message, options) {
	$component.toast.show("error", message, options);
};

$component.toast.show = function(toastType, message, options) {
	var opt = $.extend({
		timeOut: "2000",
		progressBar: false,
		preventDuplicates: false,
		newestOnTop: false,
		positionClass: "toast-top-center",

		closeButton: false,
		debug: false,
		onclick: null,
		showDuration: "300",
		hideDuration: "1000",
		extendedTimeOut: "1000",
		showEasing: "swing",
		hideEasing: "linear",
		showMethod: "fadeIn",
		hideMethod: "fadeOut"
	}, options);

	toastr.options = opt;
	toastr[toastType](message);
};

$component.tree = {};

$component.tree.setData = function($tree, data) {
	$tree.jstree(true).settings.core.data =data;
	$tree.jstree('refresh');
}

$component.tree.parse =  function(options) {

	if ($.isArray(options)) {
		options = {
			list : options
		};
	}

	var opt = $.extend({
		list: [],
		idName: "id",
		parentName: "parent",
		rootParent: null,
		expanded: true
	}, options);

	if (!$.isArray(opt.list)) {
		opt.list = [opt.list];
	}

	var results = [];

	opt.list.forEach(function(item){
		if (!item.hasOwnProperty("id")) {
			item.id = item[opt.idName];
		}
		if (!item.hasOwnProperty("parent")) {
			item.parent = item[opt.parentName];
		}
		if (item[opt.parentName] == opt.rootParent) {
			if (item.parent === opt.rootParent) {
				item.parent = '#';
			}
			item.level = 0;
			childrenOf(item);
		}
	});

	function childrenOf(item) {
		item.isLeaf = true;

		results.push(item);

		opt.list.forEach(function(newItem,i){
			if (item.id == newItem[opt.parentName]) {
				newItem.id = newItem[opt.idName];
				newItem.level  = item.level + 1;
				item.isLeaf = false;
				childrenOf(newItem);
			}
		});
	}

	results.forEach(function(item){
		if (item.isLeaf) {
			item.type = 'leaf'
		} else {
			if (opt.expanded == true || opt.expanded == 'true') {
				item.state = {
					'opened' : true
				}
				item.type = 'f-open';
			} else {
				item.type = 'f-closed';
			}
		}
	});

	return results;
};

$component.parseTree = function(options) {
	var opt = $.extend({
		list: [],
		idName: "id",
		parentName: "parent",
		rootParent: null,
		expanded: true,
	}, options);

	if (!$.isArray(opt.list)) {
		opt.list = [ opt.list ];
	}

	var results = [];

	opt.list.forEach(function(item){
		if (!item.hasOwnProperty("id")) {
			item.id = item[opt.idName];
		}
		if (item[opt.parentName] == opt.rootParent) {
			item.level = 0;
			childrenOf(item);
		}
	});

	function childrenOf(item) {
		item.isLeaf = true;
		item.expanded = opt.expanded;
		results.push(item);
		opt.list.forEach(function(newItem){
			if (item.id == newItem[opt.parentName]) {
				newItem.id = newItem[opt.idName];
				newItem.level  = item.level + 1;
				item.isLeaf = false;
				childrenOf(newItem);
			}
		});
	}
	return results;
};

//----------------------------------------------------------------------------------------------------------------------
//JQuery 및 기존 object 확장
(function($){

	String.prototype.trim = function(str) {
	    str = this != window ? this : str;
	    return str.replace(/^\s+/g, '').replace(/\s+$/g, '');
	};

	String.prototype.byteLength = function(str) {
	    str = this != window ? this : str;
	    var size = 0;
	    for ( var i = 0; i < str.length; i++) {
	        size++;
	        if (44032 <= str.charCodeAt(i) && str.charCodeAt(i) <= 55203) {
	            size++;
	        }
	        if (12593 <= str.charCodeAt(i) && str.charCodeAt(i) <= 12686) {
	            size++;
	        }
	    }
	    return size;
	};

	String.prototype.replaceAll = function(str1, str2) {
	    var temp_str = "";
	    var temp_trim = this.replace(/(^\s*)|(\s*$)/g, "");
	    if (temp_trim && str1 != str2) {
	        temp_str = temp_trim;
	        while (temp_str.indexOf(str1) > -1)
	            temp_str = temp_str.replace(str1, str2);
	    }
	    return temp_str;
	};

	String.prototype.hasFinalConsonant = function(str) {
	    if (str == null || str == "")
	        return false;
	    str = this != window ? this : str;
	    var strTemp = str.substr(str.length - 1);
	    return ((strTemp.charCodeAt(0) - 16) % 28 != 0);
	};

	String.prototype.lpad = function() {
	    var args = arguments;
	    var len = args[0];

	    if (args.length == 1)
	        padStr = " ";
	    else
	        padStr = args[1];
	    var returnString = "";
	    var padCnt = Number(len) - String(this).length;
	    for ( var i = 0; i < padCnt; i++)
	        returnString += String(padStr);
	    returnString += this;
	    return returnString.substring(returnString.length - len);
	};

	String.prototype.rpad = function() {
	    var args = arguments;
	    var totalLength = args[0];

	    if (args.length == 1)
	        paddingChar = " ";
	    else
	        paddingChar = args[1];
	    var returnString = "";
	    var padCnt = Number(totalLength) - String(this).length;
	    for ( var i = 0; i < padCnt; i++)
	        returnString += String(paddingChar);
	    returnString = this + returnString;
	    return returnString.substring(0, totalLength);
	};

	String.prototype.convertQuotes = function() {

	    var str = String(this);
	    if (str) {
	        str = str.replaceAll("`", "&#39;");
	        str = str.replaceAll("\'", "&#39;");
	        str = str.replaceAll("\"", "&#34;");
	    }
	    return str;
	};

	Number.prototype.zpad = function(len) {
		return this.toString().lpad(len, "0");
	}

	Array.prototype.containsValue = function(compareValue) {
	    for ( var i = 0; i < this.length; i++) {
	        if (this[i] == compareValue)
	            return true;
	    }
	    return false;
	};

	/**
	 * var d = new Date();
	 * alert(d.format("yyyy/MM/dd HH:mm:ss a/p (E)"));
	 *
	 * @param fmt
	 * @returns
	 */
	Date.prototype.format = function(fmt) {
		if (!this.valueOf()) return "";

		var weekName = ["일","월","화","수","목","금","토"];
		var d = this;
		var h = 0;
		return fmt.replace(/(yyyy|yy|MM|dd|E|hh|mm|ss|a\/p|A\/P)/gi,function($1){
			switch($1) {
			case "yyyy": return d.getFullYear();
			case "yy": return (d.getFullYear() % 1000).zpad(2);
			case "MM": return (d.getMonth()+1).zpad(2);
			case "dd": return d.getDate().zpad(2);
			case "E": return weekName[d.getDay()];
			case "HH": return d.getHours().zpad(2);
			case "hh": return ((h = d.getHours() % 12) ? h : 12).zpad(2);
			case "mm": return d.getMinutes().zpad(2);
			case "ss": return d.getSeconds().zpad(2);
			case "a/p": return d.getHours() < 12 ? "am" : "pm";
			case "A/P": return d.getHours() < 12 ? "AM" : "PM";
			default: return $1;
			}
		});
	}

	$.fn.enter = function(handler){
		return this.bind("keyup",function(e){
			if(e.which == 13 && !e.shiftKey && !e.ctrlKey) {
				e.preventDefault();
				if(handler){
					$(this).bind("enter", handler);
					$(this).trigger("enter");
					$(this).unbind("enter");
				}
			}
		});
	};

	$.isMobile = function() {
		var UserAgent = navigator.userAgent;

	    if (UserAgent.match(/iPhone|iPod|Android|Windows CE|BlackBerry|Symbian|Windows Phone|webOS|Opera Mini|Opera Mobi|POLARIS|IEMobile|lgtelecom|nokia|SonyEricsson/i) != null
	    		|| UserAgent.match(/LG|SAMSUNG|Samsung/) != null) {
			return true;
		} else {
			return false;
		}
	}

	$.isBlank = function(str) {
		if (str == undefined) {
			return true;
		}
		return (!str || $.trim(str) === "");
	};

	$.isNotBlank = function(str) {
		return !$.isBlank(str);
	}

	$.isJsonString = function(str) {
	    try {
	        JSON.parse(str);
	    } catch (e) {
	        return false;
	    }
	    return true;
	};
})(jQuery);

$component.resizeGrid = function() {
	var $containers = $(".grid-container");

	$containers.each(function(idx, container){
		var id = $(".ui-jqgrid-btable", container).attr("id");
		var $grid = $("#" + id);
		var cw = $(container).width() - 7;
		setTimeout(resize, 20, $grid, cw, false);
	});

	function resize($grid, cw, tb) {
		$grid.jqGrid("setGridWidth", cw, tb);
	}
}
