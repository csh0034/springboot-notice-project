//엑셀버전
var EXCEL_VER = '2010';

// 달력날짜형식 구분자
var DATE_SEP = '-';
var TIME_SEP = ':';

//경로변수(달력이미지)
try {
	if (contextPath == undefined) {
		contextPath = "";
	}
} catch (e) {
	contextPath = "";
}
var CARLENDAR_IMAGE_PATH = contextPath + '/resources/component-ui/images/icon_calendar.gif';

seriesColors = [
                [ "#FB9D9D", "#A3C8FC", "#FEE090", "#F0D3F8", "#E5EFBC", "#F5C49C", "#C6BBFB", "#A2E697", "#8AE3E4", "#F89DD6", "#ADFFDB"],	//ForegroundColor
                [ "#ffbaba", "#b4d3fe", "#ffe18d", "#f8e7fd", "#e2f49e", "#ffc99c", "#c1b4fe", "#bcf39e", "#9cf7f5", "#f3b4e3", "#9ff9c3"]	//BackgroundColor
               ];

//----------------------------------------------------------------------------------------------------------------------
// Window Onload 처리
//----------------------------------------------------------------------------------------------------------------------
$(window).ready(function(){

    /**
     * Ajax 기본설정
     */
    $.ajaxSetup({
        headers: {
            'ClientInfo': 'PATRICK'
        },
        // Added by yym
        error: function(xhr, textStatus){
        	//cfn_ajaxResponseCheck(xhr, true);
        },
        // Added by yym. jquery.fileupload.js 때문에 생긴 ...
		converters: {
			'iframe json':function(iframe){
				var result = iframe && $.parseJSON($(iframe[0].body).text());
				if (result.httpStatus && result.httpStatus != 200) {
					throw result;
				}
				else if ($.isArray(result) && result.length > 0 && result[0].httpStatus && result[0].httpStatus != 200) {
					var message = [];
                    $.each(result, function(i) {
                        if(this.serviceMessage) {
                            message.push(this.serviceMessage);
                        }
                    });
                    var msg = message.join('\n');
                    var status = result[0].httpStatus;
                    throw {serviceMessage: msg, httpStatus: status};
				}
				return result;
			}
		},
    });

    $.extend(true, $.ajaxOptions, {
        arrValJoin: false,
        validParam: {
            com: function(param) {
                return true;
            },
            biz: function(param) {
                return true;
            }
        },
        beforeEvent: {
            com: function(xhr, settings) {
            },
            biz: function(xhr, settings) {
            }
        },
        completeEvent: {
            com: function(xhr, settings, messages) {
            },
            biz: function(xhr, settings, messages) {
            }
        }
    });

    /* <number 타입기본설정>
     * format
     *  - #: 값이 0일때 빈값으로 설정
     *  - 0: 값이 0일때 0으로 설정
     * negative
     *  - true/false: 음수입력여부
     */
    $.extend(true, $.numberOptions, {
        number: {
            type: 'number',
            format: '#,##0.###',
            negative: false,
            select: true
        },
        integer: {
            type: 'integer',
            format: '#,###',
            negative: true,
            select: true
        },
        decimal: {
            type: 'decimal',
            format: '#,###.##',
            negative: true,
            select: true
        },
        currency: {
            type: 'currency',
            format: '#,##0',
            negative: true,
            select: true
        }
    });
});

/**
 * DateTimePicker 설정
 */
$.datetimepicker = {
	defaults: {
		clickclose: true
	}
}

/**
 * 공통 컴포넌트 모듈
 */

var $component = {};

var blockUI_cnt = 0;
$component.blockUI = function(msg) {
	blockUI_cnt++;
	if (blockUI_cnt > 1) {
		return;
	}
	if(blockUI_cnt <= 0) {
		blockUI_cnt = 1;
	}
    $.blockUI({
        message: msg || '<div class="ft-refresh-cw icon-spin font-medium-2"></div>',
        overlayCSS: {
            backgroundColor: '#FFF',
            opacity: 0.8,
            cursor: 'wait'
        },
        css: {
            border: 0,
            padding: 0,
            backgroundColor: 'transparent'
        }
    });
}

$component.unblockUI = function() {
	blockUI_cnt--;
	if (blockUI_cnt <= 0) {
		blockUI_cnt = 0;
		$.unblockUI();
	}
}

/**
 * 도움말
 */
$(function(){
	$(".component-show-help").click(function(){
		var helpCd = $(this).attr("helpCd");
		$component.get({
			url: contextPath + "/component/help/help_data.do",
			data: {helpCd: helpCd},
			success: function(data, status, xhr) {
				if (data == null || data.helpId == null) {
					alert("도움말이 없습니다.");
					return;
				}
				$component.showHelp(data, true);
			}
		});
	});
});

$component.helpPopup = null;

$component.showHelp = function(data, incViewCnt) {
	if (incViewCnt == null) {
		incViewCnt = true;
	}
	data.incViewCnt = incViewCnt;
	if($component.helpPopup && !$component.helpPopup.closed) {
		$component.helpPopup.close();
	}
	$component.helpPopup = $component.openPopupWindow("about:blank", "help_view", {width: data.popupWidth, height: data.popupHeight, resizable: 1, scrollbars: 1});
	$component.submit({
		target: "help_view",
		url: contextPath + "/component/help/help_popup.do",
		data: data
	});
}

$component.bbs = function($layout, options) {
	var opt = $.extend({
		title: null,
		bbsId: "",
		size: 5,
		layout: {
			title: {
				show: true,
				width: ""
			},
			writer: {
				show: true,
				width: ""
			},
			date: {
				show: true,
				width: "",
				format: "yy-mm-dd"
			},
			showMoreCaption: "more..."
		}
	}, options);

	if (opt.bbsId == null) {
		alert("$component.bbs: <bbsId>를 입력하세요.");
		return;
	}

	drawLayout();
	var liHtml = "";
	if (opt.layout.title.show)
		liHtml += '<p class="patrick-main-bbs-cont ellip"><a href=""></a></p>';
	if (opt.layout.writer.show)
		liHtml += '<p class="patrick-main-bbs-name"></p>';
	if (opt.layout.date.show)
		liHtml += '<p class="patrick-main-bbs-time"></p>';
	var $li = $('<li>' + liHtml + '</li>');
	$component.get({
		url: contextPath + "/component/bbs/" + opt.bbsId + "/article/recent.do?count=" + opt.size,
		success: function(data, status, xhr) {
			var bbs = data.bbs;
			var articles = data.articles;
			if (opt.title == null) {
				$(".patrick-main-bbs-title", $layout).text(bbs.bbsNm);
			} else {
				$(".patrick-main-bbs-title", $layout).text(opt.title);
			}
			var dateFormat = opt.layout.date.format;
			for (var i = 0; i < articles.length; i++) {
				var $cli = $li.clone();
				var date = new Date(articles[i].modifiedDtime);
				$(".patrick-main-bbs-cont a", $cli).text(articles[i].title);
				$(".patrick-main-bbs-cont a", $cli).attr("href", contextPath + "/component/bbs/" + opt.bbsId + "/article/detail.do?articleId=" + articles[i].articleId);

				$(".patrick-main-bbs-time", $cli).text($.datepicker.formatDate(dateFormat, date));
				$(".patrick-main-bbs-name", $cli).text(articles[i].modifierNm);
				$("ul", $layout).append($cli);
			}

			if (opt.layout.title.width != null && opt.layout.title.width != '') {
				$("ul li .patrick-main-bbs-cont", $layout).css("width", opt.layout.title.width);
			}
			if (opt.layout.writer.width != null && opt.layout.writer.width != '') {
				$("ul li .patrick-main-bbs-name", $layout).css("width", opt.layout.writer.width);
			}
			if (opt.layout.date.width != null && opt.layout.date.width != '') {
				$("ul li .patrick-main-bbs-time", $layout).css("width", opt.layout.date.width);
			}

		}
	});

	function drawLayout() {
		$layout.addClass("patrick-main-bbs");
		$layout.html('<p class="patrick-main-bbs-title-wrap"><span class="patrick-main-bbs-title"></span><span class="patrick-main-bbs-more"><a href=""></a></span></p><ul></ul>');
		$("ul", $layout).css("width", "100%");
		$(".patrick-main-bbs-more a").html(opt.layout.showMoreCaption);
		$(".patrick-main-bbs-more a", $layout).attr("href", contextPath + "/component/bbs/" + opt.bbsId + "/article/init.do");
	}
};

$component.setJsonToHtml = function(json, $outline) {
	$("[v-model], input, select, textarea", $outline).each(function(){
		var tag = this.tagName;
		var key = $(this).attr("v-model");
		if (key == null || key == "") {
			if (tag == "INPUT" || tag == "SELECT" || tag == "TEXTAREA") {
				key = $(this).attr("name");
			}
		}
		var val = json[key];
		if (tag == "INPUT") {
			var type = $(this).attr("type");
			if (type == "checkbox" || type == "radio") {
				if ($(this).val() == val)
					$(this).attr("checked", true);
				else
					$(this).attr("checked", false);
			}
			else {
				var edittype = $(this).attr("edittype");
				if (edittype == "currency" || edittype == "number") {
					$(this).val($component.numberFormat(val));
				}
				else if (val == null) {
					$(this).val("");
				}
				else {
					$(this).val(val);
				}
			}
		}
		else if (tag == "SELECT") {
			$(this).val(val);
			$(this).multiselectData(val);
		}
		else if (tag == "TEXTAREA") {
			$(this).val(val);
		}
		else {
			var type = $(this).attr("json-type");
			if (type == undefined || type.length == 0) {
				type = $(this).attr("edittype");
			}
			if (type == "currency" || type == "number") {
				$(this).text($component.numberFormat(val));
			}
			else {
				if (val == null)
					$(this).text("");
				else
					$(this).text(val);
			}
		}
	});
};

