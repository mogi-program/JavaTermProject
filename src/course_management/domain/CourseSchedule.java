package course_management.domain;

public class CourseSchedule {
    private int course_id;
    private String course_name;
    private int schedule_id;
    private int day_of_week; // 1:월 ~ 7:일
    private String start_time; // HH:mm
    private String end_time;   // HH:mm

    public int getCourse_id() { return course_id; }
    public void setCourse_id(int course_id) { this.course_id = course_id; }

    public String getCourse_name() { return course_name; }
    public void setCourse_name(String course_name) { this.course_name = course_name; }

    public int getSchedule_id() { return schedule_id; }
    public void setSchedule_id(int schedule_id) { this.schedule_id = schedule_id; }

    public int getDay_of_week() { return day_of_week; }
    public void setDay_of_week(int day_of_week) { this.day_of_week = day_of_week; }

    public String getStart_time() { return start_time; }
    public void setStart_time(String start_time) { this.start_time = start_time; }

    public String getEnd_time() { return end_time; }
    public void setEnd_time(String end_time) { this.end_time = end_time; }

    @Override
    public String toString() {
        return course_name + " (ID:" + course_id + ")";
    }
}