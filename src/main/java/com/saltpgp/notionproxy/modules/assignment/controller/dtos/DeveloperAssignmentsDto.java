package com.saltpgp.notionproxy.modules.assignment.controller.dtos;

import java.util.List;

public record DeveloperAssignmentsDto(String developerId, List<AssignmentDto> assignments){

}
