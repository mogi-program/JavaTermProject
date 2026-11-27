package course_management.view;

import course_management.domain.Assignment;
import course_management.domain.CourseSchedule;
import course_management.repository.AssignmentRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class CourseAssignmentDialog extends JDialog {
    private final CourseSchedule course;
    private final AssignmentRepository assignRepo = new AssignmentRepository();
    private final Runnable onChangedCallback;

    private DefaultTableModel model;
    private JTable table;
    private JTextArea detailArea;
    private ArrayList<Assignment> list;

    public CourseAssignmentDialog(JFrame parent, CourseSchedule course, Runnable onChangedCallback) {
        super(parent, "[" + course.getCourse_name() + "] 과목별 과제 상세 보기", true);
        this.course = course;
        this.onChangedCallback = onChangedCallback;

        setSize(650, 480);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // 상단 안내 라벨
        JLabel titleLbl = new JLabel("  📚 과목명: " + course.getCourse_name() + " (강의ID: " + course.getCourse_id() + ")");
        titleLbl.setFont(new Font("Malgun Gothic", Font.BOLD, 14));
        titleLbl.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
        add(titleLbl, BorderLayout.NORTH);

        // 중앙: 해당 과목 과제 목록 테이블 + 하단 상세 내용 보기 영역
        String[] headers = {"과제ID", "시작일", "마감일", "과제 요약", "상태"};
        model = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(26);

        detailArea = new JTextArea(6, 40);
        detailArea.setEditable(false);
        detailArea.setLineWrap(true);
        detailArea.setWrapStyleWord(true);
        detailArea.setFont(new Font("Malgun Gothic", Font.PLAIN, 13));
        detailArea.setText("위 목록에서 과제를 클릭하면 상세 내용이 여기에 표시됩니다.");

        JScrollPane detailScroll = new JScrollPane(detailArea);
        detailScroll.setBorder(BorderFactory.createTitledBorder("선택한 과제 상세 내용"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(table), detailScroll);
        splitPane.setDividerLocation(210);
        add(splitPane, BorderLayout.CENTER);

        // 하단 버튼 (새 과제 추가 / 완료 상태 토글 / 닫기)
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAdd = new JButton("이 과목에 새 과제 추가");
        JButton btnToggle = new JButton("완료/미완 상태 변경");
        JButton btnClose = new JButton("닫기");

        btnPanel.add(btnAdd);
        btnPanel.add(btnToggle);
        btnPanel.add(btnClose);
        add(btnPanel, BorderLayout.SOUTH);

        // 테이블 행 클릭 시 상세 내용 표시
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1 && row < list.size()) {
                    Assignment selected = list.get(row);
                    detailArea.setText(
                            "■ 강의명: " + selected.getCourse_name() + "\n" +
                                    "■ 기간: " + selected.getStart_date() + " ~ " + selected.getDue_date() + "\n" +
                                    "■ 상태: " + selected.getStatus() + "\n" +
                                    "--------------------------------------------------\n" +
                                    selected.getContent()
                    );
                }
            }
        });

        // 새 과제 추가 버튼
        btnAdd.addActionListener(e -> {
            new AutoAssignDialog(parent, course, () -> {
                loadCourseAssignments();
                if (onChangedCallback != null) onChangedCallback.run();
            }).setVisible(true);
        });

        // 완료/미완 토글 버튼
        btnToggle.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "상태를 변경할 과제를 먼저 선택하세요.");
                return;
            }
            Assignment target = list.get(row);
            target.setStatus("INCOMPLETE".equals(target.getStatus()) ? "COMPLETE" : "INCOMPLETE");
            assignRepo.update(target);
            loadCourseAssignments();
            if (onChangedCallback != null) onChangedCallback.run();
        });

        btnClose.addActionListener(e -> dispose());

        loadCourseAssignments();
    }

    private void loadCourseAssignments() {
        list = assignRepo.selectByCourseId(course.getCourse_id());
        model.setRowCount(0);
        if (list.isEmpty()) {
            detailArea.setText("해당 과목에 등록된 과제가 없습니다.");
            return;
        }
        for (Assignment a : list) {
            model.addRow(new Object[]{
                    a.getAssignment_id(),
                    a.getStart_date(),
                    a.getDue_date(),
                    a.getContent(),
                    a.getStatus()
            });
        }
    }
}