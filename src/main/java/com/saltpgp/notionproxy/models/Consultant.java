package com.saltpgp.notionproxy.models;


import java.util.List;
import java.util.UUID;


public record Consultant(String name, UUID uuid, List<ResponsiblePerson> responsiblePersonList) {
}
