package Common.models;

public class StudData {
    public String hwNum, group, name, idStr;

    public StudData(String hwN, String name, String stdGr, String id) {
        this.hwNum = hwN;
        this.group = stdGr;
        this.name = name;
        this.idStr = id;
    }
}
