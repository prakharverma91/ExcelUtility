package com.excel.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.excel.constant.ExcelConstant;
import com.excel.util.ExcelUtil;
import com.excel.util.ResponseUtil;
import com.opencsv.CSVWriter;


@Service
public class ExcelService {

	private static final Logger log = LoggerFactory.getLogger(ExcelService.class);

	public static String getFileNameExtension(String fileName){

		if(fileName == null){
			return null;
		}
		int lastindex=fileName.lastIndexOf(".");

		if(lastindex < 0)
			return null;

		return fileName.substring(lastindex+1,fileName.length());
	}

	public ResponseEntity<Object> getColumnsOfFile(MultipartFile file,Integer currentHead) throws Exception{

		String fileExtension = getFileNameExtension(file.getOriginalFilename());
		if(fileExtension == null){
			return ResponseUtil.errorResponse("Invalid file extension", HttpStatus.BAD_REQUEST);
		}else if(!fileExtension.matches("(xlsx|xlsm|xltx|xltm|xls|xlt|xlm)")){
			return ResponseUtil.errorResponse("Valid file format is xlsx|xlsm|xltx|xltm|xls|xlt|xlm", HttpStatus.NOT_ACCEPTABLE);
		}

		int startIndex = currentHead+1;
		int numberOfCols = readAndGetColumnFromExcelFile(file.getInputStream(),currentHead); 
		return ResponseUtil.response(getColStringListFromColumns(startIndex,numberOfCols), "Columns fetch successfully");

	}

	Integer readAndGetColumnFromExcelFile(InputStream file,Integer currentHead) throws Exception, IOException{

		Workbook workbook = new XSSFWorkbook( file);
		if(workbook.getNumberOfSheets()<1){
			log.error("Sheet not exist in the file : {}",workbook.getNumberOfSheets());
			throw new Exception("Non of the sheet exist in the Excel file");
		}

		DataFormatter dataFormatter = new DataFormatter();
		List<Integer> cols = new ArrayList<Integer>();

		log.debug("Num sheet s type : {}",workbook.getNumCellStyles());
		log.debug("\n\nIterating over Rows and Columns using for-each loop\n");
		//	int counter=0;
		for(Row row: workbook.getSheetAt(0)) {

			for(Cell cell: row) {
				log.debug("Value of row is => {}",dataFormatter.formatCellValue(cell)+"\t");
				currentHead++;
				cols.add(currentHead);
			}
			break;
		}

		return currentHead;
	}


	public ResponseEntity<Object> createExcelByOtherExcel(MultipartFile file1,MultipartFile file2,List<String> colOrder,String mimeType,String fileType,String delimiter) throws Exception{

		String file1Extension = getFileNameExtension(file1.getOriginalFilename());
		String file2Extension = getFileNameExtension(file2.getOriginalFilename());

		if(file1Extension == null || file2Extension== null){
			return ResponseUtil.errorResponse("Invalid file extension", HttpStatus.BAD_REQUEST);
		}else if(!file1Extension.matches("(xlsx|xlsm|xltx|xltm|xls|xlt|xlm)") || !file2Extension.matches("(xlsx|xlsm|xltx|xltm|xls|xlt|xlm)")){
			return ResponseUtil.errorResponse("Valid file format is xlsx|xlsm|xltx|xltm|xls|xlt|xlm", HttpStatus.NOT_ACCEPTABLE);
		}

		Map<String,Integer> colIndexMap = getColIndexMap(colOrder);

		log.debug("Col map is : {}",colIndexMap);

		Workbook workbook1 = new XSSFWorkbook(file1.getInputStream());
		if(workbook1.getNumberOfSheets()<1){
			log.error("Sheet not exist in the file : {}",workbook1.getNumberOfSheets());
			throw new Exception("Non of the sheet exist in the Excel file");
		}

		Workbook workbook2 = new XSSFWorkbook(file2.getInputStream());
		if(workbook2.getNumberOfSheets()<1){
			log.error("Sheet not exist in the file : {}",workbook2.getNumberOfSheets());
			throw new Exception("Non of the sheet exist in the Excel file");
		}

		Workbook outputWorkBook = new XSSFWorkbook();

		Sheet outputSheet = outputWorkBook.createSheet();

		DataFormatter dataFormatter = new DataFormatter();

		int rowCount=0;

		int numerOfCell = 0;

		numerOfCell = workbook1.getSheetAt(0).getRow(0).getLastCellNum()+1;

		for(Row row: workbook1.getSheetAt(0)) {

			Row newRow = outputSheet.createRow(rowCount);

			int cellIndex = 1;

			for(Cell cell: row) {

				log.debug("Value of row is => {}",dataFormatter.formatCellValue(cell)+"\t");

				Integer index = colIndexMap.get( ExcelConstant.COL+cellIndex);
				if(index == null){
					cellIndex++;
					continue;
				}
				Cell newCell = newRow.createCell(index);				

				CellType currentCellType = cell.getCellType();
				if(currentCellType.equals(CellType.NUMERIC )){
					newCell.setCellValue(cell.getNumericCellValue());
				}else if(currentCellType.equals(CellType.STRING )){
					newCell.setCellValue( cell.getStringCellValue());
				}else if(currentCellType.equals(CellType.BOOLEAN)){
					newCell.setCellValue( cell.getBooleanCellValue());
				}

				cellIndex++;
			}
			rowCount++;
		}


		rowCount=0;

		log.debug("Total number of cell in the file1 is : {}",numerOfCell);
		for(Row row: workbook2.getSheetAt(0)) {

			Row existingRow= outputSheet.getRow(rowCount);
			if(existingRow == null){
				existingRow = outputSheet.createRow(rowCount);
			}

			int cellIndex = numerOfCell;
			for(Cell cell: row) {

				log.debug("Value of row is => {}",dataFormatter.formatCellValue(cell)+"\t");
				log.debug("try to get value from map with key : {}  , value is : {}",ExcelConstant.COL+cellIndex,colIndexMap.get( ExcelConstant.COL+cellIndex));
				Integer index = colIndexMap.get( ExcelConstant.COL+cellIndex);
				if(index == null){
					log.debug("this cell not added in output file");
					cellIndex++;
					continue;
				}
				Cell newCell = existingRow.createCell(index);				

				CellType currentCellType = cell.getCellType();
				if(currentCellType.equals(CellType.NUMERIC )){
					newCell.setCellValue(cell.getNumericCellValue());
				}else if(currentCellType.equals(CellType.STRING )){
					newCell.setCellValue( cell.getStringCellValue());
				}else if(currentCellType.equals(CellType.BOOLEAN)){
					newCell.setCellValue( cell.getBooleanCellValue());
				}

				cellIndex++;
			}
			rowCount++;
		}

		Map<String,Object> response = new HashMap<>();
		response.put("file",writeValueIntoFile(outputWorkBook,fileType,delimiter));
		response.put("fileName", "output."+fileType);	
		response.put("mimeType",mimeType);

		return ResponseUtil.response(response,"new file generated successfully");
	}



