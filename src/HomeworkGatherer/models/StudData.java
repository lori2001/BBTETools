package HomeworkGatherer.models;

public class StudData {
    public int hwNum, group;
    public String name, idStr;

    public StudData() {
        hwNum = 0;
        group = 0;
        name = "";
        idStr = "";
    }
    public StudData(int hwN, String name, int stdGr, String id) {
        hwNum = hwN;
        group = stdGr;
        this.name = name;
        idStr = id;
    }
}
