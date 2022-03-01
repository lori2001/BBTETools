package ScheduleGenerator.models;

public record Major(String code, String name, String lang) {

    public String getCode() {
        return code;
    }

    public String getLang() {
        return lang;
    }

    public String getName() {
        return name;
    }
}
