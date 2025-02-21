package com.saltpgp.notionproxy.modules.idcard.controller.dtos;

import com.saltpgp.notionproxy.modules.idcard.model.User;

public record UserDto(String name, String course, String endDate, String status, String gitHub) {
    public static UserDto fromModel(User user) {
        return new UserDto(
                user.getName(),
                user.getCourse(),
                user.getEndDate(),
                user.getStatus(),
                user.getGitHub()
        );
    }
}
