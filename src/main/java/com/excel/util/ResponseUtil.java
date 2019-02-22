package com.excel.util;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public class ResponseUtil {


	/**
	 * Use This Method When Need to Success Response with Status Code 200
	 * @param  data 
	 * @param  message
	 * @return ResponseEntity
	 */

	final static public ResponseEntity<Object> response(Object data,String message){
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("isSuccess",true);
		map.put("data",data);
		map.put("status",HttpStatus.OK);
		map.put("message", message);
		map.put("timestamp",new Date());
		return new ResponseEntity<Object>(map,HttpStatus.OK);
	}

	final static public ResponseEntity<Object> fieldErrorResponse(String message,Object fieldError){
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("data",null);
		map.put("status",HttpStatus.BAD_REQUEST);
		map.put("message", message);
		map.put("timestamp",new Date());
		return new ResponseEntity<Object>(map,HttpStatus.BAD_REQUEST);
	} 

	/**
	 * Utility For getting Map of All Validation Related Error
     Occur in Spring's Form Validation procedure
	 * @param result
	 * @return filederror 
	 */
	public static Map<String,Object> getFieldErrorResponse(BindingResult result){	
		Map<String,Object> fielderror=new HashMap<String,Object>();	
		List<FieldError> errors =result.getFieldErrors();		
		for(FieldError error : errors){		 
			fielderror.put(error.getField(), error.getDefaultMessage());		
		}		return fielderror;
	}


	/**
	 * Use This Method when need to create error Response 
	 * @param message
	 * @param httpStatus
	 * @return ResponseEntity
	 */
	final static public ResponseEntity<Object> errorResponse(String message,HttpStatus httpStatus){
		Map<String,Object> map=new HashMap<String,Object>(6);
		map.put("data",null);
		map.put("status",httpStatus);
		map.put("message", message);
		map.put("timestamp",new Date());
		return new ResponseEntity<Object>(map,httpStatus);
	} 
	
}
