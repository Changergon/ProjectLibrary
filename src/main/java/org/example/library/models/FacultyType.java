package org.example.library.models;

import lombok.Getter;

@Getter
public enum FacultyType {
    COMMON("Общедоступный"),
    ARTS("Искусство"),
    ENGINEERING("Инженерия"),
    BUSINESS("Бизнес"),
    SCIENCE("Наука"); // Общедоступный доступ

    private final String displayName;

    FacultyType(String displayName) {
        this.displayName = displayName;
    }

}
