package com.excel.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ExcelUiController {

	
	private static final Logger log = LoggerFactory.getLogger(ExcelUiController.class);


	@RequestMapping("/home")
	 public String home() {
	  return "index";
	 }
	
	@RequestMapping("/")
	 public String defaultHome() {
	  return "index";
	 }
	
}
