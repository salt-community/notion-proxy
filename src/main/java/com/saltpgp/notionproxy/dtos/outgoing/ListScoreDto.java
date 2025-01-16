package com.saltpgp.notionproxy.dtos.outgoing;

import com.saltpgp.notionproxy.models.Score;

import java.util.List;
import java.util.UUID;

public record ListScoreDto(UUID id, List<Score> scores) {
}