/**
 * 레이어 팝업화면 호출
 *
 */
$component.openPopupLayer = function(options) {
	opt = $.extend({
		id:"",
		url:"",
		param: {}, // deprecated. use data
		data: {},
		title:"",
		modal: true,
		remove: true,
		closeonEscape: true,
		callback: function(data) {
			// 팝업창으로부터 데이터를 받는다
		}
	}, options);

	var data = $component.removeEmptyJsonKey(opt.data);
	opt.param = $.extend(opt.param, data);
	cfn_popupLayer(opt);
};

/**
 * 레이어 팝업화면 닫기
 */
$component.closePopupLayer = function(options){
	cfn_popupLayerClose(options);
};

$component.alert = function(msg) {
	if(!msg) {
		msg = "";
	}

	/*

	var html = '<div class="modal animated shake text-left" id="alertwrap" tabindex="-1" role="dialog" aria-labelledby="myModalLabel40" aria-hidden="true">';
		html+= '	<div class="modal-dialog" role="document">';
		html+= '		<div class="modal-content">';
		html+= '			 <div class="modal-header">';
		html+= '				<h4 class="modal-title" id="myModalLabel40">Alert</h4>';
		html+= '				<button type="button" class="close" data-dismiss="modal" aria-label="Close">';
		html+= '					<span aria-hidden="true">&times;</span>';
		html+= '				</button>';
		html+= '			 </div>';
		html+= '			<div class="modal-body"></div>';
		html+= '			<div class="modal-footer">';
		html+= '				<button type="button" class="btn grey btn-outline-secondary" data-dismiss="modal">Ok</button>';
		html+= '			</div>';
		html+= '		</div>';
		html+= '	</div>';
		html+= '</div>';

	var $div = $(html);
	$("body").append($div);

	$("#alertwrap .modal-body").html(msg);
	$("#alertwrap").modal();

	*/

	Swal.fire({
		title: "",
		icon: 'warning',
		html: "<div class='d-inline-block text-left'>" + msg.replaceAll("\n", "<br/>") + "</div>",
		confirmButtonText: "Ok",
		showCloseButton: true
	});
}

/**
 * 오픈 팝업 윈도우
 */
$component.openPopupWindow = function(url, winnm, options) {
	var opt = $.extend({
			width: 100,
			height: 100,
			left: 0,
			top: 0,
			menubar: 0,
			resizable: 0,
			scrollbars: 0,
			status: 1,
			titlebar: 1,
			toolbar: 0,
		},options);

	var optstr = "";
	$.each(opt, function(key, value){
		if (value == true || value == "true" || value == "yes" || value == 1 || value == "1") {
			optstr += key + "=1,";
		}
		else if (value == false || value == "false" || value == "no" || value == 0 || value == "0"){
			optstr += key + "=0,";
		}
		else {
			optstr += key + "=" + value + ",";
		}
	});
	return window.open(url, winnm, optstr);
};

/*
 * 팝업 공지화면을 호출
 *
 */
$component.openNoticePopup = function(popupType) {
	var url = contextPath + "/component/popup/showlist.do?popupType=" + popupType;
	$.ajax({
		async: true,
		url: url,
		cache: false,
		dataType: "json",
		success: function(data, textStatus, jqXHR) {
			for (var i=0; i < data.length; i++) {
				var notShow = $component.popupCookie.isClosedOnToday(data[i]);
				if (notShow)
					continue;
				$component.openPopupLayer({
					id: data[i],
					title: '알림',
					url: contextPath + "/component/popup/show.do",
					param: {popupId: data[i], wid: data[i]},
					modal: true,
					remove: true,
					closeonEscape: false
				});
			}
		}
	});
}

/**
 * 비밀번호 변경 팝업 호출
 */
$component.openModifyPasswordPopup = function () {
    $component.openPopupLayer({
        id: 'modifyPasswordPopup',
        url: contextPath + '/component/password/popup.do',
        param: {
        	wid: 'modifyPasswordPopup'
        },
        title: '비밀번호 변경',
        modal: true,
        remove: true,
        closeOnEscape: true
	});
};


/**
 * 사용자 선택 팝업 호출
 *
 */
$component.openSelectUserPopup = function (options) {
	var opt = $.extend({
		data: {
			loginId: "",
			userNm: "",
			userStatus: "",
			roleCode: "",
			eventEmailRcv: "",
			badMbr: null,
			pstinstCl: "",
			institutionId: "",
			specialtyCd: "",
		},
		callback: function(user) {

		}
	}, options);

	opt.data.wid = "openSelectUserPopup";
    $component.openPopupLayer({
        id: opt.data.wid,
        url: contextPath + '/component/user/member/select.do',
        data: opt.data,
        title: '회원 선택',
        modal: true,
        remove: true,
        width: 800,
        closeOnEscape: true,
        callback: opt.callback
	});
};

$component.openEmployeePopup = function (callback) {
	var wid = "openEmployeePopup";
    $component.openPopupLayer({
        id: wid,
        url: contextPath + '/component/user/select_emp.do',
        param: {
        	wid: wid,
        	multi: true
        },
        title: '임직원 선택',
        modal: true,
        remove: true,
        closeOnEscape: true,
        callback:callback
	});
};

$component.openOneEmployeePopup = function (callback) {
	var wid = "openOneEmployeePopup";
    $component.openPopupLayer({
        id: wid,
        url: contextPath + '/component/user/select_emp.do',
        param: {
        	wid: wid,
        	multi: false
        },
        title: '임직원 선택',
        modal: true,
        remove: true,
        closeOnEscape: true,
        callback:callback
	});
};

$component.itemRect = {
	add: function($parent, options) {
		var opt = $.extend({
			display: "",
			data: {},
			onRemove: function($item, data) {
			}
		}, options);

		var $item = $("<div class='-c-rect-section'><span class='-c-remove-button'> x </span><span class='-c-rect-display'></span></div>");
		$('.-c-rect-display', $item).html(opt.display);
		$item.data('data', opt.data);
		var $input = $("<input />");
		$.each(opt.data, function(key, value) {
			var $cinput = $input.clone();
			$cinput.attr('name', key).attr('value', value).attr("type", "hidden");
			$item.append($cinput);
		});
		$parent.append($item);

		$('.-c-remove-button', $item).click(function(){
			opt.onRemove($item, opt.data);
		});
	},
	data: function($parent) {
		var data = [];
		$(".-c-rect-section", $parent).each(function(){
			var d = $(this).data("data");
			data.push(d);
		});
		return data;
	}

}

$component.removeEmptyJsonKey = function(jsonObj) {
	var obj = $.extend({},jsonObj);
	return $.each(obj, function(key, value){
		if (value === "" || value === null) {
			delete obj[key];
		}
		else if (Object.prototype.toString.call(value) === '[object Object]') {
			$component.removeEmptyJsonKey(value);
		}
		else if (Array.isArray(value)) {
			value.forEach(function(el){
				$component.removeEmptyJsonKey(el);
			});
		}
	});
};

$component.toFormData = function($outline) {

}

$component.param = function($outline) {
	return $.param($component.toJson($outline), true);
}

$component.gridToJson = function($grid, options){
	var opt = $.extend({
		checked: false,		        // true: send checked rows only
		stateFlag:["I","U", "D",""]	// ["I", "U", "D"]
	}, options);

//	$grid.gridEditClose();

	var rows = new Array();
	if (opt.checked) {
		var chkIds = $grid.getCheckedIndex();
		$(chkIds).each(function(i,n) {
            var rowData = $grid.getRowData(n);
            if (rowData.stateFlag != $.jgrid.state.flag_insert) {
                rows.push(rowData);
            }
        });
	}
	else {
		var stFlags = new Array();
        if ($.isArray(opt.stateFlag) && opt.stateFlag.length > 0) {
        	stFlags = opt.stateFlag;
        } else {
        	stFlags = [$.jgrid.state.flag_insert, $.jgrid.state.flag_update, $.jgrid.state.flag_delete];
        }

        if (stFlags.length > 0) {
    		var data = $grid.getRowData();
            $(data).each(function(idx,rowData) {
                for (var j = 0; j < stFlags.length; j++) {
                    if (!rowData.stateFlag) {
                    	rowData.stateFlag = '';
                    }
                    if (stFlags[j] == rowData.stateFlag) {
                        rows.push(rowData);
                    }
                }
            });
        }
	}

	return rows;
};

/**
 * Grid의 변경된 데이터 만을 JSON으로 만들어
 * Request Body에 넣어 post로 전송한다.
 *
 * $grid: Grid jQuery object
 * options: <OPTIONS> 참조
 */
