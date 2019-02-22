package com.excel.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.excel.service.ExcelService;
import com.excel.util.ResponseUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value="ExcelController",description="Operation related to Excel Utility")
@RestController
public class ExcelController {

	@Autowired
	private ExcelService excelService;

	private static final Logger log = LoggerFactory.getLogger(ExcelController.class);

	@ApiOperation(value = "Activity for number of columns of excel file, pass 0 for file1 and pass the length of previous file column in currentHead")
	@PostMapping(value="file/columns",produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> getColumnsOfFile(@RequestPart(name="file",required=false) MultipartFile file,@RequestParam Integer currentHead){
		log.info("Inside the getColumnsOfFile API");
		log.info("file is : {}",file);
		if(file == null){
			return ResponseUtil.errorResponse("File can not be null", HttpStatus.BAD_REQUEST);
		}

		try {
			return excelService.getColumnsOfFile(file,currentHead);
		} catch (Exception e) {
			log.error("Exception occur while process the request of get column from excel file : {}",e);
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}		

	}

	@PostMapping(value="excel",produces={MediaType.APPLICATION_JSON_UTF8_VALUE},consumes={MediaType.MULTIPART_FORM_DATA_VALUE,MediaType.APPLICATION_JSON_UTF8_VALUE})
	public ResponseEntity<Object> createNewExcelFromGivenExcel(@RequestPart(name="file1",required=false) MultipartFile file1,@RequestPart(name="file2",required=false) MultipartFile file2,@RequestParam List<String> colOrder,@RequestParam String mimeType,@RequestParam String fileType,@RequestParam String delimiter){
		log.info("Inside the createNewExcelFromGivenExcel API");

		log.info("File1 is : {}",file1);
		log.info("File2 is : {}",file2);
		log.info("Colorder order is : {}",colOrder);
		log.info("mimeType is : {}",mimeType);
		log.info("fileType is : {}",fileType);
		log.info("delimiter is : {}",delimiter);

		if(file1 == null){
			return ResponseUtil.errorResponse("File1 can not be null", HttpStatus.BAD_REQUEST);
		}else if(file2 == null){
			return ResponseUtil.errorResponse("File2 can not be null", HttpStatus.BAD_REQUEST);
		}else if(colOrder == null){
			return ResponseUtil.errorResponse("colOrder can not be null", HttpStatus.BAD_REQUEST);
		}

		try {
			return excelService.createExcelByOtherExcel(file1,file2,colOrder,mimeType,fileType,delimiter);
		}catch (Exception e) {
			log.error("Exception occur while process the request of get column from excel file : {}",e);
			log.error("Message is : {}",e.getMessage());
			return ResponseUtil.errorResponse(e.getMessage() == null ? "File not generated" : e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}		

		
	}


}
