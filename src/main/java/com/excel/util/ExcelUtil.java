package com.excel.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excel.service.ExcelService;

public class ExcelUtil {

	private static final Logger log = LoggerFactory.getLogger(ExcelService.class);

	public static Double parseIntoDouble(String input){
		try{
			return Double.parseDouble(input);
		}catch (Exception e) {
			log.error("Exception occur while parse the value : {} into double",input);
			return null;
		}
	}

	public static Integer parseIntoInteger(String input){
		try{
			return Integer.parseInt(input);
		}catch (Exception e) {
			log.error("Exception occur while parse the value : {} into Integer",input);
			return null;
		}
	}

	// return zero if not found index
	public static Integer getIntegerValue(String col){

		int i=0;

		for(char ch : col.toCharArray()){

			if((ch+"").matches("[0-9]")){
				return parseIntoInteger(col.substring(i,col.length()));
			}
			i++;
		}
		return 0;
	}

}