$component.postGridJson = function($grid, options){
	var opt = $.extend({
		addedData: {},
		data: {},
		listPropertyName: "list",   // server parameter list property name
		checked: false,		// true: send checked rows only
		emptyMessage: "처리할 데이터가 없습니다.",
		stateFlag:["I","U", "D",""],		// ["I", "U", "D"]
		url: "",
		cache: false,
		success: function(data, status, xhr) {
		}
	}, options);

	var rows = $component.gridToJson($grid, opt);
	if (rows.length == 0) {
		alert(opt.emptyMessage);
		return;
	}

	var data = {};
	data[opt.listPropertyName] = rows;
	data = $.extend(data, opt.addedData);
	data = $.extend(data, opt.data);
	opt.data = JSON.stringify(data);

	// SEND
	opt = $.extend({
		type: "post",
		dataType: "json",
		contentType: "application/json; charset=utf-8",
        beforeSend: function(xhr, settings) {
        	$(".dev-error").hide();
        	$component.blockUI();
        },
        complete: function(xhr, status) {
        	$component.unblockUI();
        },
        error: function(xhr, status) {
        	$component.checkAjaxError(xhr, status);
        }
	}, opt);

    return $.ajax(opt);
};

/***
 * $component.gridExcelExport() 사용시
 * Excel cell 배경색 변경할 때 사용
 */
$component.gridExcelColor = {
	AUTOMATIC: 64,
	AQUA: 49,
	BLACK: 8,
	BLUE: 12,
	BLUE_GREY: 54,
	BRIGHT_GREEN: 11,
	BROWN: 60,
	CORAL: 29,
	CORNFLOWER_BLUE: 24,
	DARK_BLUE: 18,
	DARK_GREEN: 58,
	DARK_RED: 16,
	DARK_TEAL: 56,
	DARK_YELLOW: 19,
	GOLD: 51,
	GREEN: 17,
	GREY_25_PERCENT: 22,
	GREY_40_PERCENT: 55,
	GREY_50_PERCENT: 23,
	GREY_80_PERCENT: 63,
	INDIGO: 62,
	LAVENDER: 46,
	LEMON_CHIFFON: 26,
	LIGHT_BLUE: 48,
	LIGHT_CORNFLOWER_BLUE: 31,
	LIGHT_GREEN: 42,
	LIGHT_ORANGE: 52,
	LIGHT_TURQUOISE: 41,
	LIGHT_YELLOW: 43,
	LIME: 50,
	MAROON: 25,
	OLIVE_GREEN: 59,
	ORANGE: 53,
	ORCHID: 28,
	PALE_BLUE: 44,
	PINK: 14,
	PLUM: 61,
	RED: 10,
	ROSE: 45,
	ROYAL_BLUE: 30,
	SEA_GREEN: 57,
	SKY_BLUE: 40,
	TAN: 47,
	TEAL: 21,
	TURQUOISE: 15,
	VIOLET: 20,
	WHITE: 9,
	YELLOW: 13,
};

