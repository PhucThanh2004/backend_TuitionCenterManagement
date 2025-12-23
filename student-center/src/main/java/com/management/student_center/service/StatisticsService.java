package com.management.student_center.service;

import com.management.student_center.dto.statistics.RevenueStatisticDTO;
import com.management.student_center.dto.statistics.SubjectRevenueDTO;
import com.management.student_center.repository.StudentTuitionRepository;
import com.management.student_center.repository.TeacherPaymentRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class StatisticsService {

	private final StudentTuitionRepository tuitionRepo;
	private final TeacherPaymentRepository paymentRepo;

	public StatisticsService(StudentTuitionRepository tuitionRepo, TeacherPaymentRepository paymentRepo) {
		this.tuitionRepo = tuitionRepo;
		this.paymentRepo = paymentRepo;
	}

	public List<RevenueStatisticDTO> getYearlyStatistics(int year) {
		// 1. Lấy dữ liệu thô
		List<Object[]> rawRevenue = tuitionRepo.sumTotalRevenueByYear(year);
		List<Object[]> rawExpense = paymentRepo.sumTotalExpenseByYear(year);

		// 2. Chuyển List<Object[]> thành Map để dễ truy xuất theo tháng
		Map<Integer, BigDecimal> revenueMap = new HashMap<>();
		for (Object[] row : rawRevenue) {
			revenueMap.put((Integer) row[0], (BigDecimal) row[1]);
		}

		Map<Integer, BigDecimal> expenseMap = new HashMap<>();
		for (Object[] row : rawExpense) {
			expenseMap.put((Integer) row[0], (BigDecimal) row[1]);
		}

		// 3. Tạo danh sách đủ 12 tháng (Tháng nào thiếu thì set = 0)
		List<RevenueStatisticDTO> result = new ArrayList<>();
		for (int m = 1; m <= 12; m++) {
			BigDecimal rev = revenueMap.getOrDefault(m, BigDecimal.ZERO);
			BigDecimal exp = expenseMap.getOrDefault(m, BigDecimal.ZERO);
			result.add(new RevenueStatisticDTO(m, rev, exp));
		}

		return result;
	}
	public byte[] exportRevenueToExcel(int year) throws IOException {
        List<RevenueStatisticDTO> statistics = getYearlyStatistics(year);
        
        // Các cột trong file Excel
        String[] columns = {"Tháng", "Doanh Thu (VND)", "Chi Phí (VND)", "Lợi Nhuận (VND)"};

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Báo cáo " + year);

            // 1. Tạo Font cho Header (In đậm, màu xanh)
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.BLUE.getIndex());

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);

            // 2. Tạo dòng Header
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < columns.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(columns[col]);
                cell.setCellStyle(headerCellStyle);
            }

            // 3. Định dạng số tiền (Currency format)
            CellStyle currencyStyle = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            currencyStyle.setDataFormat(format.getFormat("#,##0")); // Định dạng 1,000,000

            // 4. Đổ dữ liệu vào các dòng
            int rowIdx = 1;
            BigDecimal totalRev = BigDecimal.ZERO;
            BigDecimal totalExp = BigDecimal.ZERO;
            BigDecimal totalProfit = BigDecimal.ZERO;

            for (RevenueStatisticDTO dto : statistics) {
                Row row = sheet.createRow(rowIdx++);

                // Cột Tháng
                row.createCell(0).setCellValue("Tháng " + dto.getMonth());

                // Cột Thu
                Cell cellRev = row.createCell(1);
                cellRev.setCellValue(dto.getTotalRevenue().doubleValue());
                cellRev.setCellStyle(currencyStyle);

                // Cột Chi
                Cell cellExp = row.createCell(2);
                cellExp.setCellValue(dto.getTotalExpense().doubleValue());
                cellExp.setCellStyle(currencyStyle);

                // Cột Lợi nhuận
                BigDecimal profit = dto.getTotalRevenue().subtract(dto.getTotalExpense());
                Cell cellProfit = row.createCell(3);
                cellProfit.setCellValue(profit.doubleValue());
                cellProfit.setCellStyle(currencyStyle);

                // Cộng tổng
                totalRev = totalRev.add(dto.getTotalRevenue());
                totalExp = totalExp.add(dto.getTotalExpense());
                totalProfit = totalProfit.add(profit);
            }

            // 5. Thêm dòng TỔNG CỘNG cuối cùng
            Row totalRow = sheet.createRow(rowIdx);
            Font totalFont = workbook.createFont();
            totalFont.setBold(true);
            totalFont.setColor(IndexedColors.RED.getIndex());
            CellStyle totalStyle = workbook.createCellStyle();
            totalStyle.setFont(totalFont);
            totalStyle.setDataFormat(format.getFormat("#,##0"));

            Cell cellTotalLabel = totalRow.createCell(0);
            cellTotalLabel.setCellValue("TỔNG CỘNG");
            cellTotalLabel.setCellStyle(totalStyle);

            Cell cellTotalRev = totalRow.createCell(1);
            cellTotalRev.setCellValue(totalRev.doubleValue());
            cellTotalRev.setCellStyle(totalStyle);

            Cell cellTotalExp = totalRow.createCell(2);
            cellTotalExp.setCellValue(totalExp.doubleValue());
            cellTotalExp.setCellStyle(totalStyle);

            Cell cellTotalProfit = totalRow.createCell(3);
            cellTotalProfit.setCellValue(totalProfit.doubleValue());
            cellTotalProfit.setCellStyle(totalStyle);

            // 6. Tự động giãn cột cho đẹp
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
	
	
	public List<SubjectRevenueDTO> getSubjectStatistics(int year) {
        // 1. Lấy dữ liệu từ DB (Object[0] = Tên môn, Object[1] = Tiền)
        List<Object[]> rawRevenue = tuitionRepo.sumRevenueBySubject(year);
        List<Object[]> rawExpense = paymentRepo.sumExpenseBySubject(year);

        // 2. Map doanh thu: Key = Tên môn, Value = Tiền
        Map<String, BigDecimal> revenueMap = new HashMap<>();
        for (Object[] row : rawRevenue) {
            String subjectName = (String) row[0];
            BigDecimal amount = (BigDecimal) row[1]; // DB trả về Decimal
            revenueMap.put(subjectName, amount);
        }

        // 3. Map chi phí: Key = Tên môn, Value = Tiền
        Map<String, BigDecimal> expenseMap = new HashMap<>();
        for (Object[] row : rawExpense) {
            String subjectName = (String) row[0];
            // Lưu ý: Bảng lương bạn khai báo totalMoney là float, cần ép kiểu cẩn thận
            BigDecimal amount;
            if (row[1] instanceof Double) {
                amount = BigDecimal.valueOf((Double) row[1]);
            } else if (row[1] instanceof Float) {
                amount = BigDecimal.valueOf((Float) row[1]);
            } else {
                amount = (BigDecimal) row[1];
            }
            expenseMap.put(subjectName, amount);
        }

        // 4. Gộp danh sách tất cả các môn có phát sinh thu hoặc chi
        Set<String> allSubjects = new HashSet<>();
        allSubjects.addAll(revenueMap.keySet());
        allSubjects.addAll(expenseMap.keySet());

        List<SubjectRevenueDTO> result = new ArrayList<>();
        for (String subject : allSubjects) {
            BigDecimal rev = revenueMap.getOrDefault(subject, BigDecimal.ZERO);
            BigDecimal exp = expenseMap.getOrDefault(subject, BigDecimal.ZERO);
            result.add(new SubjectRevenueDTO(subject, rev, exp));
        }

        // Sắp xếp theo tên môn cho đẹp
        result.sort(Comparator.comparing(SubjectRevenueDTO::getSubjectName));
        return result;
    }

    public byte[] exportSubjectRevenueToExcel(int year) throws IOException {
        List<SubjectRevenueDTO> statistics = getSubjectStatistics(year);

        String[] columns = {"Tên Môn Học", "Tổng Doanh Thu", "Tổng Chi Phí (Lương)", "Lợi Nhuận"};

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Doanh thu môn học " + year);

            // --- STYLING (Giống hàm cũ) ---
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            
            // Format tiền tệ
            CellStyle currencyStyle = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            currencyStyle.setDataFormat(format.getFormat("#,##0"));

            // --- HEADER ---
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < columns.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(columns[col]);
                cell.setCellStyle(headerStyle);
            }

            // --- DATA ROWS ---
            int rowIdx = 1;
            BigDecimal totalRev = BigDecimal.ZERO;
            BigDecimal totalExp = BigDecimal.ZERO;
            BigDecimal totalProfit = BigDecimal.ZERO;

            for (SubjectRevenueDTO dto : statistics) {
                Row row = sheet.createRow(rowIdx++);

                // Tên môn
                row.createCell(0).setCellValue(dto.getSubjectName());

                // Thu
                Cell cellRev = row.createCell(1);
                cellRev.setCellValue(dto.getTotalRevenue().doubleValue());
                cellRev.setCellStyle(currencyStyle);

                // Chi
                Cell cellExp = row.createCell(2);
                cellExp.setCellValue(dto.getTotalExpense().doubleValue());
                cellExp.setCellStyle(currencyStyle);

                // Lợi nhuận
                Cell cellProfit = row.createCell(3);
                cellProfit.setCellValue(dto.getProfit().doubleValue());
                cellProfit.setCellStyle(currencyStyle);

                // Tính tổng
                totalRev = totalRev.add(dto.getTotalRevenue());
                totalExp = totalExp.add(dto.getTotalExpense());
                totalProfit = totalProfit.add(dto.getProfit());
            }

            // --- TOTAL ROW ---
            Row totalRow = sheet.createRow(rowIdx);
            CellStyle totalStyle = workbook.createCellStyle();
            Font totalFont = workbook.createFont();
            totalFont.setBold(true);
            totalStyle.setFont(totalFont);
            totalStyle.setDataFormat(format.getFormat("#,##0"));
            totalStyle.setBorderTop(BorderStyle.THIN);

            Cell labelCell = totalRow.createCell(0);
            labelCell.setCellValue("TỔNG CỘNG");
            labelCell.setCellStyle(totalStyle);

            Cell sumRev = totalRow.createCell(1);
            sumRev.setCellValue(totalRev.doubleValue());
            sumRev.setCellStyle(totalStyle);

            Cell sumExp = totalRow.createCell(2);
            sumExp.setCellValue(totalExp.doubleValue());
            sumExp.setCellStyle(totalStyle);

            Cell sumProfit = totalRow.createCell(3);
            sumProfit.setCellValue(totalProfit.doubleValue());
            sumProfit.setCellStyle(totalStyle);

            // Auto size
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
}