	public String writeValueIntoFile(Workbook workbook,String fileType,String delimiter) throws IOException {

		try {

			File file= new File("output."+fileType);
			log.info("File name is : {}",file);

			if(fileType.equals("csv")){

				CSVWriter csvWriter = new CSVWriter(new FileWriter(file));
				
				for(Row row : workbook.getSheetAt(0)){

					String[] data	= new String[row.getLastCellNum()];

					for(int index = 0 ;index < row.getLastCellNum();index++){
						data[index] = row.getCell(index)+"";
					}	
					csvWriter.writeNext(data);
				}
				csvWriter.close();
			}else if(fileType.equals("xlsx")){
				FileOutputStream fileOut = new FileOutputStream(file);
				workbook.write(fileOut);
				fileOut.close();
			}else if(fileType.equals("txt")){
			    StringBuffer sb = new StringBuffer();
				FileWriter fw = new FileWriter(file);
			    for(Row row : workbook.getSheetAt(0)){
					String[] data	= new String[row.getLastCellNum() < 0 ? 0 : row.getLastCellNum()];

					for(int index = 0 ;index < row.getLastCellNum();index++){
						sb.append(row.getCell(index)+delimiter);
					}
					sb.deleteCharAt(sb.length()-1);
					sb.append("\n");
				}
			    fw.write(sb.toString());
			    fw.close();
			}
			workbook.close();
			InputStream in = new FileInputStream(file);
			return Base64.encodeBase64String(IOUtils.toByteArray(in));

		} catch (IOException ex) {
			throw new IOException("Error writing to output file", ex);
		}
	}


	List<String> getColStringListFromColumns(int startPoint,int numberOfCols){

		List<String> cols= new ArrayList<>();

		for(int index=startPoint;index<=numberOfCols;index++){
			cols.add(ExcelConstant.COL+index);
		}

		return cols;

	}

	/*Set<String> getColStringListFromColumns(int numberOfCols){

		List<String> cols= new ArrayList<>();

		for(int index=1;index<=numberOfCols;index++){
			cols.add(ExcelConstant.COL+index);
		}

		return cols;

	}
	 */


	Map<String,Integer> getColIndexMap(List<String> colOrder){

		Map<String,Integer> colIndexMap = new HashMap<String,Integer>();

		int index = 0;
		for(String colName : colOrder){
			colIndexMap.put(colName.trim(),index );
			index++;
		}

		return colIndexMap;
	}

	private void createHeaderRow(Sheet s,List<String> colNames) {

		Row r = s.createRow(0);

		int index = 0;
		for(String colName :colNames){

			Cell c = r.createCell(index);
			c.setCellValue(colName);

			index++;
		}

	}

}
