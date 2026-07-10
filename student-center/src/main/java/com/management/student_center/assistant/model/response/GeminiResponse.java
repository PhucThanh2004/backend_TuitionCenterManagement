package com.management.student_center.assistant.model.response;

import java.util.List;

public class GeminiResponse {

    private List<Candidate> candidates;

	public List<Candidate> getCandidates() {
		return candidates;
	}

	public void setCandidates(List<Candidate> candidates) {
		this.candidates = candidates;
	}

}