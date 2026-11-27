package course_management.view;

import course_management.domain.CourseSchedule;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class TimetablePan extends JPanel {
    JTable table;
    DefaultTableModel model;
    String[] tableHeader = {"시간", "월", "화", "수", "목", "금", "토", "일"};
    ArrayList<CourseSchedule> courseList;

    public TimetablePan() {
        courseList = new ArrayList<>();
        setLayout(new BorderLayout());
    }

    public void initView() {
        model = new DefaultTableModel(tableHeader, 13) { // 09:00 ~ 21:00
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(35);
        JScrollPane scroll = new JScrollPane(table);
        putSearchResult();

        add(scroll, "Center");
    }

    public void putSearchResult() {
        for (int r = 0; r < 13; r++) {
            model.setValueAt(String.format("%02d:00", r + 9), r, 0);
            for (int c = 1; c <= 7; c++) {
                model.setValueAt("", r, c);
            }
        }

        for (CourseSchedule cs : courseList) {
            int startH = Integer.parseInt(cs.getStart_time().split(":")[0]);
            int endH = Integer.parseInt(cs.getEnd_time().split(":")[0]);
            int col = cs.getDay_of_week(); // 1(월) ~ 7(일)

            for (int h = startH; h < endH; h++) {
                int row = h - 9;
                if (row >= 0 && row < 13 && col >= 1 && col <= 7) {
                    model.setValueAt(cs.getCourse_name() + " (" + cs.getStart_time() + "~" + cs.getEnd_time() + ")", row, col);
                }
            }
        }
    }

    public void setCourseList(ArrayList<CourseSchedule> courseList) {
        this.courseList = courseList;
    }
}