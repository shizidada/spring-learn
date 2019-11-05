package org.excel.operator.poi;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.excel.operator.entity.ImportExcelDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Description
 * </p>
 *
 * @author taohua
 * @version v1.0.0
 * @date 2019 2019/10/27 12:33
 * @see org.excel.operator.poi
 */
public class XSSFOperator {

  private static final Logger logger = LoggerFactory.getLogger(XSSFOperator.class);

  private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  public XSSFOperator() {
  }

  /**
   * 读取 excel 数据
   *
   * @param inputStream
   * @return
   */
  public List<ImportExcelDO> importExcelFile(InputStream inputStream) {
    try {
      // 创建 XSSFWorkbook 操作 xlsx xls ==> HSSFWorkbook
      XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

      // 获取第 0 个 Sheet
      XSSFSheet sheet = workbook.getSheetAt(0);

      List<ImportExcelDO> importExcelInfoList = new ArrayList<>();

      for (Row row : sheet) {
        // header
        if (row.getRowNum() == 0) {
          continue;
        }

        ImportExcelDO importExcelDO = new ImportExcelDO();

        Cell iccIdCell = row.getCell(0);
        Cell operatorsCell = row.getCell(1);
        Cell receiverCell = row.getCell(2);
        Cell phoneCell = row.getCell(3);
        Cell addressCell = row.getCell(4);

        // read excel cell will be null
        if (iccIdCell == null
            && operatorsCell == null
            && receiverCell == null
            && phoneCell == null
            && addressCell == null) {
          logger.info("read excel cell null row num {} ", row.getRowNum());
          continue;
        }

        if (iccIdCell != null) {
          // SIM卡卡号
          String iccId = iccIdCell.getStringCellValue();
          importExcelDO.setIccid(iccId);
        }
        if (operatorsCell != null) {
          // 运营商
          String operators = operatorsCell.getStringCellValue();
          importExcelDO.setOperators(operators);
        }

        if (receiverCell != null) {
          // 收货人
          String receiver = receiverCell.getStringCellValue();
          importExcelDO.setReceiver(receiver);
        }

        if (phoneCell != null) {
          // 收货手机号
          String phone = phoneCell.getStringCellValue();
          importExcelDO.setPhone(phone);
        }
        if (addressCell != null) {
          // 收货地址
          String address = addressCell.getStringCellValue();
          importExcelDO.setAddress(address);
        }
        //importExcelDO.setCreateTime(dateFormat.format(new Date()));
        importExcelDO.setCreateTime(new Date());

        //importExcelDO.setUpdateTime(dateFormat.format(new Date()));
        importExcelDO.setUpdateTime(new Date());

        // add to list, need to optimize list add any more data
        importExcelInfoList.add(importExcelDO);
      }
      return importExcelInfoList;
    } catch (Exception e) {
      e.printStackTrace();
      logger.error(e.getMessage());
    }
    return null;
  }

  /**
   * 写出 excel
   *
   * @param exportDiffList
   * @param outputStream
   */
  public void exportExcelFile(List<ImportExcelDO> exportDiffList, OutputStream outputStream) {
    XSSFWorkbook workbook = new XSSFWorkbook();

    // 设置 XSSFCellStyle 样式
    //XSSFCellStyle cellStyle = workbook.createCellStyle();
    //XSSFColor color = new XSSFColor(new java.awt.Color(255, 0, 0));
    //cellStyle.setFillForegroundColor(color);
    //cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

    // 创建表头
    XSSFSheet sheet = workbook.createSheet();
    XSSFRow titleRow = sheet.createRow(0);
    titleRow.createCell(0).setCellValue("iccid");
    titleRow.createCell(1).setCellValue("运营商");
    titleRow.createCell(2).setCellValue("收货人");
    titleRow.createCell(3).setCellValue("收货手机号");
    titleRow.createCell(4).setCellValue("收货地址");

    for (ImportExcelDO excelInfo : exportDiffList) {
      // 填充内容
      int lastRowNum = sheet.getLastRowNum();
      XSSFRow dataRow = sheet.createRow(lastRowNum + 1);
      dataRow.createCell(0).setCellValue(excelInfo.getIccid());
      dataRow.createCell(1).setCellValue(excelInfo.getOperators());
      dataRow.createCell(2).setCellValue(excelInfo.getReceiver());
      dataRow.createCell(3).setCellValue(excelInfo.getPhone());
      dataRow.createCell(4).setCellValue(excelInfo.getAddress());
    }
    try {
      workbook.write(outputStream);
    } catch (IOException e) {
      e.printStackTrace();
      logger.error("workbook 写出失败。");
    } finally {
      if (workbook != null) {
        try {
          workbook.close();
        } catch (IOException e) {
          e.printStackTrace();
          logger.error("workbook 写出失败。");
        }
      }
    }
  }
}
