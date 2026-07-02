package com.management.student_center.dto;

public class ImportErrorDTO {
    private int rowNumber;
    private AttendanceImportDTO data;
    private String errorMessage;
    
    public ImportErrorDTO() {}
    
    public ImportErrorDTO(int rowNumber, AttendanceImportDTO data, String errorMessage) {
        this.rowNumber = rowNumber;
        this.data = data;
        this.errorMessage = errorMessage;
    }
    
    public int getRowNumber() { return rowNumber; }
    public void setRowNumber(int rowNumber) { this.rowNumber = rowNumber; }
    
    public AttendanceImportDTO getData() { return data; }
    public void setData(AttendanceImportDTO data) { this.data = data; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}