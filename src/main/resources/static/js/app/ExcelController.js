
'use strict'
var module = angular.module('demo.controllers', []);
module.controller("ExcelController", ["$scope","$http" ,"ExcelService","altaFileSvcs",
	function($scope,$http, UserService,altaFileSvcs) {

	$scope.image = null;
	$scope.imageFileName = '';

	$scope.uploadme = {};
	$scope.uploadme.src = '';

	$scope.file1 = null;
	$scope.file2 = null; 

	$scope.file1ColNames = [];
	$scope.file2ColNames = [];

	$scope.columnOrder="";
	$scope.fileTypes=[{type : "xlsx" , mimeType : "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},{type : "csv",mimeType : "text/csv"},{type : "txt",mimeType : "text/plain"}];
	$scope.typeName=null;

	$scope.delimiters=["|","*"];
	$scope.delimiter="";

	var del="";


	// to convert dataURI to blob
	$scope.delimiterChange = function(delm){
		del = delm;
	}

	$scope.downloadOutputFile = function(typeName){

		console.log("*********Download Output File ***********");
		console.log("File 1 : "+$scope.file1);
		console.log("File 2 : "+$scope.file2);
		console.log("columnOrder : "+$scope.columnOrder);
		console.log("file type : "+typeName);
		console.log(typeof typeName);
		console.log("delimiter : "+del);

		

		if($scope.file1 == null || $scope.file2 == null){
			alert("Please upload both excel file");
			return;
		}else if($scope.columnOrder == ""){
			alert("Please enter the column Order");
			return;
		}else if(typeName == null || typeName== ""){
			alert("Please select file type");
			return;
		}
		
		var typeJson = angular.fromJson(typeName);
	    if(del == "" && typeJson.type == "txt" ){
			alert("Please select delimiter");
			return;
		}


		var formdata = new FormData();
		formdata.append('file1', $scope.file1);
		formdata.append('file2', $scope.file2);
		formdata.append('empName', "prakhar verma");
		formdata.append('fileType', typeJson.type);
		formdata.append('mimeType', typeJson.mimeType);
		formdata.append('delimiter', del);


		var colOrders = $scope.columnOrder.split(',');
		formdata.append('colOrder', colOrders);

		$http.post('/excel' ,formdata ,{ 
			transformRequest: angular.identity ,
			headers: { 'Content-Type': undefined } 
		} ).then(function (result) {
			console.log("***********Get Column Success Response*********");	
			console.log(result);
			altaFileSvcs.initDownload(result.data.data.file, result.data.data.mimeType, result.data.data.fileName);
			console.log("File download comlpeted");

		}, function (result) {
			console.log("***********Get Column Error Response*********");	
			console.log(result);
			alert("There some problem while processing the request \n\n"+result.data.message);

		});

		console.log("*********after Download Output File ***********");

	}

	$scope.isDelimiterShow = function(typeName){

		console.log(typeName);
		console.log("-------"+typeof typeName);
		
		if(typeName == null || typeName== ""){
			return false;
		}
		if( typeName != undefined ){
			console.log("inside ");
			var typeJson = angular.fromJson(typeName);
			if(typeJson != null){
				if(typeJson.type == 'txt'){
					return true;
				}	
			}

		}

		return false;
	}
//	UserService.getUserById(1).then(function(value) {
//	console.log(value.data);
//	}, function(reason) {
//	console.log("error occured");
//	}, function(value) {
//	console.log("no callback");
//	});


//	$scope.saveUser = function() {


//	$scope.userDto.skillDtos = $scope.skills.map(skill => {
//	return {
//	skillId: null,
//	skillName: skill
//	};
//	});
//	UserService.saveUser($scope.userDto).then(function() {
//	console.log("works");
//	UserService.getAllUsers().then(function(value) {
//	$scope.allUsers = value.data;
//	}, function(reason) {
//	console.log("error occured");
//	}, function(value) {
//	console.log("no callback");
//	});
//	$scope.skills = [];
//	$scope.userDto = {
//	userId: null,
//	userName: null,
//	skillDtos: []
//	};
//	}, function(reason) {
//	console.log("error occured");
//	}, function(value) {
//	console.log("no callback");
//	});
//	}
}
]);


module.directive('fileDropzone',  ['$http', function($http) {
	return {
		restrict: 'A',
		scope: {
			file: '=',
			fileName: '=',
			fileOneCols: '=',
			fileTwoCols: '=',
			fileOne: '=',
			fileTwo: '='
		},
		link: function(scope,element, attrs) {
			var checkSize,
			isTypeValid,
			processDragOverOrEnter,
			validMimeTypes;

			processDragOverOrEnter = function (event) {
				if (event != null) {
					event.preventDefault();
				}
				event.dataTransfer.effectAllowed = 'copy';
				return false;
			};

			validMimeTypes = attrs.fileDropzone;

			checkSize = function(size) {
				var _ref;
				if (((_ref = attrs.maxFileSize) === (void 0) || _ref === '') || (size / 1024) / 1024 < attrs.maxFileSize) {
					return true;
				} else {
					alert("File must be smaller than " + attrs.maxFileSize + " MB");
					return false;
				}
			};

			isTypeValid = function(type) {
				
//				console.log("tttttt");
//				console.log(type);
//				if ((validMimeTypes === (void 0) || validMimeTypes === '') || validMimeTypes.indexOf(type) > -1) {
//					return true;
//				}
//					else {
//						alert("Invalid file type.  File must be one of following types " + validMimeTypes);
//						return false;
//					}
			};

			element.bind('dragover', processDragOverOrEnter);
			element.bind('dragenter', processDragOverOrEnter);

			return element.bind('drop', function(event) {
				var file, name, reader, size, type;
				if (event != null) {
					event.preventDefault();
				}
				reader = new FileReader();
				reader.onload = function(evt) {
					if (checkSize(size) && isTypeValid(type)) {
						return scope.$apply(function() {
							scope.file = evt.target.result;

							if (angular.isString(scope.fileName)) {
								return scope.fileName = name;
							}
						});
					}
				};
				

				if(scope.fileOne != null && scope.fileTwo != null){
					alert("Both file is uploaded.\n If u want to modify any one\n Refresh the page and upload again");
					return;
				}

				file = event.dataTransfer.files[0];
				name = file.name;
				type = file.type;
				size = file.size;
				reader.readAsDataURL(file);

				console.log("tttttt");
				console.log(type);
				if (!((validMimeTypes === (void 0) || validMimeTypes === '') || validMimeTypes.indexOf(type) > -1)) {
					alert("Invalid file type.  File must be one of following types " + validMimeTypes);
					return;
     			}

				console.log(file);

				var formdata = new FormData();
				formdata.append('file', file);
				formdata.append('empName', "prakhar verma");

				console.log(formdata);

				$http.post('/file/columns?currentHead='+scope.fileOneCols.length ,formdata ,{ 
					transformRequest: angular.identity ,
					headers: { 'Content-Type': undefined } 
				} ).then(function (result) {
					console.log("***********Get Column Success Response*********");	
					console.log(result);

					if(scope.fileOne == null){

						scope.fileOne = file;
						scope.fileOneCols = [];

						angular.forEach(result.data.data, function(value) {
							scope.fileOneCols.push(value);
						});

					}else if(scope.fileTwo == null){
						scope.fileTwo = file;
						scope.fileTwoCols = [];
						angular.forEach(result.data.data, function(value) {
							scope.fileTwoCols.push(value);
						});
					}

				}, function (result) {
					console.log("***********Get Column Error Response*********");	
					console.log(result);
					alert("There some problem while processing the request \n\n"+result.data.message);

				});

				return false;
			});
		}
	};
}])


.directive("fileread", [function () {
	return {
		scope: {
			fileread: "="
		},
		link: function (scope, element, attributes) {
			element.bind("change", function (changeEvent) {
				var reader = new FileReader();
				reader.onload = function (loadEvent) {
					scope.$apply(function () {
						scope.fileread = loadEvent.target.result;
					});
				}
				reader.readAsDataURL(changeEvent.target.files[0]);
			});
		}
	}
}]);





module.factory('altaFileSvcs', ['$document', '$timeout', function ($document, $timeout) {


	function isFileAPIAvailable() {
		// Check for the various File API support.
		if (window.File && window.FileReader && window.FileList && window.Blob) {
			// Great success! All the File APIs are supported.
			return true;
		} else {
			// source: File API availability - http://caniuse.com/#feat=fileapi
			// source: <output> availability - http://html5doctor.com/the-output-element/
			document.writeln('The HTML5 APIs used in this form are only available in the following browsers:<br />');
			// 6.0 File API & 13.0 <output>
			document.writeln(' - Google Chrome: 13.0 or later<br />');
			// 3.6 File API & 6.0 <output>
			document.writeln(' - Mozilla Firefox: 6.0 or later<br />');
			// 10.0 File API & 10.0 <output>
			document.writeln(' - Internet Explorer: Not supported (partial support expected in 10.0)<br />');
			// ? File API & 5.1 <output>
			document.writeln(' - Safari: Not supported<br />');
			// ? File API & 9.2 <output>
			document.writeln(' - Opera: Not supported');
			return false;
		}
	}

	// to convert dataURI to blob
	function base64StringtoBlob(base64data, mimeString) {
		// convert base64 to raw binary data held in a string
		base64data = base64data.replace(/\s/g, '');
		var byteString = window.atob(base64data), array = [];
		for (var i = 0; i < byteString.length; i++) {
			array.push(byteString.charCodeAt(i));
		}
		return new Blob([new Uint8Array(array)], {type: mimeString});
	};
	/*
     var getFileObject = function (filePk, fileName, isArchive) {
     var requestMap = {
     queryParamHash : {
     actionID : "FNPageLoadBean_entityData",
     headerID : filePk,
     entityName : "EOFile"
     }
     };
     altaAjaxSvcs.postAuthAjax(requestMap, function(data){
     if(!data.responseMsg.isError){
     var responseResult = data.responseResult.result.data;
     var fileObject = responseResult.file;
     var mimeType = responseResult.type.split(':')[1].split(';')[0];
     initDownload(fileObject,mimeType,fileName);
     };
     });
     };*/


	// for downloading..
	function initDownload(fileObject, mimeString, fileName) {
		// separate out the mime component
		//var mimeString = $scope.objectHashData.eoFile_type.split(':')[1].split(';')[0];
		var blob = base64StringtoBlob(fileObject, mimeString);


		if (window.navigator.msSaveOrOpenBlob) {
			navigator.msSaveBlob(blob, fileName);
		} else {
			var downloadLink = angular.element('<a></a>');
			downloadLink.attr('href', window.URL.createObjectURL(blob));
			downloadLink.attr('download', fileName);

			$document.find('body').append(downloadLink);
			$timeout(function () {
				downloadLink[0].click();
				downloadLink.remove();
			}, null);
		}
	};
	var baseTypes = "|image|video|audio|text|";
	var fileTypes = {
			"pdf": "pdf",
			"excel": "|vnd.ms-excel|vnd.openxmlformats-officedocument.spreadsheetml.sheet|vnd.openxmlformats-officedocument.spreadsheetml.template|csv|comma-separated-values|vnd.msexcel|excel|",
			"powerpoint": "|vnd.ms-powerpoint|vnd.openxmlformats-officedocument.presentationml.presentation|vnd.openxmlformats-officedocument.presentationml.template|vnd.openxmlformats-officedocument.presentationml.slideshow|",
			"text": "|rtf|x-rtf|richtext|",
			"word": "|msword|vnd.openxmlformats-officedocument.wordprocessingml.document|vnd.openxmlformats-officedocument.wordprocessingml.template|",
			"zip": "|x-compressed|x-compress|x-rar-compressed|zip|x-tar|x-compressed-zip|"
	};
	var fileIconHash = {
			"image": "fa-file-image-o blue",
			"video": "fa-file-video-o blue",
			"audio": "fa-file-audio-o blue",
			"word": "fa-file-word-o blue",
			"excel": "fa-file-excel-o green",
			"powerpoint": "fa-file-powerpoint-o red",
			"text": "fa-file-text-o black",
			"pdf": "fa-file-pdf-o red",
			"zip": "fa-file-zip-o yellow"
	};

	function getBaseType(fileType) {
		var baseType = "";
		angular.forEach(fileTypes, function (value, key) {
			if (value.indexOf(fileType) != -1)
				baseType = key;
		});
		return baseType;
	};
	var getFileIconCss = function (mimeString) {
		if (baseTypes.indexOf(mimeString.split("/")[0]) != -1) {
			return fileIconHash[mimeString.split("/")[0]];
		}
		var iconCss = fileIconHash[getBaseType(mimeString.split("/")[1])];
		return iconCss != undefined ? iconCss : 'fa-file-text-o black';
	};
	return {
		fileIconHash: fileIconHash,
		baseTypes: baseTypes,
		fileTypes: fileTypes,
		isFileAPIAvailable: isFileAPIAvailable,
		initDownload: initDownload,
		base64StringtoBlob: base64StringtoBlob,
		getFileIconCss: getFileIconCss
	}
}]);

module.filter('fileExtFilter', function () {
	return function (input, isExtOnly) {
		if (isExtOnly)
			return input.slice(input.lastIndexOf('.') + 1);
		else
			return input.slice(0, input.lastIndexOf('.'));
	};
});


//get Extension on the Basis of MIME Type
module.filter('fileExtensionFilter', function (altaFileSvcs) {
	var baseTypes = altaFileSvcs.baseTypes;
	var fileTypes = altaFileSvcs.fileTypes;

	function getBaseType(fileType) {
		var baseType = "";
		angular.forEach(fileTypes, function (value, key) {
			if (value.indexOf(fileType) != -1)
				baseType = key;
		});
		return baseType;
	};
	return function (mimeString) {
		mimeString = mimeString.split(':')[1].split(';')[0].toLowerCase();
		if (baseTypes.indexOf(mimeString.split("/")[0]) != -1) {
			return mimeString.split("/")[0];
		} else {
			return getBaseType(mimeString.split("/")[1]);
		}
		return mimeString;

	};
});