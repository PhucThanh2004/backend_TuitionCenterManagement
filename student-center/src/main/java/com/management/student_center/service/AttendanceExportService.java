package com.management.student_center.service;

import com.management.student_center.dto.AttendanceExportDTO;
import com.management.student_center.dto.AttendanceImportDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AttendanceExportService {

    // Headers cho export attendance theo môn học (HIỂN THỊ TIẾNG VIỆT)
    private static final String[] EXPORT_HEADERS = {
        "STT", "Mã học sinh", "Tên học sinh", "Email", "Lớp", "Trường",
        "Trạng thái", "Ghi chú", "Ngày học",
        "Giờ bắt đầu", "Giờ kết thúc", "Môn học", "Mã môn", "Phòng học"
    };
    
    // Headers cho import template
    private static final String[] IMPORT_TEMPLATE_HEADERS = {
        "Mã học sinh", "Mã môn học", "Ngày học (YYYY-MM-DD)", "Trạng thái", "Ghi chú"
    };
    
    // Map trạng thái tiếng Anh - tiếng Việt
    private static final Map<String, String> STATUS_MAP = new HashMap<>();
    static {
        STATUS_MAP.put("present", "Có mặt");
        STATUS_MAP.put("late", "Đi muộn");
        STATUS_MAP.put("absent", "Vắng mặt");
        STATUS_MAP.put("Có mặt", "present");
        STATUS_MAP.put("Đi muộn", "late");
        STATUS_MAP.put("Vắng mặt", "absent");
    }
    
    // Danh sách trạng thái hiển thị tiếng Việt cho dropdown
    private static final String[] STATUS_DISPLAY = {"Có mặt", "Đi muộn", "Vắng mặt"};
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    // ==================== EXPORT METHODS ====================
    
    public byte[] exportToExcel(List<AttendanceExportDTO> data) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Điểm danh");
            
            int rowNum = 0;
            
            // === TIÊU ĐỀ CHÍNH ===
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("BÁO CÁO ĐIỂM DANH HỌC SINH");
            
            CellStyle titleStyle = createTitleStyle(workbook);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, EXPORT_HEADERS.length - 1));
            
            // === THÔNG TIN BỔ SUNG ===
            Row infoRow = sheet.createRow(rowNum++);
            Cell infoCell = infoRow.createCell(0);
            
            String exportDate = LocalDate.now().format(DATE_FORMATTER);
            String totalStudents = String.valueOf(data.size());
            
            // Thêm thông tin vào các ô riêng biệt
            infoRow.createCell(0).setCellValue("Ngày xuất:");
            infoRow.createCell(1).setCellValue(exportDate);
            
            infoRow.createCell(3).setCellValue("Tổng số học sinh:");
            infoRow.createCell(4).setCellValue(totalStudents);
            
            // Tính thống kê trạng thái
            Map<String, Long> statusStats = data.stream()
                .filter(dto -> dto.getStatus() != null)
                .collect(HashMap::new, 
                    (map, dto) -> map.merge(dto.getStatus(), 1L, Long::sum),
                    HashMap::putAll);
            
            int colIndex = 6;
            for (Map.Entry<String, Long> entry : statusStats.entrySet()) {
                String statusDisplay = STATUS_MAP.getOrDefault(entry.getKey(), entry.getKey());
                infoRow.createCell(colIndex).setCellValue(statusDisplay + ":");
                infoRow.createCell(colIndex + 1).setCellValue(entry.getValue());
                colIndex += 2;
            }
            
            CellStyle infoStyle = createInfoStyle(workbook);
            for (int i = 0; i < infoRow.getLastCellNum(); i++) {
                Cell cell = infoRow.getCell(i);
                if (cell != null) {
                    cell.setCellStyle(infoStyle);
                }
            }
            
            // === DÒNG TRỐNG ===
            rowNum++;
            
            // === HEADER BẢNG ===
            Row headerRow = sheet.createRow(rowNum++);
            CellStyle headerStyle = createHeaderStyle(workbook);
            for (int i = 0; i < EXPORT_HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(EXPORT_HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // === FILL DATA ===
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle lockedStyle = createLockedStyle(workbook);
            
            int stt = 1;
            for (AttendanceExportDTO dto : data) {
                Row row = sheet.createRow(rowNum++);
                
                // STT
                createCellWithStyle(row, 0, stt++, dataStyle);
                
                // Student ID - LOCKED
                createCellWithStyle(row, 1, dto.getStudentId() != null ? dto.getStudentId() : 0, lockedStyle);
                
                // Student Name - LOCKED
                createCellWithStyle(row, 2, dto.getStudentName() != null ? dto.getStudentName() : "", lockedStyle);
                
                // Email - LOCKED
                createCellWithStyle(row, 3, dto.getStudentEmail() != null ? dto.getStudentEmail() : "", lockedStyle);
                
                // Grade - LOCKED
                createCellWithStyle(row, 4, dto.getGrade() != null ? dto.getGrade() : "", lockedStyle);
                
                // School Name - LOCKED
                createCellWithStyle(row, 5, dto.getSchoolName() != null ? dto.getSchoolName() : "", lockedStyle);
                
                // Status - CÓ THỂ CHỈNH SỬA (Hiển thị tiếng Việt)
                String statusDisplay = dto.getStatus() != null ? 
                    STATUS_MAP.getOrDefault(dto.getStatus(), dto.getStatus()) : "";
                createCellWithStyle(row, 6, statusDisplay, dataStyle);
                
                // Note - CÓ THỂ CHỈNH SỬA
                createCellWithStyle(row, 7, dto.getNote() != null ? dto.getNote() : "", dataStyle);
                
                // Session Date - LOCKED
                createCellWithStyle(row, 8, dto.getSessionDate() != null ? dto.getSessionDate().toString() : "", lockedStyle);
                
                // Start Time - LOCKED
                createCellWithStyle(row, 9, dto.getStartTime() != null ? dto.getStartTime().toString() : "", lockedStyle);
                
                // End Time - LOCKED
                createCellWithStyle(row, 10, dto.getEndTime() != null ? dto.getEndTime().toString() : "", lockedStyle);
                
                // Subject Name - LOCKED
                createCellWithStyle(row, 11, dto.getSubjectName() != null ? dto.getSubjectName() : "", lockedStyle);
                
                // Subject ID - LOCKED
                createCellWithStyle(row, 12, dto.getSubjectId() != null ? dto.getSubjectId() : "", lockedStyle);
                
                // Room Name - LOCKED
                createCellWithStyle(row, 13, dto.getRoomName() != null ? dto.getRoomName() : "", lockedStyle);
            }
            
            // === DATA VALIDATION FOR STATUS (Cột 6 - Trạng thái) ===
            DataValidationHelper validationHelper = sheet.getDataValidationHelper();
            
            // Tạo dropdown với giá trị tiếng Việt
            DataValidationConstraint statusConstraint = validationHelper
                .createExplicitListConstraint(STATUS_DISPLAY);
            
            CellRangeAddressList addressList = new CellRangeAddressList(
                rowNum - data.size(), // row bắt đầu
                rowNum - 1,            // row kết thúc
                6, 6                   // cột Trạng thái
            );
            
            DataValidation validation = validationHelper.createValidation(statusConstraint, addressList);
            validation.setShowErrorBox(true);
            validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            validation.createErrorBox("Lỗi nhập liệu", 
                "Vui lòng chọn từ danh sách: Có mặt, Đi muộn, Vắng mặt");
            
            // Cho phép ô trống
            validation.setEmptyCellAllowed(true);
            
            sheet.addValidationData(validation);
            
            // === PROTECT SHEET (Chỉ cho phép chỉnh sửa cột Trạng thái và Ghi chú) ===
            sheet.protectSheet(""); // Password rỗng
            
            // Mở khóa cho các ô được phép chỉnh sửa (cột 6: Trạng thái, cột 7: Ghi chú)
            for (int i = 1; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    // Cột Trạng thái (index 6)
                    Cell statusCell = row.getCell(6);
                    if (statusCell != null) {
                        statusCell.getCellStyle().setLocked(false);
                    }
                    
                    // Cột Ghi chú (index 7)
                    Cell noteCell = row.getCell(7);
                    if (noteCell != null) {
                        noteCell.getCellStyle().setLocked(false);
                    }
                }
            }
            
            // === AUTO-SIZE COLUMNS ===
            for (int i = 0; i < EXPORT_HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
                // Set độ rộng tối thiểu
                if (sheet.getColumnWidth(i) < 3000) {
                    sheet.setColumnWidth(i, 3000);
                }
            }
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return baos.toByteArray();
        }
    }
    
    public String exportToCsv(List<AttendanceExportDTO> data) {
        StringBuilder csv = new StringBuilder();
        
        // Header với tiếng Việt
        csv.append("STT,")
           .append(String.join(",", EXPORT_HEADERS))
           .append("\n");
        
        // Data
        int stt = 1;
        for (AttendanceExportDTO dto : data) {
            csv.append(stt++).append(",")
               .append(dto.getStudentId() != null ? dto.getStudentId() : "").append(",")
               .append(escapeCsv(dto.getStudentName())).append(",")
               .append(escapeCsv(dto.getStudentEmail())).append(",")
               .append(escapeCsv(dto.getGrade())).append(",")
               .append(escapeCsv(dto.getSchoolName())).append(",")
               .append(escapeCsv(STATUS_MAP.getOrDefault(dto.getStatus(), dto.getStatus()))).append(",")
               .append(escapeCsv(dto.getNote())).append(",")
               .append(dto.getSessionDate() != null ? dto.getSessionDate().toString() : "").append(",")
               .append(dto.getStartTime() != null ? dto.getStartTime().toString() : "").append(",")
               .append(dto.getEndTime() != null ? dto.getEndTime().toString() : "").append(",")
               .append(escapeCsv(dto.getSubjectName())).append(",")
               .append(dto.getSubjectId() != null ? dto.getSubjectId() : "").append(",")
               .append(escapeCsv(dto.getRoomName()))
               .append("\n");
        }
        
        return csv.toString();
    }
    
    // ==================== IMPORT METHODS ====================
    
    public List<AttendanceImportDTO> importFromExcel(java.io.InputStream inputStream) throws IOException {
        List<AttendanceImportDTO> list = new ArrayList<>();
        
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            
            // Tìm header row - bỏ qua tiêu đề và thông tin
            Row headerRow = null;
            int headerRowIndex = -1;
            
            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                // Kiểm tra xem row có phải header không (có chứa "Mã học sinh" hoặc "Student ID")
                for (int j = 0; j < row.getLastCellNum(); j++) {
                    Cell cell = row.getCell(j);
                    if (cell != null) {
                        String value = getStringCellValue(cell);
                        if (value != null && (value.contains("Mã học sinh") || 
                            value.contains("Student ID") || 
                            value.contains("student_id"))) {
                            headerRow = row;
                            headerRowIndex = i;
                            break;
                        }
                    }
                }
                if (headerRow != null) break;
            }
            
            if (headerRow == null) {
                throw new IOException("Không tìm thấy header trong file");
            }
            
            // Phát hiện cột
            Map<String, Integer> columnIndexMap = detectColumns(headerRow);
            
            // Kiểm tra cột bắt buộc
            if (!columnIndexMap.containsKey("STUDENT_ID") || 
                !columnIndexMap.containsKey("SESSION_DATE") || 
                !columnIndexMap.containsKey("STATUS")) {
                throw new IOException("File thiếu cột bắt buộc: Mã học sinh, Ngày học, Trạng thái");
            }
            
            // Start from row after header
            for (int i = headerRowIndex + 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                if (isRowEmpty(row)) continue;
                
                // Lấy dữ liệu
                Long studentId = getLongCellValue(row.getCell(columnIndexMap.get("STUDENT_ID")));
                
                Long subjectId = null;
                if (columnIndexMap.containsKey("SUBJECT_ID")) {
                    subjectId = getLongCellValue(row.getCell(columnIndexMap.get("SUBJECT_ID")));
                }
                
                LocalDate date = parseDateCell(row.getCell(columnIndexMap.get("SESSION_DATE")));
                
                // Xử lý trạng thái - hỗ trợ cả tiếng Anh và tiếng Việt
                String statusRaw = getStringCellValue(row.getCell(columnIndexMap.get("STATUS")));
                String status = null;
                if (statusRaw != null) {
                    statusRaw = statusRaw.trim();
                    // Chuyển từ tiếng Việt sang tiếng Anh nếu cần
                    status = STATUS_MAP.getOrDefault(statusRaw, statusRaw.toLowerCase());
                    // Kiểm tra status hợp lệ
                    if (!Arrays.asList("present", "late", "absent").contains(status)) {
                        status = null;
                    }
                }
                
                // Ghi chú
                String note = null;
                if (columnIndexMap.containsKey("NOTE")) {
                    note = getStringCellValue(row.getCell(columnIndexMap.get("NOTE")));
                    if (note != null) note = note.trim();
                }
                
                // Validate
                if (studentId == null || studentId <= 0) continue;
                if (date == null) continue;
                if (status == null) continue;
                
                AttendanceImportDTO dto = new AttendanceImportDTO();
                dto.setStudentId(studentId);
                dto.setSubjectId(subjectId);
                dto.setSessionDate(date);
                dto.setStatus(status);
                dto.setNote(note != null ? note.trim() : null);
                
                list.add(dto);
            }
        }
        
        return list;
    }
    
    private Map<String, Integer> detectColumns(Row headerRow) {
        Map<String, Integer> columnMap = new HashMap<>();
        
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell == null) continue;
            
            String header = getStringCellValue(cell);
            if (header == null) continue;
            
            String normalized = header.trim().toLowerCase();
            
            // Student ID - hỗ trợ cả tiếng Việt và tiếng Anh
            if (normalized.contains("mã học sinh") || 
                normalized.contains("student id") || 
                normalized.contains("student_id") || 
                normalized.contains("id học sinh") ||
                normalized.contains("studentid") ||
                normalized.contains("mã hs")) {
                columnMap.put("STUDENT_ID", i);
            }
            
            // Subject ID
            if (normalized.contains("mã môn học") || 
                normalized.contains("mã môn") ||
                normalized.contains("subject id") || 
                normalized.contains("subject_id") || 
                normalized.contains("subjectid") ||
                normalized.contains("subject code") ||
                normalized.contains("subject_code")) {
                columnMap.put("SUBJECT_ID", i);
            }
            
            // Date
            if (normalized.contains("ngày học") || 
                normalized.contains("ngay hoc") || 
                normalized.contains("session date") ||
                normalized.contains("session_date") ||
                normalized.contains("date")) {
                columnMap.put("SESSION_DATE", i);
            }
            
            // Status
            if (normalized.contains("trạng thái") || 
                normalized.contains("trang thai") || 
                normalized.contains("status") ||
                normalized.contains("state")) {
                columnMap.put("STATUS", i);
            }
            
            // Note
            if (normalized.contains("ghi chú") || 
                normalized.contains("ghi chu") || 
                normalized.contains("note") ||
                normalized.contains("notes")) {
                columnMap.put("NOTE", i);
            }
        }
        
        return columnMap;
    }
    
    public List<AttendanceImportDTO> importFromCsv(java.io.InputStream inputStream) throws IOException {
        List<AttendanceImportDTO> list = new ArrayList<>();
        
        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(inputStream))) {
            String line;
            boolean isHeader = true;
            Map<String, Integer> columnIndexMap = null;
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                if (isHeader) {
                    String[] headers = parseCsvLine(line);
                    columnIndexMap = detectColumnsFromArray(headers);
                    isHeader = false;
                    
                    if (!columnIndexMap.containsKey("STUDENT_ID") || 
                        !columnIndexMap.containsKey("SESSION_DATE") || 
                        !columnIndexMap.containsKey("STATUS")) {
                        throw new IOException("File thiếu cột bắt buộc: Mã học sinh, Ngày học, Trạng thái");
                    }
                    continue;
                }
                
                if (columnIndexMap == null) continue;
                
                String[] fields = parseCsvLine(line);
                if (fields.length <= Math.max(columnIndexMap.values().stream().max(Integer::compareTo).orElse(0), 0)) {
                    continue;
                }
                
                Long studentId = null;
                try {
                    studentId = Long.parseLong(fields[columnIndexMap.get("STUDENT_ID")].trim());
                } catch (NumberFormatException e) {
                    continue;
                }
                if (studentId == null || studentId <= 0) continue;
                
                Long subjectId = null;
                if (columnIndexMap.containsKey("SUBJECT_ID")) {
                    try {
                        subjectId = Long.parseLong(fields[columnIndexMap.get("SUBJECT_ID")].trim());
                    } catch (NumberFormatException e) {
                        // ignore
                    }
                }
                
                LocalDate date = null;
                try {
                    String dateStr = fields[columnIndexMap.get("SESSION_DATE")].trim();
                    date = LocalDate.parse(dateStr, DATE_FORMATTER);
                } catch (Exception e) {
                    continue;
                }
                
                String statusRaw = fields[columnIndexMap.get("STATUS")].trim();
                String status = STATUS_MAP.getOrDefault(statusRaw, statusRaw.toLowerCase());
                if (!Arrays.asList("present", "late", "absent").contains(status)) {
                    continue;
                }
                
                String note = null;
                if (columnIndexMap.containsKey("NOTE") && columnIndexMap.get("NOTE") < fields.length) {
                    note = fields[columnIndexMap.get("NOTE")].trim();
                }
                
                AttendanceImportDTO dto = new AttendanceImportDTO();
                dto.setStudentId(studentId);
                dto.setSubjectId(subjectId);
                dto.setSessionDate(date);
                dto.setStatus(status);
                dto.setNote(note);
                
                list.add(dto);
            }
        }
        
        return list;
    }
    
    private Map<String, Integer> detectColumnsFromArray(String[] headers) {
        Map<String, Integer> columnMap = new HashMap<>();
        
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i].trim().toLowerCase();
            
            if (header.contains("mã học sinh") || 
                header.contains("student id") || 
                header.contains("student_id") || 
                header.contains("id học sinh") ||
                header.contains("studentid") ||
                header.contains("mã hs")) {
                columnMap.put("STUDENT_ID", i);
            }
            
            if (header.contains("mã môn học") || 
                header.contains("mã môn") ||
                header.contains("subject id") || 
                header.contains("subject_id") || 
                header.contains("subjectid") ||
                header.contains("subject code") ||
                header.contains("subject_code")) {
                columnMap.put("SUBJECT_ID", i);
            }
            
            if (header.contains("ngày học") || 
                header.contains("ngay hoc") || 
                header.contains("session date") ||
                header.contains("session_date") ||
                header.contains("date")) {
                columnMap.put("SESSION_DATE", i);
            }
            
            if (header.contains("trạng thái") || 
                header.contains("trang thai") || 
                header.contains("status") ||
                header.contains("state")) {
                columnMap.put("STATUS", i);
            }
            
            if (header.contains("ghi chú") || 
                header.contains("ghi chu") || 
                header.contains("note") ||
                header.contains("notes")) {
                columnMap.put("NOTE", i);
            }
        }
        
        return columnMap;
    }
    
    // ==================== TEMPLATE METHODS ====================
    
    public byte[] generateImportTemplate(String format) throws IOException {
        if ("excel".equalsIgnoreCase(format)) {
            return generateExcelTemplate();
        } else {
            return generateCsvTemplate().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        }
    }
    
    private byte[] generateExcelTemplate() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Import Template");
            
            // Tiêu đề
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("HƯỚNG DẪN IMPORT DỮ LIỆU ĐIỂM DANH");
            CellStyle titleStyle = createTitleStyle(workbook);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, IMPORT_TEMPLATE_HEADERS.length - 1));
            
            // Hướng dẫn
            Row guideRow = sheet.createRow(1);
            Cell guideCell = guideRow.createCell(0);
            guideCell.setCellValue("Trạng thái: present (Có mặt), late (Đi muộn), absent (Vắng mặt)");
            CellStyle guideStyle = createGuideStyle(workbook);
            guideCell.setCellStyle(guideStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, IMPORT_TEMPLATE_HEADERS.length - 1));
            
            // Header
            Row headerRow = sheet.createRow(3);
            CellStyle headerStyle = createHeaderStyle(workbook);
            for (int i = 0; i < IMPORT_TEMPLATE_HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(IMPORT_TEMPLATE_HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Example data
            Row exampleRow = sheet.createRow(4);
            exampleRow.createCell(0).setCellValue(1001);
            exampleRow.createCell(1).setCellValue(5);
            exampleRow.createCell(2).setCellValue("2026-07-02");
            exampleRow.createCell(3).setCellValue("present");
            exampleRow.createCell(4).setCellValue("Đúng giờ");
            
            // Data validation for status column
            DataValidationHelper validationHelper = sheet.getDataValidationHelper();
            DataValidationConstraint statusConstraint = validationHelper
                .createExplicitListConstraint(new String[]{"present", "late", "absent"});
            
            CellRangeAddressList addressList = new CellRangeAddressList(4, 1000, 3, 3);
            DataValidation validation = validationHelper.createValidation(statusConstraint, addressList);
            validation.setShowErrorBox(true);
            validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            validation.createErrorBox("Lỗi nhập liệu", 
                "Chỉ chấp nhận: present (Có mặt), late (Đi muộn), absent (Vắng mặt)");
            sheet.addValidationData(validation);
            
            // Auto-size
            for (int i = 0; i < IMPORT_TEMPLATE_HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return baos.toByteArray();
        }
    }
    
    private String generateCsvTemplate() {
        return "Mã học sinh,Mã môn học,Ngày học (YYYY-MM-DD),Trạng thái,Ghi chú\n" +
               "1001,5,2026-07-02,present,Đúng giờ\n" +
               "1002,5,2026-07-02,absent,Nghỉ học\n" +
               "1003,5,2026-07-02,late,Đi muộn 5 phút\n";
    }
    
    // ==================== STYLE HELPER METHODS ====================
    
    private void createCellWithStyle(Row row, int col, Object value, CellStyle style) {
        Cell cell = row.createCell(col);
        if (value != null) {
            cell.setCellValue(value.toString());
        } else {
            cell.setCellValue("");
        }
        cell.setCellStyle(style);
    }
    
    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
    
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_TEAL.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
    
    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
    
    private CellStyle createLockedStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setLocked(true);
        
        // Màu nền xám nhạt để phân biệt với ô có thể chỉnh sửa
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        return style;
    }
    
    private CellStyle createInfoStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
    
    private CellStyle createGuideStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setColor(IndexedColors.RED.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
    
    // ==================== UTILITY METHODS ====================
    
    private String getStringCellValue(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().format(DATE_FORMATTER);
                }
                return String.valueOf((long) cell.getNumericCellValue());
            default:
                return null;
        }
    }
    
    private Long getLongCellValue(Cell cell) {
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return (long) cell.getNumericCellValue();
            } else if (cell.getCellType() == CellType.STRING) {
                return Long.parseLong(cell.getStringCellValue().trim());
            }
        } catch (Exception e) {
            // Invalid number
        }
        return null;
    }
    
    private LocalDate parseDateCell(Cell cell) {
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.STRING) {
                String dateStr = cell.getStringCellValue().trim();
                // Thử parse với các format khác nhau
                try {
                    return LocalDate.parse(dateStr, DATE_FORMATTER);
                } catch (Exception e) {
                    // Thử với format khác
                    DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    return LocalDate.parse(dateStr, formatter2);
                }
            } else if (cell.getCellType() == CellType.NUMERIC) {
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate();
                }
            }
        } catch (Exception e) {
            // Invalid date format
        }
        return null;
    }
    
    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
    
    private boolean isRowEmpty(Row row) {
        if (row == null) return true;
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = getStringCellValue(cell);
                if (value != null && !value.trim().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        result.add(current.toString());
        return result.toArray(new String[0]);
    }
}