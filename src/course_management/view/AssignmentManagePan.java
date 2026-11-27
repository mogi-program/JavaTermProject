package course_management.view;

import course_management.domain.Assignment;
import course_management.domain.CourseSchedule;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

public class AssignmentManagePan extends JPanel {
    JTable table;
    DefaultTableModel model;
    ArrayList<Assignment> assignmentList;
    String[] tableHeader = {"과제ID", "강의ID", "강의명", "시작일", "마감일", "과제내용", "상태"};
    String[] statusStr = {"INCOMPLETE", "COMPLETE"};

    // 상단 과목별 필터 콤보박스
    JComboBox<String> comboFilterCourse = new JComboBox<>();
    ArrayList<CourseSchedule> allCourses = new ArrayList<>();

    // 중앙 우측/하단 상세 내용 보기 영역
    JTextArea taDetailView = new JTextArea(5, 30);

    // 하단 등록/수정 입력 폼
    JTextField tfAssignId = new JTextField();
    JComboBox<CourseSchedule> comboCourse = new JComboBox<>();
    JTextField tfStartDate = new JTextField(LocalDate.now().toString());
    JTextField tfDueDate = new JTextField(LocalDate.now().plusDays(7).toString());
    JTextField tfContent = new JTextField();
    JComboBox<String> comboStatus = new JComboBox<>(statusStr);

    JButton btnInsert = new JButton("과제 등록");
    JButton btnUpdate = new JButton("과제 수정");
    JButton btnDelete = new JButton("과제 삭제");
    JPanel panS = new JPanel(new GridLayout(4, 4, 15, 5));

    public AssignmentManagePan() {
        assignmentList = new ArrayList<>();
        setLayout(new BorderLayout(5, 5));
        tfAssignId.setEditable(false);

        // 상단 필터 바
        JPanel panN = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panN.add(new JLabel("  과목별 모아보기: "));
        comboFilterCourse.addItem("전체 과목 보기");
        panN.add(comboFilterCourse);
        add(panN, BorderLayout.NORTH);

        // 상세 내용 영역 설정
        taDetailView.setEditable(false);
        taDetailView.setLineWrap(true);
        taDetailView.setWrapStyleWord(true);
        taDetailView.setFont(new Font("Malgun Gothic", Font.PLAIN, 13));
        taDetailView.setText("과제를 선택하면 상세 내용이 표시됩니다.");

        // 하단 입력 폼
        panS.add(new JLabel("과제ID(자동):", JLabel.RIGHT)); panS.add(tfAssignId);
        panS.add(new JLabel("강의 선택:", JLabel.RIGHT)); panS.add(comboCourse);
        panS.add(new JLabel("시작일(YYYY-MM-DD):", JLabel.RIGHT)); panS.add(tfStartDate);
        panS.add(new JLabel("마감일(YYYY-MM-DD):", JLabel.RIGHT)); panS.add(tfDueDate);
        panS.add(new JLabel("과제 내용:", JLabel.RIGHT)); panS.add(tfContent);
        panS.add(new JLabel("진행 상태:", JLabel.RIGHT)); panS.add(comboStatus);

        panS.add(new JLabel(""));
        JPanel btnGroup = new JPanel(new FlowLayout());
        btnGroup.add(btnInsert);
        btnGroup.add(btnUpdate);
        btnGroup.add(btnDelete);
        panS.add(btnGroup);
    }

    public void initView() {
        model = new DefaultTableModel(tableHeader, assignmentList.size()) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        JScrollPane tableScroll = new JScrollPane(table);

        JScrollPane detailScroll = new JScrollPane(taDetailView);
        detailScroll.setBorder(BorderFactory.createTitledBorder("선택한 과제 상세 내용"));

        JSplitPane centerSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScroll, detailScroll);
        centerSplit.setDividerLocation(220);

        putSearchResult();

