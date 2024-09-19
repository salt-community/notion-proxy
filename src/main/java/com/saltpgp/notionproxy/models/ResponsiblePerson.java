package com.saltpgp.notionproxy.models;

import java.util.UUID;

public record ResponsiblePerson(String name, UUID id, String email) {
}
