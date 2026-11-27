package course_management.view;

import course_management.domain.CourseSchedule;
import course_management.repository.CourseRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class CourseManagePan extends JPanel {
    JTable table;
    DefaultTableModel model;
    ArrayList<CourseSchedule> courseList;
    String[] tableHeader = {"강의ID", "스케줄ID", "강의명", "요일", "시작시간", "종료시간"};
    String[] dayNames = {"월", "화", "수", "목", "금", "토", "일"};

    // 1~9교시 고정 시간 정의 (점심시간 5교시 제외)
    String[] periodLabels = {
            "1교시 (09:00~09:50)",
            "2교시 (10:00~10:50)",
            "3교시 (11:00~11:50)",
            "4교시 (12:00~12:50)",
            "6교시 (13:40~14:30)",
            "7교시 (14:40~15:30)",
            "8교시 (15:40~16:30)",
            "9교시 (16:40~17:30)"
    };
    String[] startTimes = {"09:00", "10:00", "11:00", "12:00", "13:40", "14:40", "15:40", "16:40"};
    String[] endTimes   = {"09:50", "10:50", "11:50", "12:50", "14:30", "15:30", "16:30", "17:30"};

    JTextField tfCourseId = new JTextField();
    JTextField tfScheduleId = new JTextField();
    JTextField tfCourseName = new JTextField();
    JComboBox<String> comboDay = new JComboBox<>(dayNames);
    JComboBox<String> comboStartPeriod = new JComboBox<>(periodLabels);
    JComboBox<String> comboEndPeriod = new JComboBox<>(periodLabels);

    JButton btnInsert = new JButton("강의 추가");
    JButton btnUpdate = new JButton("강의 수정");
    JButton btnDelete = new JButton("강의 삭제");
    JPanel panS = new JPanel(new GridLayout(4, 4, 15, 5));

    public CourseManagePan() {
        courseList = new ArrayList<>();
        setLayout(new BorderLayout());

        tfCourseId.setEditable(false);
        tfScheduleId.setEditable(false);

        panS.add(new JLabel("강의ID(자동):", JLabel.RIGHT)); panS.add(tfCourseId);
        panS.add(new JLabel("스케줄ID(자동):", JLabel.RIGHT)); panS.add(tfScheduleId);
        panS.add(new JLabel("강의명:", JLabel.RIGHT)); panS.add(tfCourseName);
        panS.add(new JLabel("요일:", JLabel.RIGHT)); panS.add(comboDay);
        panS.add(new JLabel("시작 교시:", JLabel.RIGHT)); panS.add(comboStartPeriod);
        panS.add(new JLabel("종료 교시:", JLabel.RIGHT)); panS.add(comboEndPeriod);

        panS.add(new JLabel(""));
        JPanel btnGroup = new JPanel(new FlowLayout());
        btnGroup.add(btnInsert);
        btnGroup.add(btnUpdate);
        btnGroup.add(btnDelete);
        panS.add(btnGroup);
    }

    public void initView() {
        model = new DefaultTableModel(tableHeader, courseList.size()) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        putSearchResult();

        add(scroll, "Center");
        add(panS, "South");
    }

    public void putSearchResult() {
        model.setRowCount(courseList.size());
        CourseRepository repo = new CourseRepository();
        for (int i = 0; i < courseList.size(); i++) {
            CourseSchedule cs = courseList.get(i);
            model.setValueAt(cs.getCourse_id(), i, 0);
            model.setValueAt(cs.getSchedule_id(), i, 1);
            model.setValueAt(cs.getCourse_name(), i, 2);
            model.setValueAt(repo.toDayName(cs.getDay_of_week()), i, 3);
            model.setValueAt(cs.getStart_time(), i, 4);
            model.setValueAt(cs.getEnd_time(), i, 5);
        }
    }

    // 테이블 행 클릭 시 해당 시작/종료 시각에 맞는 교시 콤보박스 자동 선택
    public void setTextField(int rowIndex) {
        tfCourseId.setText(model.getValueAt(rowIndex, 0).toString());
        tfScheduleId.setText(model.getValueAt(rowIndex, 1).toString());
        tfCourseName.setText(model.getValueAt(rowIndex, 2).toString());
        comboDay.setSelectedItem(model.getValueAt(rowIndex, 3).toString());

        String sTime = model.getValueAt(rowIndex, 4).toString();
        String eTime = model.getValueAt(rowIndex, 5).toString();
        for (int i = 0; i < startTimes.length; i++) {
            if (startTimes[i].equals(sTime)) comboStartPeriod.setSelectedIndex(i);
            if (endTimes[i].equals(eTime)) comboEndPeriod.setSelectedIndex(i);
        }
    }

    // 선택된 시작 교시의 시작시각 ~ 종료 교시의 종료시각을 추출
    public CourseSchedule getCourseSchedule() {
        CourseSchedule cs = new CourseSchedule();
        if (!tfCourseId.getText().isEmpty()) {
            cs.setCourse_id(Integer.parseInt(tfCourseId.getText()));
            cs.setSchedule_id(Integer.parseInt(tfScheduleId.getText()));
        }
        cs.setCourse_name(tfCourseName.getText().trim());
        cs.setDay_of_week(new CourseRepository().toDayInt(comboDay.getSelectedItem().toString()));

        int startIdx = comboStartPeriod.getSelectedIndex();
        int endIdx = comboEndPeriod.getSelectedIndex();
        // 만약 종료 교시를 시작 교시보다 앞선 교시로 선택했다면 시작 교시와 동일하게 보정
        if (endIdx < startIdx) endIdx = startIdx;

        cs.setStart_time(startTimes[startIdx]);
        cs.setEnd_time(endTimes[endIdx]);
        return cs;
    }

    public void setCourseList(ArrayList<CourseSchedule> courseList) { this.courseList = courseList; }
    public JButton getBtnInsert() { return btnInsert; }
    public JButton getBtnUpdate() { return btnUpdate; }
    public JButton getBtnDelete() { return btnDelete; }
    public JTable getTable() { return table; }
}