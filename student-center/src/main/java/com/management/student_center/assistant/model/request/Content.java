package com.management.student_center.assistant.model.request;

import java.util.List;

public class Content {

    private List<Part> parts;

    public Content() {
    }
    
	public List<Part> getParts() {
		return parts;
	}

	public void setParts(List<Part> parts) {
		this.parts = parts;
	}

}