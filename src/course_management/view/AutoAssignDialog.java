package course_management.view;

import course_management.domain.Assignment;
import course_management.domain.CourseSchedule;
import course_management.repository.AssignmentRepository;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;

public class AutoAssignDialog extends JDialog {
    public AutoAssignDialog(JFrame parent, CourseSchedule course, Runnable onSavedCallback) {
        super(parent, "강의 종료 10분 전 - 과제 등록", true);
        setSize(400, 260);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel panC = new JPanel(new GridLayout(4, 2, 10, 10));
        panC.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        JTextField tfCourse = new JTextField(course.getCourse_name());
        tfCourse.setEditable(false);
        JTextField tfStart = new JTextField(LocalDate.now().toString());
        JTextField tfDue = new JTextField(LocalDate.now().plusDays(7).toString());
        JTextField tfContent = new JTextField();

        panC.add(new JLabel("해당 강의:", JLabel.RIGHT)); panC.add(tfCourse);
        panC.add(new JLabel("시작일(YYYY-MM-DD):", JLabel.RIGHT)); panC.add(tfStart);
        panC.add(new JLabel("마감일(YYYY-MM-DD):", JLabel.RIGHT)); panC.add(tfDue);
        panC.add(new JLabel("과제 내용:", JLabel.RIGHT)); panC.add(tfContent);

        JButton btnSave = new JButton("과제 저장");
        btnSave.addActionListener(e -> {
            if (tfContent.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "과제 내용을 입력하세요.");
                return;
            }
            Assignment a = new Assignment();
            a.setCourse_id(course.getCourse_id());
            a.setStart_date(Date.valueOf(tfStart.getText().trim()));
            a.setDue_date(Date.valueOf(tfDue.getText().trim()));
            a.setContent(tfContent.getText().trim());

            new AssignmentRepository().insert(a);
            JOptionPane.showMessageDialog(this, "과제가 등록되었습니다.");
            if (onSavedCallback != null) onSavedCallback.run();
            dispose();
        });

        add(panC, "Center");
        add(btnSave, "South");
    }
}