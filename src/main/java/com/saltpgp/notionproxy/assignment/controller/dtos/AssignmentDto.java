package com.saltpgp.notionproxy.assignment.controller.dtos;

import java.util.List;

public record AssignmentDto(String name, int score, List<String>categories, String comment) {
}
