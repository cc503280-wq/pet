package com.pet.service.product; // 請確認 package

import com.pet.model.product.Product;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelExportService {

    public ByteArrayInputStream productsToExcel(List<Product> products) {
        // 1. 建立工作簿與工作表
        try (Workbook workbook = new XSSFWorkbook(); 
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("商品列表");

            // 2. 設定標題列 (Header)
            String[] headers = {"ID", "商品名稱", "價格", "庫存", "分類", "狀態", "建立時間"};
            Row headerRow = sheet.createRow(0);

            // 設定標題樣式 (加粗、背景色)
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 3. 填入資料列 (Data)
            int rowIdx = 1;
            for (Product p : products) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(p.getProductId());
                row.createCell(1).setCellValue(p.getProductName());
                row.createCell(2).setCellValue(p.getPrice());
                row.createCell(3).setCellValue(p.getStock());
                
                // 處理可能為 null 的關聯物件
                String catName = (p.getCategory() != null) ? p.getCategory().getCategoryName() : "未分類";
                row.createCell(4).setCellValue(catName);
                
                String status = p.getIsActive() ? "上架中" : "未上架";
                row.createCell(5).setCellValue(status);
                
                // 處理日期 (轉字串)
                String dateStr = (p.getCreatedAt() != null) ? p.getCreatedAt().toString() : "";
                row.createCell(6).setCellValue(dateStr);
            }

            // 4. 自動調整欄寬
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 5. 寫入串流
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Excel 匯出失敗: " + e.getMessage());
        }
    }
}