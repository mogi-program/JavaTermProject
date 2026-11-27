package course_management.view;

import course_management.domain.CourseSchedule;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class TimetablePan extends JPanel {
    JTable table;
    DefaultTableModel model;
    String[] tableHeader = {"교시 (시간)", "월", "화", "수", "목", "금", "토", "일"};
    ArrayList<CourseSchedule> courseList;
    CourseSchedule[][] cellCourseMap; // 각 셀에 매핑된 강의 객체 저장용

    String[] rowLabels = {
            "1교시 (09:00~09:50)",
            "2교시 (10:00~10:50)",
            "3교시 (11:00~11:50)",
            "4교시 (12:00~12:50)",
            "5교시 [점심시간 12:50~13:30]",
            "6교시 (13:40~14:30)",
            "7교시 (14:40~15:30)",
            "8교시 (15:40~16:30)",
            "9교시 (16:40~17:30)"
    };
    String[] rowStartTimes = {"09:00", "10:00", "11:00", "12:00", "12:50", "13:40", "14:40", "15:40", "16:40"};
    String[] rowEndTimes   = {"09:50", "10:50", "11:50", "12:50", "13:30", "14:30", "15:30", "16:30", "17:30"};

    public TimetablePan() {
        courseList = new ArrayList<>();
        cellCourseMap = new CourseSchedule[rowLabels.length][8];
        setLayout(new BorderLayout());
    }

    public void initView() {
        model = new DefaultTableModel(tableHeader, rowLabels.length) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(40);
        table.getColumnModel().getColumn(0).setPreferredWidth(160);

        JLabel guideLbl = new JLabel("  ※ 시간표에서 과목을 더블클릭하면 해당 과목의 상세 과제 목록을 확인할 수 있습니다.");
        guideLbl.setBorder(BorderFactory.createEmptyBorder(6, 5, 6, 5));

        add(guideLbl, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        putSearchResult();
    }

    public void putSearchResult() {
        cellCourseMap = new CourseSchedule[rowLabels.length][8];

        for (int r = 0; r < rowLabels.length; r++) {
            model.setValueAt(rowLabels[r], r, 0);
            for (int c = 1; c <= 7; c++) {
                if (r == 4) {
                    model.setValueAt("--- 점심시간 ---", r, c);
                } else {
                    model.setValueAt("", r, c);
                }
            }
        }

        for (CourseSchedule cs : courseList) {
            int col = cs.getDay_of_week();
            if (col < 1 || col > 7) continue;

            for (int r = 0; r < rowLabels.length; r++) {
                if (r == 4) continue;
                if (rowStartTimes[r].compareTo(cs.getStart_time()) >= 0 &&
                        rowEndTimes[r].compareTo(cs.getEnd_time()) <= 0) {
                    model.setValueAt(cs.getCourse_name(), r, col);
                    cellCourseMap[r][col] = cs; // 셀 좌표에 강의 객체 매핑
                }
            }
        }
    }

    public CourseSchedule getCourseAt(int row, int col) {
        if (row >= 0 && row < rowLabels.length && col >= 1 && col <= 7) {
            return cellCourseMap[row][col];
        }
        return null;
    }

    public JTable getTable() { return table; }
    public void setCourseList(ArrayList<CourseSchedule> courseList) { this.courseList = courseList; }
}