package com.management.student_center.service.payroll;

import com.management.student_center.dto.payroll.PayrollDetailResponseDTO;
import com.management.student_center.dto.payroll.PayrollListItemDTO;
import com.management.student_center.dto.payroll.PayrollSessionDetailDTO;
import com.management.student_center.enums.TeacherPaymentStatus;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PayrollExcelExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    // Export danh sách tất cả bảng lương
    public ByteArrayInputStream exportAllPayrollsToExcel(List<PayrollListItemDTO> payrolls) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("Danh sách bảng lương");
            
            // Tạo style
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle moneyStyle = createMoneyCellStyle(workbook);
            CellStyle dateStyle = createDateCellStyle(workbook);
            
            // Tạo header row
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Mã GV", "Tên giáo viên", "Tháng", "Năm", 
                               "Tổng buổi", "Tổng tiền (VNĐ)", "Trạng thái", "Ngày thanh toán"};
            
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 5000);
            }
            
            // Điền dữ liệu
            int rowNum = 1;
            for (PayrollListItemDTO payroll : payrolls) {
                Row row = sheet.createRow(rowNum++);
                
                row.createCell(0).setCellValue(payroll.getPaymentId());
                row.createCell(1).setCellValue(payroll.getTeacherId());
                row.createCell(2).setCellValue(payroll.getTeacherName());
                row.createCell(3).setCellValue(payroll.getMonth());
                row.createCell(4).setCellValue(payroll.getYear());
                row.createCell(5).setCellValue(payroll.getTotalSessions());
                
                Cell amountCell = row.createCell(6);
                amountCell.setCellValue(payroll.getAmount().doubleValue());
                amountCell.setCellStyle(moneyStyle);
                
                row.createCell(7).setCellValue(getStatusText(payroll.getStatus()));
                
                Cell dateCell = row.createCell(8);
                if (payroll.getPaymentDate() != null) {
                    dateCell.setCellValue(payroll.getPaymentDate().format(DATE_FORMATTER));
                    dateCell.setCellStyle(dateStyle);
                } else {
                    dateCell.setCellValue("");
                }
            }
            
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
            
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi xuất file Excel: " + e.getMessage());
        }
    }
    
    // Export chi tiết 1 bảng lương
    public ByteArrayInputStream exportPayrollDetailToExcel(PayrollDetailResponseDTO payroll) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            // Sheet 1: Thông tin tổng hợp
            Sheet summarySheet = workbook.createSheet("Thông tin chung");
            createSummarySheet(summarySheet, payroll);
            
            // Sheet 2: Chi tiết các buổi dạy
            Sheet detailSheet = workbook.createSheet("Chi tiết buổi dạy");
            createDetailSheet(detailSheet, payroll);
            
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
            
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi xuất file Excel: " + e.getMessage());
        }
    }
    
    private void createSummarySheet(Sheet sheet, PayrollDetailResponseDTO payroll) {
        CellStyle headerStyle = createHeaderStyle(sheet.getWorkbook());
        CellStyle labelStyle = createLabelStyle(sheet.getWorkbook());
        CellStyle valueStyle = createValueStyle(sheet.getWorkbook());
        CellStyle moneyStyle = createMoneyCellStyle(sheet.getWorkbook());
        
        sheet.setColumnWidth(0, 5000);
        sheet.setColumnWidth(1, 8000);
        
        int rowNum = 0;
        
        // Title
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("BẢNG LƯƠNG GIÁO VIÊN - THÁNG " + payroll.getMonth() + "/" + payroll.getYear());
        titleCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
        
        rowNum++; // Empty row
        
        // Thông tin giáo viên
        addInfoRow(sheet, rowNum++, "Mã giáo viên:", String.valueOf(payroll.getTeacherId()), labelStyle, valueStyle);
        addInfoRow(sheet, rowNum++, "Tên giáo viên:", payroll.getTeacherName(), labelStyle, valueStyle);
        addInfoRow(sheet, rowNum++, "Tháng/Năm:", payroll.getMonth() + "/" + payroll.getYear(), labelStyle, valueStyle);
        addInfoRow(sheet, rowNum++, "Tổng số buổi:", String.valueOf(payroll.getTotalSessions()), labelStyle, valueStyle);
        
        Row amountRow = sheet.createRow(rowNum++);
        Cell amountLabel = amountRow.createCell(0);
        amountLabel.setCellValue("Tổng thu nhập:");
        amountLabel.setCellStyle(labelStyle);
        Cell amountValue = amountRow.createCell(1);
        amountValue.setCellValue(payroll.getAmount().doubleValue());
        amountValue.setCellStyle(moneyStyle);
        
        addInfoRow(sheet, rowNum++, "Trạng thái:", getStatusText(payroll.getStatus()), labelStyle, valueStyle);
        
        if (payroll.getPaymentDate() != null) {
            addInfoRow(sheet, rowNum++, "Ngày thanh toán:", payroll.getPaymentDate().format(DATE_FORMATTER), labelStyle, valueStyle);
        }
    }
    
    private void createDetailSheet(Sheet sheet, PayrollDetailResponseDTO payroll) {
        CellStyle headerStyle = createHeaderStyle(sheet.getWorkbook());
        CellStyle moneyStyle = createMoneyCellStyle(sheet.getWorkbook());
        CellStyle timeStyle = createTimeCellStyle(sheet.getWorkbook());
        CellStyle dateStyle = createDateCellStyle(sheet.getWorkbook());
        
        // Header row
        Row headerRow = sheet.createRow(0);
        String[] columns = {"STT", "Ngày dạy", "Giờ bắt đầu", "Giờ kết thúc", "Môn học", 
                           "Số giờ", "Loại lương", "Đơn giá (VNĐ)", "Thành tiền (VNĐ)", 
                           "Dạy thay", "Ghi chú"};
        
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, i == 10 ? 6000 : 4000);
        }
        
        // Data rows
        int rowNum = 1;
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (PayrollSessionDetailDTO detail : payroll.getDetails()) {
            Row row = sheet.createRow(rowNum++);
            
            row.createCell(0).setCellValue(rowNum - 1);
            
            // Ngày dạy
            Cell dateCell = row.createCell(1);
            dateCell.setCellValue(detail.getSessionDate().format(DATE_FORMATTER));
            dateCell.setCellStyle(dateStyle);
            
            // Giờ bắt đầu
            if (detail.getStartTime() != null) {
                Cell startTimeCell = row.createCell(2);
                startTimeCell.setCellValue(detail.getStartTime().format(TIME_FORMATTER));
                startTimeCell.setCellStyle(timeStyle);
            } else {
                row.createCell(2).setCellValue("");
            }
            
            // Giờ kết thúc
            if (detail.getEndTime() != null) {
                Cell endTimeCell = row.createCell(3);
                endTimeCell.setCellValue(detail.getEndTime().format(TIME_FORMATTER));
                endTimeCell.setCellStyle(timeStyle);
            } else {
                row.createCell(3).setCellValue("");
            }
            
            row.createCell(4).setCellValue(detail.getSubjectName() != null ? detail.getSubjectName() : "");
            row.createCell(5).setCellValue(detail.getWorkedHours() != null ? detail.getWorkedHours().doubleValue() : 0);
            row.createCell(6).setCellValue(getSalaryTypeText(detail.getSalaryType()));
            
            Cell rateCell = row.createCell(7);
            rateCell.setCellValue(detail.getSalaryRate() != null ? detail.getSalaryRate().doubleValue() : 0);
            rateCell.setCellStyle(moneyStyle);
            
            Cell amountCell = row.createCell(8);
            amountCell.setCellValue(detail.getAmount() != null ? detail.getAmount().doubleValue() : 0);
            amountCell.setCellStyle(moneyStyle);
            
            row.createCell(9).setCellValue(detail.getReplacement() != null && detail.getReplacement() ? "Có" : "Không");
            row.createCell(10).setCellValue(detail.getNote() != null ? detail.getNote() : "");
            
            if (detail.getAmount() != null) {
                totalAmount = totalAmount.add(detail.getAmount());
            }
        }
        
        // Add total row
        Row totalRow = sheet.createRow(rowNum);
        Cell totalLabel = totalRow.createCell(7);
        totalLabel.setCellValue("TỔNG CỘNG:");
        totalLabel.setCellStyle(headerStyle);
        
        Cell totalAmountCell = totalRow.createCell(8);
        totalAmountCell.setCellValue(totalAmount.doubleValue());
        totalAmountCell.setCellStyle(moneyStyle);
    }
    
    private void addInfoRow(Sheet sheet, int rowNum, String label, String value, 
                           CellStyle labelStyle, CellStyle valueStyle) {
        Row row = sheet.createRow(rowNum);
        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(labelStyle);
        
        Cell valueCell = row.createCell(1);
        valueCell.setCellValue(value);
        valueCell.setCellStyle(valueStyle);
    }
    
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
    
    private CellStyle createLabelStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
    
    private CellStyle createValueStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
    
    private CellStyle createMoneyCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0"));
        return style;
    }
    
    private CellStyle createDateCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("dd/MM/yyyy"));
        return style;
    }
    
    private CellStyle createTimeCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("HH:mm"));
        return style;
    }
    
    private String getStatusText(TeacherPaymentStatus status) {
        if (status == null) return "CHƯA XÁC ĐỊNH";
        
        switch (status) {
            case DRAFT: return "NHÁP";
            case WAITING_TEACHER_CONFIRMATION: return "CHỜ XÁC NHẬN";
            case TEACHER_CONFIRMED: return "ĐÃ XÁC NHẬN";
            case FINALIZED: return "ĐÃ CHỐT";
            case PAID: return "ĐÃ THANH TOÁN";
            default: return status.name();
        }
    }
    
    private String getSalaryTypeText(com.management.student_center.enums.SalaryType salaryType) {
        if (salaryType == null) return "CHƯA XÁC ĐỊNH";
        
        switch (salaryType) {
            case PER_HOUR: return "Theo giờ";
            case PER_SESSION: return "Theo buổi";
            default: return salaryType.name();
        }
    }
}