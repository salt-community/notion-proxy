package com.saltpgp.notionproxy.assignment.controller.dtos;

import com.saltpgp.notionproxy.assignment.model.Assignment;

import java.util.ArrayList;
import java.util.List;

public record AssignmentDto(String id, String name, int score, List<String> categories, String comment) {
    public static AssignmentDto fromModel(Assignment assignment) {
        return new AssignmentDto(
                assignment.getId(),
                assignment.getName(),
                assignment.getScore(),
                assignment.getCategories(),
                assignment.getComment()
        );
    }

    public static List<AssignmentDto> fromModelList(List<Assignment> assignments) {
        List<AssignmentDto> AssignmentDtoList = new ArrayList<>();
        for (Assignment assignment : assignments) {
            AssignmentDtoList.add(fromModel(assignment));
        }
        return AssignmentDtoList;
    }
}