$component.gridExcelExport = function(options) {
	var opt = $.extend({
		caption: "",
		url: contextPath + '/component/excel/gridExcelDownload.do',
		gridId: "",
		fileName: "",
		excludeCol: [],
		showHidden: false
	}, options);

    var detailId = null;
    var records = 0;
    var isRecords = false;
    var exportInfo = null;
    var excelArray = new Array();

    // 다운로드시 제외컬럼 설정
    var excludeCol = opt.excludeCol || [];
    excludeCol.push('rn');
    excludeCol.push('cb');

	detailId = opt.gridId.replace(/#/gi, '');
	records = $('#'+detailId).getGridParam('records');
	if(records > 0) {
		exportInfo = createInfoToExport('#'+detailId, opt.caption, excludeCol);
		exportInfo['showHidden'] = opt.showHidden;
		excelArray.push(exportInfo);
		isRecords = true;
	}

    if(isRecords == false) {
        alert('Excel ' + $.uimsg.warning.noData + ' \n' + $.uimsg.warning.selRetry);
        return;
    }

    var params = new Object();
    params['exportedJson'] = excelArray;

    if(opt.fileName != undefined) {
    	params['downloadName'] = opt.fileName;
    }

    url = opt.url;
    submitDynamicFormFixed('_downloadExcelForm', url, 'post', params);
}

$component.gridExcelImport = function(options) {
	var opt = $.extend({
		url: contextPath + "/component/excel/gridExcelUpload.do",
		gridId: "",
		fileInput: $("input[type=file]"),
		fileInputName: "importedFile",
		data: {},
		startrow: 1,
		autobind: true,
		required: [],
		stateFlag: 'I',
		success: function(data) {

		}
	}, options);

    var fileInput = $(opt.fileInput);
    if (fileInput) {
        if (fileInput.is('[type=file]') == false) {
            return;
        }
    }
    else {
        return;
    }

    fileInput.attr("name", opt.fileInputName);
    var detailId = null;
    if (opt.gridId) {
        detailId = opt.gridId.replace(/#/gi, '');
    }

    if (opt.autobind == undefined) {
    	opt.autobind = true;
    }

    // 엑셀버전 분기
    var url = contextPath + '/common/excel/gridExcelUpload.do';
    if (opt.url) {
        url = opt.url;
    }
    else {
        var ver = (opt.data != undefined && opt.data.ver != undefined) ? opt.data.ver : (typeof(EXCEL_VER) != 'undefined' ? EXCEL_VER : '');
        if (ver == '2007') {

        }
    }

    var importInfo = createInfoToImport('#'+detailId);
    importInfo['startrow'] = opt.startrow;
	importInfo['required'] = opt.required;
	importInfo['stateFlag'] = opt.stateFlag;

	var params = new Object();
	params['importedJson'] = JSON.stringify(importInfo);

	if (opt.data != undefined) {
		for (var name in opt.data) {
			params[name] = opt.data[name];
		}
	}

	$component.upload.init({
		url: url,
		data: params,
		fileInput: fileInput,
		uploadIndicator: $(""),
		beginUpload: function() {
			$component.blockUI();
		},
		endUpload: function() {
			$component.unblockUI();
		},
		success: function(data) {
			if (opt.autobind == true && data) {
				$('#'+detailId).setGridData(data);
			}
			if ($.isFunction(opt.success)) {
				opt.success(data);
			}
		}
	});
}

$component.singleUpload = {
	upload: function($parent, data) {
		var d = $.extend({}, data);
		$parent.fileupload("option", "formData", d);
		$(".startUpload", $parent).click();
	},
	init: function($parent, options){
		var opt = $.extend({
			url: "",
			fileAdded: function(file){

			},
			beginUpload: function() {
				$component.blockUI();
			},
			endUpload: function() {
				$component.unblockUI();
			},
			success: function(data) {}
		},options);

		$parent.append(
				  '<div class="-single-upload-filebox">'
				+ '<span id="filename" class="upload-name input_like">파일을 첨부하세요.</span>'
				+ '<label for="file" class=>파일 첨부</label>'
				+ '<input type="file" id="file" name="file" class="upload-hidden">'
				+ '</div>');

		$(".-single-upload-filebox .upload-hidden", $parent).change(function(){
			var filename = "";
			if(window.FileReader){
				filename = $(this)[0].files[0].name;
			} else {
				filename = $(this).val().split('/').pop().split('\\').pop();
			}

			$(this).siblings('.upload-name').val(filename);
			$("#filename").text(filename);
		});

		$parent.fileupload({
	    	url: opt.url,
	        dataType: 'json',
	        sequentialUploads: true,
	        add: function(e, data) {
	        	$(".startUpload", $parent).remove();
	        	if ($.isFunction(opt.fileAdded)){
	        		opt.fileAdded(data.files[0]);
	        	}
	        	data.context = $("<button class='startUpload' style='display: none;'/>")
	        		.appendTo($parent)
	        		.unbind("click").bind("click", function(){
	        			data.submit();
	        		});
	        },
	        start: function(e){
	        	if ($.isFunction(opt.beginUpload)) {
	        		opt.beginUpload();
	        	}
	        },
	        fail: function(e, data) {
	        	if(data.errorThrown && data.errorThrown.serviceMessage) {
					alert(data.errorThrown.serviceMessage)
				}
	        },
	        done: function(e, data) {
	        	if ($.isFunction(opt.success)) {
	        		opt.success(data.result);
	        	}
	        },
	        always: function(e, data) {
	        	if ($.isFunction(opt.endUpload)) {
	        		opt.endUpload();
	        	}
	        }
	    });
	}
};

$component.upload = {
	showButton: function($parent) {
		$(".-file-upload label.-f-file-button", $parent).show();
		$(".-file-attach a.-f-del-attachment", $parent).show();
		$(".-file-button", $parent).show();
	},
	hideButton: function($parent) {
		$(".-file-upload label.-f-file-button", $parent).hide();
		$(".-file-attach a.-f-del-attachment", $parent).hide();
		$(".-file-button", $parent).hide();
	},
	basic: function($parent, options) {
		var opt = $.extend({
			listURL: "",
			uploadURL: "",
			uploadData:{},
			downloadURL: "",
			removeURL: "",
			removeData:{},
			max: 99999,   /* 첨부가능한 파일 최대개수 */
			fileList: function(list) {

			}
		}, options);

		var html =
            "<div class='-file-upload input_hidden'>" +
		        "<input type='file' name='file' class='fileInput -file-input' multiple  />" +
		        "<label class='input_hidbtn -file-button'>파일 첨부</label>" +
		        "<span class='uploadIndicator'>[ 첨부파일수: <label class='fileCounter'>0</label>건 ]</span>" +
		    "</div>" +
		    "<div class='-file-attach'>" +
		    "</div>";

		$parent.append(html);
		var attachHTML =
			"<div class='-f-file-item -file-item'>" +
				"<a attachmentId='' class='-f-del-attachment' href='#:none;'><font><b>X</b></font></a>" +
				"<a class='-f-download-attachment' href=''></a>" +
			"</div>";
		var $divAttach = $(attachHTML);
		if (opt.downloadURL.indexOf("?") < 0)
			opt.downloadURL += "?";
		else
			opt.downloadURL += "&";

		function addFileItem($parent, $fileItem, data) {
			$fileItem.attr("id", "file-" + data.attachmentId);
			$("a.-f-del-attachment", $fileItem).attr("attachmentId", data.attachmentId);
			$("a.-f-download-attachment", $fileItem).attr("href", opt.downloadURL + "attachmentId=" + data.attachmentId);

			var fileSize = data.fileSize;
			var unit = "Bytes";
			if (Math.floor(data.fileSize / 1024 / 1024) > 0) {
				fileSize = Math.floor(data.fileSize / 1024 / 1024);
				unit = "MB";
			}
			else if (Math.floor(data.fileSize / 1024) > 0) {
				fileSize = Math.floor(data.fileSize / 1024);
				unit = "KB";
			}
			fileSize = $.formatNumber(fileSize, $.numberOptions.number);
			$("a.-f-download-attachment", $fileItem).html(data.filename + " (" + fileSize + " " + unit + ")");
			$parent.append($fileItem);
		}

		// file list
		$.ajax({
			dataType:"json",
			url: opt.listURL,
			cache: false,
			success: function(data){
				for (var i = 0; i < data.length; i++ ) {
					var $file = $divAttach.clone();
					addFileItem($(".-file-attach", $parent), $file, data[i]);
				}
				$(".-file-upload .fileCounter", $parent).text($(".-file-attach div", $parent).length);
				if ($.isFunction(opt.fileList)) {
					opt.fileList(data);
				}
			}
		});

		// 파일 삭제 이벤트
		$(".-file-attach", $parent).delegate('.-f-del-attachment', 'click', function() {
			var attId = $(this).attr('attachmentId');
			if (!confirm("파일을 삭제하시겠습니까?"))
				return;
			$.ajax({
				url: opt.removeURL,
				cache: false,
				data: $.extend({}, opt.removeData, {attachmentId: attId}),
				success: function(data) {
					$(".-file-attach div#file-" + attId, $parent).remove();
					$(".-file-upload .fileCounter", $parent).text($(".-file-attach div").length);
				}
			});
		});

		// 파일을 선택하면, 업로드를 하고 결과를 화면에 표시
		$component.upload.init({
			parent: $parent,
			url: opt.uploadURL,
			data: opt.uploadData,
			max: opt.max,
			fileInput: $("input.fileInput", $parent),
			uploadIndicator: $("span.uploadIndicator", $parent),
			success: function(data) {
				for (var i = 0; i < data.length; i++ ) {
					var $file = $divAttach.clone();
					addFileItem($(".-file-attach", $parent), $file, data[i]);
				}
				$(".-file-upload .fileCounter", $parent).text($(".-file-attach div", $parent).length);
			}
		});

		$(".-file-button", $parent).click(function(){
			var cnt = $(".-file-attach .-file-item", $parent).length;
			if (cnt >= opt.max) {
				alert("파일은 최대 " + opt.max + "개까지만 첨부할 수 있습니다.");
				return
			}
			$(".-file-input", $parent).click();
		});

	},
	list: function($parent, options) {
		var opt = $.extend({
			listURL: "",
			downloadURL: "",
			fileList: function(list) {

			}
		}, options);

		var html =
            "<div id='fileUpload' class='input_hidden'>" +
		    "</div>" +
		    "<div id='fileAttach'>" +
		    "</div>";

		$parent.append(html);
		var attachHTML =
			"<div id='' class='-f-file-item -file-item'>" +
				"<a class='-f-download-attachment' href=''></a>" +
			"</div>";
		var $divAttach = $(attachHTML);
		if (opt.downloadURL.indexOf("?") < 0)
			opt.downloadURL += "?";
		else
			opt.downloadURL += "&";

		function addFileItem($parent, $fileItem, data) {
			$fileItem.attr("id", "file-" + data.attachmentId);
			$("a.-f-download-attachment", $fileItem).attr("href", opt.downloadURL + "attachmentId=" + data.attachmentId);

			var fileSize = Math.floor(data.fileSize / 1024);
			fileSize = $.formatNumber(fileSize, $.numberOptions.number);
			$("a.-f-download-attachment", $fileItem).html(data.filename + " (" + fileSize + "KB)");
			$parent.append($fileItem);
		}

		// file list
		$.ajax({
			dataType:"json",
			url: opt.listURL,
			cache: false,
			success: function(data){
				for (var i = 0; i < data.length; i++ ) {
					var $file = $divAttach.clone();
					addFileItem($("#fileAttach", $parent), $file, data[i]);
				}
				if (data.length == 0) {
					$("#fileAttach", $parent).text("없음");
				}
				if ($.isFunction(opt.fileList)) {
					opt.fileList(data);
				}
			}
		});

	},
	image: function($parent, options) {
		var opt = $.extend({
			listURL: "",
			uploadURL: "",
			uploadData:{},
			downloadURL: "",
			removeURL: "",
			removeData:{},
			max: 99999
		}, options);

		var html =
            "<div class='input_hidden -file-upload'>" +
	        	"<input type='file' name='file' class='fileInput -file-input' multiple  />" +
	        	"<label class='input_hidbtn -file-button'>이미지 첨부</label>" +
	        	"<span class='uploadIndicator'>첨부파일수: <label class='fileCounter'>0</label>건</span>" +
	        "</div>" +
		    "<table class='fileAttach -file-attach'>" +
		    "<thead>" +
		    	"<tr><th style='width:30px;'>삭제</th><th style='width:250px;'>이름</th><th style='width:100%;'>이미지 URL</th></tr>" +
		    "</thead>" +
		    "<tbody></tbody>"
		    "</table>";

		$parent.append(html);
		var attachHTML =
			"<tr class='fileItem -file-item'>" +
				"<td><a attachmentId='' class='delAttachment' href='#:none;'><font><b>X</b></font></a></td>" +
				"<td class='imageNm'><a target='_blank'></a></td>" +
				"<td class='imageUrl'>?</td>" +
			"</tr>";
		var $fileItem = $(attachHTML);
		if (opt.downloadURL.indexOf("?") < 0)
			opt.downloadURL += "?";
		else
			opt.downloadURL += "&";

		function addImageItem($parent, $fileItem, data) {
			$fileItem.addClass("-file-" + data.attachmentId);
			$("a.delAttachment", $fileItem).attr("attachmentId", data.attachmentId);
			var name = data.filename + " (" + data.fileSize + ")"
			var url = opt.downloadURL + "attachmentId=" + data.attachmentId;
			$("td.imageNm a", $fileItem).html(name);
			$("td.imageNm a", $fileItem).attr("href", url);
			$("td.imageUrl", $fileItem).html(url);
			$parent.append($fileItem);
		}

		// file list
		$.ajax({
			dataType:"json",
			url: opt.listURL,
			cache: false,
			success: function(data){
				for (var i = 0; i < data.length; i++ ) {
					var $item = $fileItem.clone();
					addImageItem($(".-file-attach tbody", $parent), $item, data[i]);
				}
				$(".-file-upload .fileCounter", $parent).text($(".-file-attach tbody tr", $parent).length);
			}
		});

		// 파일 삭제 이벤트
		$(".-file-attach", $parent).delegate('.delAttachment', 'click', function() {
			var attId = $(this).attr('attachmentId');
			if (!confirm("파일을 삭제하시겠습니까?"))
				return;
			$.ajax({
				url: opt.removeURL,
				cache: false,
				data: $.extend({}, opt.removeData, {attachmentId: attId}),
				success: function(data) {
					$(".-file-attach .-file-" + attId, $parent).remove();
					$(".-file-upload .fileCounter", $parent).text($(".-file-attach tbody tr", $parent).length);
				}
			});
		});

		// 파일을 선택하면, 업로드를 하고 결과를 화면에 표시
		$component.upload.init({
			parent: $parent,
			url: opt.uploadURL,
			data: opt.uploadData,
			fileInput: $("input.fileInput", $parent),
			uploadIndicator: $("span.uploadIndicator", $parent),
			max: opt.max,
			success: function(data) {
				for (var i = 0; i < data.length; i++ ) {
					var $item = $fileItem.clone();
					addImageItem($(".-file-attach tbody", $parent), $item, data[i]);
				}
				$(".-file-upload .fileCounter", $parent).text($(".-file-attach tbody tr", $parent).length);
			}
		});

		$(".-file-button", $parent).click(function(){
			var cnt = $(".-file-attach .-file-item", $parent).length;
			if (cnt >= opt.max) {
				alert("파일은 최대 " + opt.max + "개까지만 첨부할 수 있습니다.");
				return
			}
			$(".-file-input", $parent).click();
		});

	},
	album: function($parent, options) {
		var opt = $.extend({
			listURL: "",
			uploadURL: "",
			uploadData:{},
			downloadURL: "",
			removeURL: "",
			removeData:{},
			max: 99999,
			fileList: function(list) {

			}
		}, options);

		var html =
            "<div class='input_hidden -file-upload'>" +
		        "<input type='file' name='file' class='fileInput -file-input' multiple  />" +
		        "<label class='input_hidbtn -file-button'>파일 첨부</label>" +
		        "<span class='uploadIndicator'>[ 첨부파일수: <label class='fileCounter'>0</label>건 ]</span>" +
		    "</div>" +
		    "<div class='-file-attach'>" +
		    "</div>";

		$parent.append(html);
		var attachHTML =
			"<div class='-f-file-item -file-item'>" +
				"<a attachmentId='' class='-f-del-attachment' href='#:none;'><font><b>X</b></font></a>" +
				"<img class='-f-download-attachment' src='' />" +
			"</div>";
		var $divAttach = $(attachHTML);
		if (opt.downloadURL.indexOf("?") < 0)
			opt.downloadURL += "?";
		else
			opt.downloadURL += "&";

		function addFileItem($parent, $fileItem, data) {
			$fileItem.addClass("file-" + data.attachmentId);
			$("a.-f-del-attachment", $fileItem).attr("attachmentId", data.attachmentId);
			$("img.-f-download-attachment", $fileItem).attr("src", opt.downloadURL + "attachmentId=" + data.attachmentId);
			$parent.append($fileItem);
		}

		// file list
		$.ajax({
			dataType:"json",
			url: opt.listURL,
			cache: false,
			success: function(data){
				for (var i = 0; i < data.length; i++ ) {
					var $file = $divAttach.clone();
					addFileItem($(".-file-attach", $parent), $file, data[i]);
				}
				$(".-file-upload .fileCounter", $parent).text($(".-file-attach div", $parent).length);
				if ($.isFunction(opt.fileList)) {
					opt.fileList(data);
				}
			}
		});

		// 파일 삭제 이벤트
		$(".-file-attach", $parent).delegate('.-f-del-attachment', 'click', function() {
			var attId = $(this).attr('attachmentId');
			if (!confirm("파일을 삭제하시겠습니까?"))
				return;
			$.ajax({
				url: opt.removeURL,
				cache: false,
				data: $.extend({}, opt.removeData, {attachmentId: attId}),
				success: function(data) {
					$(".-file-attach div.file-" + attId, $parent).remove();
					$(".-file-upload .fileCounter", $parent).text($(".-file-attach div").length);
				}
			});
		});

		$(".-file-attach", $parent).delegate('img.-f-download-attachment', 'click', function() {
			var attachmentId = $(this).parents("div").first().find("a.-f-del-attachment").attr("attachmentId");
			var $html = $("<div><img src='' style='max-width: 1000px; cursor: pointer;'></div>");
			$("img", $html).attr("src", opt.downloadURL + "attachmentId=" + attachmentId);
			$html.dialog({
				resizable: false,
				modal: true,
				width: 100
			});
			$("img", $html).one("load", function(){
				var w = $("img", $html).width() + 30;
				$html.dialog("option", "width", w);
				$html.dialog("option", "position", {at:"center"});
				$("img", $html).click(function(){
					$html.dialog("close");
				});
			});
		});

		// 파일을 선택하면, 업로드를 하고 결과를 화면에 표시
		$component.upload.init({
			parent: $parent,
			url: opt.uploadURL,
			data: opt.uploadData,
			max: opt.max,
			fileInput: $("input.fileInput", $parent),
			uploadIndicator: $("span.uploadIndicator", $parent),
			success: function(data) {
				for (var i = 0; i < data.length; i++ ) {
					var $file = $divAttach.clone();
					addFileItem($(".-file-attach", $parent), $file, data[i]);
				}
				$(".-file-upload .fileCounter", $parent).text($(".-file-attach div", $parent).length);
			}
		});

		$(".-file-button", $parent).click(function(){
			var cnt = $(".-file-attach .-file-item", $parent).length;
			if (cnt >= opt.max) {
				alert("파일은 최대 " + opt.max + "개까지만 첨부할 수 있습니다.");
				return
			}
			$(".-file-input", $parent).click();
		});
	},
	init: function(options){

		var opt = $.extend({
			parent: null,
			url: "",
			data: {},
			max: 99999,
			fileInput: $("input.fileInput[type=file]"),
			uploadIndicator: $("span.upload-indicator"),
			// EVENT
			beginUpload: function() {
				this.uploadIndicator.addClass('ac_loading');
			},
			endUpload: function() {
				this.uploadIndicator.removeClass("ac_loading");
			},
			error: function(data) {
				if(data.errorThrown && data.errorThrown.serviceMessage) {
					alert(data.errorThrown.serviceMessage)
				}
			},
			success: function(data) {

			}
		}, options);

	    opt.fileInput.fileupload({
	    	url: opt.url,
	        dataType: 'json',
	        formData: opt.data,
	        sequentialUploads: true,
	        add: function(e, data) {
	        	if (opt.parent == null) {
	        		data.submit();
	        		return;
	        	}

	        	var cnt = $(".-file-item", opt.parent).length + data.originalFiles.length;
	        	if (cnt > opt.max) {
	        		alert("파일은 최대 " + opt.max + "개까지만 첨부할 수 있습니다.");
	        		data.jqXHR.abort();
	        		return;
	        	}
        		data.submit();
	        },
	        start: function(e, data){
	        	if ($.isFunction(opt.beginUpload)) {
	        		opt.beginUpload();
	        	} else {
	        		this.uploadIndicator.addClass('ac_loading');
	        	}
	        },
	        fail: function(e, data) {
	        	if ($.isFunction(opt.error)) {
	        		opt.error(data);
	        	}
	        },
	        done: function(e, data) {
	            if ($.isFunction(opt.success)) {
	            	opt.success(data.result);
	            }
	        },
	        always: function(e, data) {
	        	if ($.isFunction(opt.endUpload)) {
	        		opt.endUpload();
	        	} else {
	        		this.uploadIndicator.removeClass("ac_loading");
	        	}
	        }
	    });
	}
};

$component.popupCookie = {
	closeOnToday: function(popupId) {
		var midnightDtime = new Date(parseInt(new Date().getTime() / 86400000) * 86400000 + 54000000 - 1000);
		$.cookie("isClosed"+popupId, true, {expires: midnightDtime, path: "/"});
	},
	isClosedOnToday: function(popupId) {
		return $.cookie("isClosed"+popupId);
	}
};

$component.vsplit = function(options){
	var opt = $.extend({
			splitter: $(""),
			target: $(""),
			min: 400,
		}, options);

	opt.splitter.empty();
	opt.splitter.append("<p>아이콘</p>");
	opt.splitter.addClass("splitter");

	// splitter를 drag할 때, floating되면서, width 100% 때문에 크기가 달라지는 현상 막기
	opt.splitter.mousedown(function(){
		var ow = opt.splitter.width();
		opt.splitter.width(ow);
	});

	// splitter 원래 크기 속성으로 돌려놓기
	opt.splitter.mouseup(function(){
		opt.splitter.width("");
	});

	opt.splitter.draggable({
		helper: "clone",
		axis: "y",
		drag: function(event, ui) {
			var h = 0;
			var nh = 0;
			if (opt.target.getGridParam("colModel") != undefined) {
				h = opt.target.getGridParam("height");
				nh = h + (ui.position.top - ui.originalPosition.top);
			}
			else {
				h = opt.target.height();
				nh = h + (ui.position.top - ui.originalPosition.top);
			}

			if (nh < opt.min) {
				ui.position.top = opt.min - h + ui.originalPosition.top;
			}
		},
		stop: function(event, ui) {
			// splitter 원래 크기 속성으로 돌려놓기
			opt.splitter.width("");

			if (opt.target.getGridParam("colModel") != undefined) {
				var h = opt.target.getGridParam("height");
				var nh = h + (ui.position.top - ui.originalPosition.top);
				opt.target.setGridHeight(nh);
			}
			else {
				var h = opt.target.height();
				var nh = h + (ui.position.top - ui.originalPosition.top);
				opt.target.height(nh);
			}
		}
	});
};

/*************************************************************
 *
 * SELECT BOX
 *
 *************************************************************/

$component.select = {};
/**
 * selectbox 내용물 모두 삭제
 */
$component.select.clear = function($selectbox) {
	$selectbox.loadSelect([]);
}

/**
 * values = [{code:코드, codeNm:텍스트}, ...]
 * selectedVal = 선택된 항목으로 표시할 값.
 */
$component.select.loadArray = function($selectbox, values, selectedVal) {
	$selectbox.loadSelect(values, '', selectedVal);
}

/**
 * 서버로부터 데이터를 가져와 채운다.
 * 서버 데이터 형식 = [{code:코드, codeNm:텍스트}, ...]
 */
$component.select.load = function($selectbox, options) {
	var opt = $.extend({
		url: contextPath + '/common/ajax/getSelectData.do',
		data: {},
		success: function(data, status, xhr) {

		}
	}, options);
	$component.get({
		url: opt.url,
		data: opt.data,
		success: function(data, status, xhr) {
			$selectbox.loadSelect(data);
			opt.success(data, status, xhr);
		}
	});
};

/**
 * value: 선택 값. 단일 값 또는 Array
 * value가 없으면, 선택된 값을 얻는 용도로만 사용
 * multi=true인 경우 Array return
 * multi=false인 경우 단일 값 return
 */

$component.select.val = function($selectbox, value) {
	if(value != undefined) {
		$component.select.selectedData($selectbox, value);
	} else {
		return $component.select.selectedData($selectbox);
	}
}

$component.select.selectedData = function($selectbox, value) {
	var $t = $selectbox;

	if(value != undefined) {
		var values = [];
		if (value instanceof Array) {
			values = value;
	    } else {
			values = [value];
	    }

	    if($t.attr('multi') == 'true') {
	        $t.val(null);
	        $t.multiselect('refresh');

	        $.each(values, function(i, val) {
				$t.multiselect("widget").find('input[value='+val+']:checkbox').each(function(){
					if(this.value == val) {
	                    this.click();
	                }
	            });
	        });
	    } else {
			var val = values[0];
	        if (val == '' || val == null) {
	            // 첫번째 옵션값으로 설정
	            var fo = $t.find('option').get(0);
	            if (fo) {
	                val = fo.value;
	            } else {
	                val = '';
	            }
	        }
	        $t.val(val);
	        $t.multiselect('refresh');
	    }
	} else {
		var val = [];
		$t.multiselect("getChecked").map(function(){
			val.push(this.value);
		});
		if($t.attr('multi') == 'true') {
			return val;
		} else {
			if (val.length > 0) {
				return val[0];
			}
			return null;
		}
	}
};

/**
 * 선택된 값 구하기
 * multi=true인 경우 Array return
 * multi=false인 경우 단일 값 return
 */
$component.select.text = function($selectbox) {
	return $component.select.selectedText($selectbox);
}

$component.select.selectedText = function($selectbox) {
	var $t = $selectbox;
    var val = [];
    $t.multiselect("widget").find('ul input:checked').each(function() {
        val.push($(this).parent().find('span').text());
    });
    if($t.attr('multi') == 'true') {
		return val;
	} else {
		if (val.length > 0) {
			return val[0];
		}
		return null;
	}
};

/*************************************************************
*
* datetimepicker
*
*************************************************************/
$component.datetimepicker = {};

$component.datetimepicker.disable = function($dtp) {
	$dtp.attr("disabled", true);
	$dtp.parent().find("span").css("display", "none");
};

$component.datetimepicker.enable = function($dtp) {
	$dtp.attr("disabled", false);
	$dtp.parent().find("span").css("display", "");
}

//----------------------------------------------------------------------------------------------------------------------
// Document 초기화 처리
//----------------------------------------------------------------------------------------------------------------------
function cfn_documentInit() {

    /**
     * 1. Select Element에 val속성으로 초기값설정.
     * 2. mode가 'layer'인 경우 MultiSelect로 설정.
     */
    $('select').each(function(){
        var mode = $(this).attr('mode');
        if(mode == 'layer') {
            $(this).setMultiSelect();
        }
        /*
        else {
            var val = $(this).attr('val');
            $(this).val(val);
        }*/
    });

    /**
     * Input Element에 datepicker를 설정한다.
     */
    $('input[type=text]').each(function() {

        var edittype = $(this).attr('edittype');
        if (edittype != undefined) {
            if(edittype.indexOf('time') != -1 || edittype.indexOf('date') != -1) {
                if ($.datepicker.disable === true) {
                    $(this).setDateTimePicker();
                } else {
                    $(this).setDatePicker();
                }
                $(this).css("ime-mode", "disabled");
            } else if(edittype == 'number') {
                $(this).numberFormat(edittype);
                $(this).css("ime-mode", "disabled");
            } else if(edittype == 'integer') {
                $(this).numberFormat(edittype);
                $(this).css("ime-mode", "disabled");
            } else if(edittype == 'decimal') {
                $(this).numberFormat(edittype);
                $(this).css("ime-mode", "disabled");
            } else if(edittype == 'currency') {
                $(this).numberFormat(edittype);
                $(this).css("ime-mode", "disabled");
            } else if(edittype == 'korean') {
                $(this).keyFilter(/^[가-힣\s]*$/g);
                $(this).css("ime-mode", "active");
            } else if(edittype == 'alpha') {
                $(this).keyFilter(/[a-zA-Z]/g);
                $(this).css("ime-mode", "disabled");
            } else if(edittype == 'alphanum') {
                $(this).keyFilter(/[a-zA-Z0-9]/g);
                $(this).css("ime-mode", "disabled");
            } else if(edittype == 'loweralpha') {
                $(this).keyFilter(/[a-z]/g);
                $(this).css("ime-mode", "disabled");
            } else if(edittype == 'upperalpha') {
                $(this).keyFilter(/[A-Z]/g);
                $(this).css("ime-mode", "disabled");
            } else if(edittype == 'loweralphanum') {
                $(this).keyFilter(/[a-z0-9]/g);
                $(this).css("ime-mode", "disabled");
            } else if(edittype == 'upperalphanum') {
                $(this).keyFilter(/[A-Z0-9]/g);
                $(this).css("ime-mode", "disabled");
            }
        } else {
            /* IME-MODE (auto | normal | active | inactive | disabled | inherit)
             * IE에서만 동작하고, FF/Chrome 에서는 윈도우 자체에 있는 모드를 사용하므로 엘리먼트별로 지정이 불가능함.
             * > auto     : 한/영 중 선택된 모드
             * > active   : default 로 한글모드(IE에서만 됨)
             * > inactive : default 로 영문모드
             * > disable  : 영문만을 사용할 수 있는 모드
             */
            $(this).focusin(function() {
                var imeMode = $(this).attr('imeMode');
                if(!imeMode) imeMode = "inactive";
                $(this).css("ime-mode", imeMode);
            });
            $(this).focusout(function() {
                $(this).css("ime-mode", "");
            });
        }

        // 필수style설정
        if($(this).attr('require') == 'true') {
            $(this).addClass('required');
        }
    });

    /**
     * Input Element에 autocomplete를 설정한다.
     */
    $('input[accept=autocomplete]:text').each(function() {

        $(this).setAutoComplete();
    });

    /**
     * Tab설정 ID가 있는 경우 Tab 설정한다.
     */
    /*
    $('div[id^=tabs]').each(function() {
        $(this).tabs();

        //$('#tabs').tabs("disabled");
        //$('#tabs').tabs({disabled:[0,1,2]});
    });*/

    /**
     * Button Element의 비활성화를 설정한다.
     */
    $('span[id^=btn_]').each(function() {
        $(this).filter('.btn_none').disableObj();
    });
}



//----------------------------------------------------------------------------------------------------------------------
// 팝업관련
//----------------------------------------------------------------------------------------------------------------------
/**
 * 윈도우 닫기
 */
function cfn_closeWindow() {
    var nvua = navigator.userAgent;
    if (nvua.indexOf('MSIE') >= 0){
        if(nvua.indexOf('MSIE 5.0') == -1) {
            top.opener = '';
        }
    } else if (nvua.indexOf('Gecko') >= 0){
        top.name = 'CLOSE_WINDOW';
        wid = window.open('','CLOSE_WINDOW');
    }
    top.close();
}

/**
 * 팝업(윈도우) 열기
 */
function cfn_popupWindow(options) {
    return popupWindow(options);
}

/**
 * 팝업(윈도우) 닫기
 */
function cfn_popupWindowClose(options) {
    popupWindowClose(options);
}

/**
 * 팝업(윈도우) 콜백
 */
function cfn_popupWindowCallback(ret) {
    popupCallback(ret);
}

/**
 * 팝업(레이어) 열기
 */
function cfn_popupLayer(options) {
    return popupLayer(options);
}

/**
 * 팝업(레이어) 닫기
 */
function cfn_popupLayerClose(options) {
    popupLayerClose(options);
}

/**
 * 메세지 레이어(Alert)
 */
function cfn_messageLayer(message, timer) {

    if (message) {
        message = message.replaceAll('\n', '</br>');
    }
    /* timer 기본값 필요시 사용
    if (!timer) {
        timer = 2000;
    }*/

    var $div = $(document.createElement('div'));
    $div.attr('id', 'alertwrap')
        .attr('class', 'alertwrap')
        .hide();

    var cont = '<div class="arttxt">'
             +  message
             + '</div>'
             ;
    $div.append(cont);
    $('body').append($div);

    if ($div.length > 0) {
        /*
        var o = {
        };
        $div.dialog(o);*/

        // 레이어 지정시간후 닫기설정
        var timeout = timer ? setTimeout(function() {
            cfn_popupLayerClose({id: 'alertwrap'});
        }, timer) : null;

        // 레이어 메세지 띄우기
        cfn_popupLayer({
            id: 'alertwrap',
            title: '',
            //width: 782,
            //height: 550,
            //remove: false,
            modal: false,
            remove: true,
            position: ['center', 'center'],
            buttons: {
                확인: function() {
                    //$( this ).dialog( "close" );
                    cfn_popupLayerClose({id: 'alertwrap'});
                }
            },
            open: function(event, ui) {
                var t = this;
                //$(t).prev().find('a.ui-dialog-titlebar-close').hide();
                $(t).prev().hide();
                $(t).find('.imgbtn').click(function() {
                    //$(t).dialog('close');
                    cfn_popupLayerClose({id: 'alertwrap'});
                    clearTimeout(timeout);
                });
            },
            close: function(event, ui) {
                clearTimeout(timeout);
            }
        });
    }
}

var commonjs = {

	ajaxResponseCheck : function(xhr, showmsg) {

	}

};

//----------------------------------------------------------------------------------------------------------------------
// Ajax 관련
//----------------------------------------------------------------------------------------------------------------------
/*
 * Ajax 결과값 체크
 * - 서버오류 및 세션만료시 메세지를 설정하여 오류페이지 출력
 */
function cfn_ajaxResponseCheck(xhr, showMsg) {

    if(xhr.status == 0 && xhr.statusText == "error") {
    	$component.alert("Connection refused.");
    	return;
    }

    if (xhr == null || xhr == undefined) return;

    if (xhr.readyState == 4) {
        var msgs = null;
        var errs = null;
        var xhrs = xhr.status;
        var errormsg = [];
        var jsonData = {};
        var popupMessage = null;

        if (xhrs == 200) {
            return;
        } else if (xhrs == 400) {
        	msgs = xhr.responseText;
        	errs = '올바르지 않은 사용법입니다.';
        } else if(xhrs == 401) {
            msgs = xhr.responseText;
            errs = '로그인을 하세요.';
        } else if(xhrs == 403) {
            msgs = xhr.responseText;
            errs = '권한이 없습니다.';
        } else if(xhrs == 404) {
            msgs = xhr.responseText;
            errs = '지원하지 않는 기능입니다.';
        } else if(xhrs == 500) {
            msgs = xhr.responseText;
            errs = '시스템 오류로 작업을 중단하였습니다.';
        } else if(xhrs == 502) {
            msgs = xhr.responseText;
            errs = '네트워크오류 발생';
        } else if(xhrs == 503) {
        	msgs = xhr.responseText;
            errs = 'Service Unavailable';
        } else if(xhrs >= 400) {
            msgs = xhr.responseText;
            errs = 'HttpStatus[' + xhrs + ']: 오류';
        }

        if (msgs != null && typeof msgs == 'string') {
            try {
                jsonData = $.parseJSON(msgs);
                if(typeof jsonData == 'object') {
                    if($.isArray(jsonData)) {
                        $.each(jsonData, function(i) {
                            if(this.message) {
                                errormsg.push(this.message);
                            }
                        });
                    }
                    else if ($.isNotBlank(jsonData.message)) {
                    	errormsg.push(jsonData.message);
                    } else {
                    	errormsg.push(errs);
                    }
                }

            } catch(_ex) {

	            if (xhrs == 400) {
	            	errormsg.push(errs);
	            } else if(xhrs == 401) {
	            	errormsg.push(errs);
	            } else if(xhrs == 403) {
	            	errormsg.push(errs);
	            } else if(xhrs == 404) {
	            	errormsg.push(errs);
	            } else if(xhrs == 500) {
	            	errormsg.push(errs);
	            }
            } finally {
                msgs = null;
                jsonData = null;
            }
        }

        if (errormsg.length > 0) {
            if(showMsg) {
            	$component.alert(errormsg.join('\n'));
            }
            errormsg = null;
        }
    }
    else {
        // 예외메시지
        if (xhr.statusText == 'timeout') {
            //alert('네트워크를 연결할 수 없습니다. \n잠시 후 다시 시도해주세요.(timeout)');
        	$component.alert('네트워크를 연결할 수 없습니다.\n잠시 후 다시 시도해주세요.(timeout)');
        }
    }
}



//----------------------------------------------------------------------------------------------------------------------
//	HTML5
//----------------------------------------------------------------------------------------------------------------------

$component.html5 = {};
$component.html5.upload = {};

$component.html5.postMultipart = function($form, options){
	var opt = $.extend({
		url: "",
		data:{},
		success: function(data, status, xhr) {
		}
	}, options);

	var formData = new FormData($form[0]);
	var data = $.extend({}, opt.data);
	$.each(data, function(key, value){
		if (value === "" || value === null) {
		}
		else {
			formData.append(key, value);
		}
	});
	$.ajax({
		url: opt.url,
		cache: false,
		data: formData,
		processData: false,
		contentType: false,
		type: "post",
        beforeSend: function(xhr, settings) {
        	$component.blockUI();
        },
        complete: function(xhr, status) {
        	$component.unblockUI();
        },
		success: function(data, status, xhr) {
			if ($.isFunction(opt.success)) {
				opt.success(data, status, xhr);
			}
		}
	});
};

$component.html5.upload.post = function($form, options) {
	$component.html5.postMultipart($form, options);
}

$component.html5.upload.basic = function($outline, options){

	if (arguments.length > 2) {
		var name = arguments[1];
		var value = arguments[2];
		if (name == "editable") {
			editable(value);
		}
		return;
	}

	if ($outline.length > 1) {
		alert("(Error)\n$component.html5.upload.basic()은 target이 하나이어야 합니다.");
		return;
	}

	var opt = $.extend({
		editable: true,
		name: "file",
		url: "",
		success: function(data, status, xhr) {
			return [{downloadUrl: "", removeUrl: ""}];
		}
	}, options);

	/*
	 *  LAYOUT
	 */

	$outline.addClass("-filebox");
	$outline.append("<label class='-file-add'>파일 추가</label>");
	$outline.append("<span class='-file-counter'>[ 첨부파일수: <label>0</label>건 ]</span>");
	$outline.append("<div class='-file-list'></div>");
	$outline.append("<div class='-file-attach'></div>");

	var $fileInput = $("<input type='file' name='' style='display:none;'>");
	$fileInput.change(fileChanged);
	$outline.append($fileInput);

	var $selectedFilebox = null;

	$(".-file-add", $outline).click(function(){
		$selectedFilebox = $(this).parents(".-filebox:first");
		$fileInput.click();
	});

	function fileChanged() {
		var $item = $(
				'<div class="-file-item">' +
				'	<a class="-file-del" href="#:none;"><font><b>X</b></font></a>' +
				'	<span class="-file-name">파일을 첨부하세요.</span>' +
				'</div>');


		/*
		 * <file>은 clone을 해도, 선택된 파일 정보가 복사되지 않기 때문에,
		 * 원래 선택한 file을 item에 넣고, clone을 fileInput으로
		 */
		var $fclone = $fileInput.clone();
		$fileInput.attr('name', opt.name);
		$item.append($fileInput);
		$selectedFilebox.find(".-file-attach").append($item);

		$fileInput = $fclone;
		$fileInput.change(fileChanged);
		$outline.append($fileInput);

		var filename = $(this)[0].files[0].name;
		var size = $component.getFileSizeString($(this)[0].files[0].size);

		$item.find(".-file-name").text(filename + "(" + size +")");
	}

	$(".-file-attach", $outline).delegate(".-file-del", "click", function(){
		$(this).parents(".-file-item:first").remove();
	});

	/*
	 *  LIST
	 */
	if (opt.url == null || opt.url.trim() == '') {
		return;
	}

	editable(opt.editable);
	$component.get({
		url: opt.url,
		success: function(data, status, xhr) {
			if (!$.isFunction(opt.success)) {
				return;
			}
			dataUrls = opt.success(data);
			drawFileItem(dataUrls);
			editable(opt.editable);
		}
	});

	var $item = $(
			"<div class='-file-item'>" +
				"<a class='-file-del' href='#:none;'><font><b>X</b></font></a>" +
				"<a class='-file-download' href=''></a>" +
			"</div>");

	function drawFileItem(dataUrls) {
		for (var i = 0; i < dataUrls.length; i++ ) {
			var $file = $item.clone();
			addFileItem($(".-file-list", $outline), $file, dataUrls[i]);
		}
		$(".-file-counter label", $outline).text(dataUrls.length);
	}

	function addFileItem($parent, $fileItem, fileInfo) {
		var data = fileInfo;
		$("a.-file-del", $fileItem).attr("url", data.removeUrl);
		$("a.-file-download", $fileItem).attr("href", data.downloadUrl);

		var fileSize = $component.getFileSizeString(data.fileSize);
		$("a.-file-download", $fileItem).html(data.filename + " (" + fileSize + ")");
		$parent.append($fileItem);
	}

	$(".-file-list", $outline).delegate('a.-file-del', 'click', function() {
		var self = this;
		var url = $(this).attr('url');
		if (!confirm("파일을 삭제하시겠습니까?"))
			return;
		$component.get({
			url: url,
			success: function(data, status, xhr) {
				$(self).parent().remove();
				$(".-file-counter label", $outline).text($(".-file-list div", $outline).length);
			}
		});
	});

	function editable(edit) {
		if (edit) {
			$(".-file-add", $outline).show();
			$(".-file-del", $outline).show();
			$(".-file-attach", $outline).show();
		}
		else {
			$(".-file-add", $outline).hide();
			$(".-file-del", $outline).hide();
			$(".-file-attach", $outline).empty();
			$(".-file-attach", $outline).hide();
		}

	}
};

$component.html5.upload.single = function($outline, options) {

	var opt = $.extend({
		inputName: "file",
		buttonName: "파일 첨부",
		comment: "파일을 첨부하세요",
		success: function(data, status, xhr) {
			return [{downloadUrl: "", removeUrl: ""}];
		}
	}, options);


	$outline.append(
			  '<div class="-single-upload-filebox">'
			+ '<span class="upload-name input_like">' + opt.comment  + '</span>'
			+ '<label class="-file-add">' + opt.buttonName + '</label>'
			+ '</div>');
	var $fileInput = $('<input type="file" name="' + opt.inputName + '" style="display:none;">');

	$fileInput.change( function() {
		var filename = $(this)[0].files[0].name;
		var size = $component.getFileSizeString($(this)[0].files[0].size);
		$outline.find(".upload-name").text(filename + "(" + size +")");
	});

	$outline.append($fileInput);

	$(".-file-add", $outline).click(function(){
		$fileInput.click();
	});
};

/**
 * YOUNG-MIN YU
 *
 * Encoding: UTF-8
 */

var $patrick = {};

$patrick.multipart = {};
$patrick.multipart.post = function($form, options){
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

	$.each(opt.files, function(idx, file){
		formData.append(opt.fileParameter, file);
	});

	var $progress = $(
			"<div class='ui-corner-all' tabindex='-1'  style='height: 20px; width: 300px; z-index: 99999; padding: 0; position: absolute; display: none; border: 1px solid gray; background-color: #fff;'>" +
			"<div class='bar ui-corner-all' style='background-color: #ddd; width: 0%; height: 100%; '></div>" +
			"</div>");

	$("body").append($progress);

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
					var total = event.total;
					if (total == 0) {
						$progress.remove();
						return;
					} else {
			        	$progress.show();
					}
					var percent = 0;
					var position =  event.loaded || event.position;
					percent = Math.ceil(position * 100 / total);
					$(".bar", $progress).css("width", percent + "%");
					if( percent >= 100 ) {
						$progress.remove();
					}
				}, false);
			}
			return myXhr;
		},
        beforeSend: function(xhr, settings) {
        	$(".alert").removeClass("on");

        	$component.blockUI();
        	var $block = $(".blockUI.blockMsg.blockPage");
        	var pos = $block.position();
        	var left = pos.left + $block.outerWidth() / 2;
        	var top = pos.top + $block.outerHeight() * 2;
        	left = left - $progress.width() / 2;
        	$progress.css({top: top, left: left});
        },
        complete: function(xhr, status) {
        	$component.unblockUI();
        },
		success: function(data, status, xhr) {
			$progress.remove();
			if ($.isFunction(opt.success)) {
				opt.success(data, status, xhr);
			}
		}
	});
};