        add(centerSplit, BorderLayout.CENTER);
        add(panS, BorderLayout.SOUTH);
    }

    public void putSearchResult() {
        model.setRowCount(assignmentList.size());
        for (int i = 0; i < assignmentList.size(); i++) {
            Assignment a = assignmentList.get(i);
            model.setValueAt(a.getAssignment_id(), i, 0);
            model.setValueAt(a.getCourse_id(), i, 1);
            model.setValueAt(a.getCourse_name(), i, 2);
            model.setValueAt(a.getStart_date(), i, 3);
            model.setValueAt(a.getDue_date(), i, 4);
            model.setValueAt(a.getContent(), i, 5);
            model.setValueAt(a.getStatus(), i, 6);
        }
    }

    public void setCourseCombo(ArrayList<CourseSchedule> courses) {
        this.allCourses = courses;
        Object prevFilter = comboFilterCourse.getSelectedItem();

        comboCourse.removeAllItems();
        comboFilterCourse.removeAllItems();
        comboFilterCourse.addItem("전체 과목 보기");

        for (CourseSchedule cs : courses) {
            comboCourse.addItem(cs);
            comboFilterCourse.addItem(cs.getCourse_id() + " : " + cs.getCourse_name());
        }
        if (prevFilter != null) comboFilterCourse.setSelectedItem(prevFilter);
    }

    public void setTextField(int rowIndex) {
        tfAssignId.setText(model.getValueAt(rowIndex, 0).toString());
        int courseId = Integer.parseInt(model.getValueAt(rowIndex, 1).toString());
        for (int i = 0; i < comboCourse.getItemCount(); i++) {
            if (comboCourse.getItemAt(i).getCourse_id() == courseId) {
                comboCourse.setSelectedIndex(i);
                break;
            }
        }
        String courseName = model.getValueAt(rowIndex, 2).toString();
        String sDate = model.getValueAt(rowIndex, 3).toString();
        String dDate = model.getValueAt(rowIndex, 4).toString();
        String content = model.getValueAt(rowIndex, 5).toString();
        String status = model.getValueAt(rowIndex, 6).toString();

        tfStartDate.setText(sDate);
        tfDueDate.setText(dDate);
        tfContent.setText(content);
        comboStatus.setSelectedItem(status);

        // 상세 내용 영역 업데이트
        taDetailView.setText(
                "[" + courseName + "]  기간: " + sDate + " ~ " + dDate + "  |  상태: " + status + "\n" +
                        "----------------------------------------------------------------------\n" +
                        content
        );
    }

    // 현재 필터에서 선택된 course_id 반환 (전체 보기면 null)
    public Integer getSelectedFilterCourseId() {
        int idx = comboFilterCourse.getSelectedIndex();
        if (idx <= 0 || idx - 1 >= allCourses.size()) return null;
        return allCourses.get(idx - 1).getCourse_id();
    }

    public Assignment getAssignment() {
        Assignment a = new Assignment();
        if (!tfAssignId.getText().isEmpty()) {
            a.setAssignment_id(Integer.parseInt(tfAssignId.getText()));
        }
        CourseSchedule selected = (CourseSchedule) comboCourse.getSelectedItem();
        if (selected != null) {
            a.setCourse_id(selected.getCourse_id());
        }
        a.setStart_date(Date.valueOf(tfStartDate.getText().trim()));
        a.setDue_date(Date.valueOf(tfDueDate.getText().trim()));
        a.setContent(tfContent.getText().trim());
        a.setStatus(comboStatus.getSelectedItem().toString());
        return a;
    }

    public void setAssignmentList(ArrayList<Assignment> assignmentList) { this.assignmentList = assignmentList; }
    public JComboBox<String> getComboFilterCourse() { return comboFilterCourse; }
    public JButton getBtnInsert() { return btnInsert; }
    public JButton getBtnUpdate() { return btnUpdate; }
    public JButton getBtnDelete() { return btnDelete; }
    public JTable getTable() { return table; }
}