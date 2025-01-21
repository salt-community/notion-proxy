package com.saltpgp.notionproxy.assignment.controller.dtos;

import com.saltpgp.notionproxy.assignment.model.Assignment;

import java.util.List;

public record AssignmentDto(String developerId, List<Assignment> assignments) {
}