$patrick.multipart.filezone = function($filezone, options){
	var defaults = {
		editable: true,  // can add files, can remove files
		fileListUrl: null,  // file list URL
		download: function(comtAttachment) { // called when user clicks file item

		},
		remove: function(comtAttachment, callback) { // called when user clicks file remove icon

		},
		success: function(data, status, xhr) { // called when file list successfuly loaded

		}
	};

	if (arguments.length > 2) {
		var name = arguments[1];
		var value = arguments[2];
		if (name == "editable") {
			editable(value);
		}
		return;
	}

	if ($filezone.length > 1) {
		alert("(Error)\n$component.html5.upload.basic()은 $outline이 하나이어야 합니다.");
		return;
	}

	var opt = $.extend(defaults, options);

	/*
	 *  LAYOUT
	 */

	$filezone.empty();
	$filezone.addClass("-filebox");
	$filezone.append("<label class='-file-add'>파일 추가</label>");
	$filezone.append("<span class='-file-counter'>[ 첨부파일 수: <label>0</label>건 ]</span>");
	$filezone.append("<div class='-file-list'></div>");
	$filezone.append("<div class='-file-attach'></div>");

	var fileInputHtml = "<input type='file' name='' multiple='multiple' style='display:none;'>";
	var $fileInput = $(fileInputHtml);
	$fileInput.change(fileChanged);
	$filezone.append($fileInput);

	var attachments = [];
	$filezone.data("attachments", attachments);
	$filezone.data("options", opt);

	var $selectedFilebox = null;

	$(".-file-add", $filezone).click(function(){
		$selectedFilebox = $(this).parents(".-filebox:first");
		$fileInput.click();
	});

	// 파일이 추가되면
	function fileChanged(event) {
		var $item = $(
				'<div class="-file-item">' +
				'	<a class="-file-del" href="#:none;"><font><b>X</b></font></a>' +
				'	<span class="-file-name">파일을 첨부하세요.</span>' +
				'</div>');

		var fs = event.originalEvent.target.files || event.originalEvent.dataTransfer.files;

		$fileInput.remove();
		$fileInput = $(fileInputHtml);
		$fileInput.change(fileChanged);
		$filezone.append($fileInput);

		if(event.originalEvent.target.files) {
			event.originalEvent.target.files = null;
		}
		else if (event.originalEvent.dataTransfer.files) {
			event.originalEvent.dataTransfer.files = null;
		}

		$.each(fs, function(idx, file){
			var exists = false;
			for (var i=0; i < attachments.length; i++) {
				if (attachments[i].name == file.name && attachments[i].size == file.size) {
					exists = true;
					break;
				}
			}
			if (!exists) {
				attachments.push(file);

				var $f = $item.clone();
				$selectedFilebox.find(".-file-attach").append($f);

				var filename = file.name;
				var size = $component.getFileSizeString(file.size);
				$f.find(".-file-name").text(filename + "(" + size +")");
				$f.data("attachment", file);
			}
 		});

		refreshCounter();
	}

	// 추가된 파일 삭제
	$(".-file-attach", $filezone).delegate(".-file-del", "click", function(){
		var $item = $(this).parents(".-file-item:first");
		var attachment = $item.data("attachment");
		var attachments = $filezone.data("attachments");
		$.each(attachments, function(idx, file) {
			if (attachment == file) {
				attachments.splice(idx, 1);
				return false;
			}
		});
		$item.remove();
		refreshCounter();
	});

	/*
	 *  LIST
	 */
	if (opt.fileListUrl == null || opt.fileListUrl.trim() == '') {
		return;
	}
	editable(opt.editable);
	$component.get({
		url: opt.fileListUrl,
		success: function(data, status, xhr) {
			drawFileList(data);
			editable(opt.editable);
			if ($.isFunction(opt.success)) {
				opt.success(data, status, xhr)
			}
		}
	});

	var $item = $(
			"<div class='-file-item'>" +
				"<label class='-file-del' style='cursor: pointer;'><font><b>X</b></font></label>" +
				"<label class='-file-download' style='cursor: pointer;'></label>" +
			"</div>");

	function refreshCounter() {
		var cnt = $filezone.find(".-file-item").length;
		$(".-file-counter label", $filezone).text(cnt);
	}

	function drawFileList(comtAttachments) {
		for (var i = 0; i < comtAttachments.length; i++ ) {
			var $file = $item.clone();
			addFileItem($(".-file-list", $filezone), $file, comtAttachments[i]);
		}
		refreshCounter();
	}

	// 파일목록 출력
	function addFileItem($parent, $fileItem, comtAttachment) {
		var data = comtAttachment;
		$(".-file-del", $fileItem).click(function(){
			if (!confirm("파일을 삭제하시겠습니까?")) {
				return;
			}
			if ($.isFunction(opt.remove)) {
				opt.remove(comtAttachment, function(){
					$fileItem.remove();
					refreshCounter();
				});
			}
		});
		$(".-file-download", $fileItem).click(function(){
			if ($.isFunction(opt.download)) {
				opt.download(comtAttachment);
			}
		});

		var fileSize = $component.getFileSizeString(data.fileSize);
		$(".-file-download", $fileItem).html(data.fileNm + " (" + fileSize + ")");
		$parent.append($fileItem);
	}

	function editable(edit) {
		if (edit) {
			$(".-file-add", $filezone).show();
			$(".-file-del", $filezone).show();
			$(".-file-attach", $filezone).show();
		}
		else {
			$(".-file-add", $filezone).hide();
			$(".-file-del", $filezone).hide();
			$(".-file-attach", $filezone).empty();
			$(".-file-attach", $filezone).hide();
		}

	}
};

$patrick.multipart.singleFilezone = function($parent, options){
	var opt = $.extend({
		name: "file",
		buttonLabel: "파일 첨부",
		change: function(file) {

		}
	}, options);

	$parent.empty();
	var fileId = "file-" + $component.getNewId();
	$parent.append(
			  '<div class="-single-upload-filebox">'
			+ '<span class="input_like filename">파일을 첨부하세요.</span>'
			+ '<label for="' + fileId + '" class=>' + opt.buttonLabel + '</label>'
			+ '<input type="file" id="' + fileId + '" name="' + opt.name + '" class="upload-hidden">'
			+ '</div>');

	$parent.data("attachments", []);
	$(".-single-upload-filebox .upload-hidden", $parent).change(function(event){
		var fs = event.originalEvent.target.files || event.originalEvent.dataTransfer.files;
		var file = fs[0];
		$parent.data("attachments", [file]);
		$parent.find(".filename").text(file.name);

		if ($.isFunction(opt.change)) {
			opt.change(file);
		}
	});
};

$patrick.multipart.getFiles = function($outline) {
	return $outline.data("attachments");
};