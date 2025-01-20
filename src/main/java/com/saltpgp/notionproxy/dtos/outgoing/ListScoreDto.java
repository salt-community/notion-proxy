package com.saltpgp.notionproxy.dtos.outgoing;

import com.saltpgp.notionproxy.assignment.model.Assignment;

import java.util.List;
import java.util.UUID;

public record ListScoreDto(UUID id, List<Assignment> scores) {
}
