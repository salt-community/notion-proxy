package com.saltpgp.notionproxy.dtos.outgoing;

import com.saltpgp.notionproxy.models.Score;

import java.util.ArrayList;
import java.util.List;

public record ScoreDto(String name, int score, List<String>categories) {

    public static List<ScoreDto> fromModel(List<Score> scores) {
        List<ScoreDto> scoresDto = new ArrayList<>();
        for (Score score : scores) {
            scoresDto.add(new ScoreDto(score.getName(), score.getScore(), score.getCategories()));
        }
        return scoresDto;
    }
}
