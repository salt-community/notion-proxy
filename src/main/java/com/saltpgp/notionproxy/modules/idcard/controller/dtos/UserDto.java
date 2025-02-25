package com.saltpgp.notionproxy.modules.idcard.controller.dtos;

import com.saltpgp.notionproxy.modules.idcard.model.User;

public record UserDto(String uuid, String name, String course, String email, String gitHub) {
    public static UserDto fromModel(User user) {
        return new UserDto(
                user.getUuid(),
                user.getName(),
                user.getCourse(),
                user.getEmail(),
                user.getGitHub()
        );
    }
}
