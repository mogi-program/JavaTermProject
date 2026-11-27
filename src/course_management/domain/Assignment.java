package course_management.domain;

import java.sql.Date;

public class Assignment {
    private int assignment_id;
    private int course_id;
    private String course_name; // 조인용
    private Date start_date;
    private Date due_date;
    private String content;
    private String status; // INCOMPLETE, COMPLETE

    public int getAssignment_id() { return assignment_id; }
    public void setAssignment_id(int assignment_id) { this.assignment_id = assignment_id; }

    public int getCourse_id() { return course_id; }
    public void setCourse_id(int course_id) { this.course_id = course_id; }

    public String getCourse_name() { return course_name; }
    public void setCourse_name(String course_name) { this.course_name = course_name; }

    public Date getStart_date() { return start_date; }
    public void setStart_date(Date start_date) { this.start_date = start_date; }

    public Date getDue_date() { return due_date; }
    public void setDue_date(Date due_date) { this.due_date = due_date; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}