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

    JTextField tfCourseId = new JTextField();
    JTextField tfScheduleId = new JTextField();
    JTextField tfCourseName = new JTextField();
    JComboBox<String> comboDay = new JComboBox<>(dayNames);
    JTextField tfStartTime = new JTextField("09:00");
    JTextField tfEndTime = new JTextField("11:00");

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
        panS.add(new JLabel("시작시간(HH:mm):", JLabel.RIGHT)); panS.add(tfStartTime);
        panS.add(new JLabel("종료시간(HH:mm):", JLabel.RIGHT)); panS.add(tfEndTime);

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

    public void setTextField(int rowIndex) {
        tfCourseId.setText(model.getValueAt(rowIndex, 0).toString());
        tfScheduleId.setText(model.getValueAt(rowIndex, 1).toString());
        tfCourseName.setText(model.getValueAt(rowIndex, 2).toString());
        comboDay.setSelectedItem(model.getValueAt(rowIndex, 3).toString());
        tfStartTime.setText(model.getValueAt(rowIndex, 4).toString());
        tfEndTime.setText(model.getValueAt(rowIndex, 5).toString());
    }

    public CourseSchedule getCourseSchedule() {
        CourseSchedule cs = new CourseSchedule();
        if (!tfCourseId.getText().isEmpty()) {
            cs.setCourse_id(Integer.parseInt(tfCourseId.getText()));
            cs.setSchedule_id(Integer.parseInt(tfScheduleId.getText()));
        }
        cs.setCourse_name(tfCourseName.getText().trim());
        cs.setDay_of_week(new CourseRepository().toDayInt(comboDay.getSelectedItem().toString()));
        cs.setStart_time(tfStartTime.getText().trim());
        cs.setEnd_time(tfEndTime.getText().trim());
        return cs;
    }

    public void setCourseList(ArrayList<CourseSchedule> courseList) { this.courseList = courseList; }
    public JButton getBtnInsert() { return btnInsert; }
    public JButton getBtnUpdate() { return btnUpdate; }
    public JButton getBtnDelete() { return btnDelete; }
    public JTable getTable() { return table; }
}