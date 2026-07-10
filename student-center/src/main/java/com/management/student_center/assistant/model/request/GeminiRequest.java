package com.management.student_center.assistant.model.request;

import java.util.List;

public class GeminiRequest {

    private List<Content> contents;

    public GeminiRequest() {
    }
    
	public List<Content> getContents() {
		return contents;
	}

	public void setContents(List<Content> contents) {
		this.contents = contents;
	}

}