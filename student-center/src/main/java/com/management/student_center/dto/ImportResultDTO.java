package com.management.student_center.dto;

import java.util.List;

public class ImportResultDTO {
    private int successCount;
    private int errorCount;
    private List<ImportErrorDTO> errors;
    private String message;
    private int totalRecords;
    
    public ImportResultDTO() {}
    
    public ImportResultDTO(int successCount, int errorCount, List<ImportErrorDTO> errors, 
                           String message, int totalRecords) {
        this.successCount = successCount;
        this.errorCount = errorCount;
        this.errors = errors;
        this.message = message;
        this.totalRecords = totalRecords;
    }
    
    public int getSuccessCount() { return successCount; }
    public void setSuccessCount(int successCount) { this.successCount = successCount; }
    
    public int getErrorCount() { return errorCount; }
    public void setErrorCount(int errorCount) { this.errorCount = errorCount; }
    
    public List<ImportErrorDTO> getErrors() { return errors; }
    public void setErrors(List<ImportErrorDTO> errors) { this.errors = errors; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public int getTotalRecords() { return totalRecords; }
    public void setTotalRecords(int totalRecords) { this.totalRecords = totalRecords; }
}