package com.management.student_center.assistant.dto;

import java.util.List;

public class PublicKnowledgeDTO {

    private List<String> subjects;

    private List<String> tuitions;

    private String centerInfo;

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    public List<String> getTuitions() {
        return tuitions;
    }

    public void setTuitions(List<String> tuitions) {
        this.tuitions = tuitions;
    }

    public String getCenterInfo() {
        return centerInfo;
    }

    public void setCenterInfo(String centerInfo) {
        this.centerInfo = centerInfo;
    }
}