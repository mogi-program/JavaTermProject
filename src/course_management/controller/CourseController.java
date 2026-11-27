package course_management.controller;

import center.SetFrame;
import course_management.domain.Assignment;
import course_management.domain.CourseSchedule;
import course_management.repository.AssignmentRepository;
import course_management.repository.CourseRepository;
import course_management.view.AssignmentManagePan;
import course_management.view.AutoAssignDialog;
import course_management.view.CourseAssignmentDialog;
import course_management.view.CourseManagePan;
import course_management.view.TimetablePan;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CourseController extends JFrame {
    CourseRepository courseRepo = new CourseRepository();
    AssignmentRepository assignRepo = new AssignmentRepository();

    TimetablePan timetablePan = new TimetablePan();
    CourseManagePan coursePan = new CourseManagePan();
    AssignmentManagePan assignPan = new AssignmentManagePan();

    String lastCheckedMinute = ""; // 중복 알림 방지용

    public CourseController() {
        JTabbedPane tab = new JTabbedPane();

        // 1. 각 패널 UI(테이블 모델 포함) 먼저 초기화
        timetablePan.initView();
        coursePan.initView();
        assignPan.initView();

        // 2. DB 데이터 불러와서 테이블에 채우기
        refreshAllData();

        tab.add("강의 시간표", timetablePan);
        tab.add("강의 관리", coursePan);
        tab.add("과제 관리", assignPan);

        add(tab);

        // 1. 시간표 탭: 수업 더블클릭 시 해당 과목의 상세 과제 팝업 열기
        timetablePan.getTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = timetablePan.getTable().getSelectedRow();
                    int col = timetablePan.getTable().getSelectedColumn();
                    CourseSchedule clickedCourse = timetablePan.getCourseAt(row, col);
                    if (clickedCourse != null) {
                        new CourseAssignmentDialog(CourseController.this, clickedCourse, () -> refreshAllData()).setVisible(true);
                    }
                }
            }
        });

        // 2. 강의 관리 탭: 테이블 클릭 및 추가/수정/삭제 이벤트 연결
        coursePan.getTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = coursePan.getTable().getSelectedRow();
                if (row != -1) {
                    coursePan.setTextField(row);
                }
            }
        });

        coursePan.getBtnInsert().addActionListener(e -> {
            CourseSchedule cs = coursePan.getCourseSchedule();
            if (cs.getCourse_name().isEmpty()) {
                JOptionPane.showMessageDialog(this, "강의명을 입력하세요.");
                return;
            }
            courseRepo.insert(cs);
            refreshAllData();
            JOptionPane.showMessageDialog(this, "강의가 추가되었습니다.");
        });

        coursePan.getBtnUpdate().addActionListener(e -> {
            CourseSchedule cs = coursePan.getCourseSchedule();
            if (cs.getCourse_id() == 0) {
                JOptionPane.showMessageDialog(this, "수정할 강의를 목록에서 먼저 선택하세요.");
                return;
            }
            courseRepo.update(cs);
            refreshAllData();
            JOptionPane.showMessageDialog(this, "강의가 수정되었습니다.");
        });

        coursePan.getBtnDelete().addActionListener(e -> {
            CourseSchedule cs = coursePan.getCourseSchedule();
            if (cs.getCourse_id() != 0) {
                int confirm = JOptionPane.showConfirmDialog(this, "해당 강의와 연결된 시간표 및 과제도 모두 삭제됩니다. 삭제하시겠습니까?", "강의 삭제 확인", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    courseRepo.delete(cs.getCourse_id());
                    refreshAllData();
                    JOptionPane.showMessageDialog(this, "강의가 삭제되었습니다.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "삭제할 강의를 목록에서 먼저 선택하세요.");
            }
        });

        // 3. 과제 관리 탭: 과목별 필터, 테이블 클릭 및 등록/수정/삭제 이벤트 연결
        assignPan.getComboFilterCourse().addActionListener(e -> {
            Integer filterCourseId = assignPan.getSelectedFilterCourseId();
            if (filterCourseId == null) {
                assignPan.setAssignmentList(assignRepo.select(false));
            } else {
                assignPan.setAssignmentList(assignRepo.selectByCourseId(filterCourseId));
            }
            assignPan.putSearchResult();
        });

        assignPan.getTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = assignPan.getTable().getSelectedRow();
                if (row != -1) {
                    assignPan.setTextField(row);
                }
            }
        });

        assignPan.getBtnInsert().addActionListener(e -> {
            try {
                Assignment a = assignPan.getAssignment();
                if (a.getContent().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "과제 내용을 입력하세요.");
                    return;
                }
                assignRepo.insert(a);
                refreshAllData();
                JOptionPane.showMessageDialog(this, "과제가 등록되었습니다.");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "날짜 형식은 YYYY-MM-DD 로 입력해야 합니다.");
            }
        });

        assignPan.getBtnUpdate().addActionListener(e -> {
            try {
                Assignment a = assignPan.getAssignment();
                if (a.getAssignment_id() == 0) {
                    JOptionPane.showMessageDialog(this, "수정할 과제를 목록에서 먼저 선택하세요.");
                    return;
                }
                assignRepo.update(a);
                refreshAllData();
                JOptionPane.showMessageDialog(this, "과제가 수정되었습니다.");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "날짜 형식은 YYYY-MM-DD 로 입력해야 합니다.");
            }
        });

        assignPan.getBtnDelete().addActionListener(e -> {
            Assignment a = assignPan.getAssignment();
            if (a.getAssignment_id() != 0) {
                assignRepo.delete(a.getAssignment_id());
                refreshAllData();
                JOptionPane.showMessageDialog(this, "과제가 삭제되었습니다.");
            } else {
                JOptionPane.showMessageDialog(this, "삭제할 과제를 목록에서 먼저 선택하세요.");
            }
        });

        // 4. 실시간 시간 감시 및 알림/자동 팝업 타이머 시작 (10초 주기)
        startAlarmTimer();

        SetFrame.setFrame(this, 900, 620, "강의 시간표 및 과제 관리 프로그램");
    }

    // 모든 패널의 데이터를 DB에서 최신화 (과목 필터 상태 유지)
    private void refreshAllData() {
        ArrayList<CourseSchedule> courses = courseRepo.selectAll();
        Integer filterCourseId = assignPan.getSelectedFilterCourseId();
        ArrayList<Assignment> assignments = (filterCourseId == null)
                ? assignRepo.select(false)
                : assignRepo.selectByCourseId(filterCourseId);

        timetablePan.setCourseList(courses);
        coursePan.setCourseList(courses);
        assignPan.setCourseCombo(courses);
        assignPan.setAssignmentList(assignments);

        timetablePan.putSearchResult();
        coursePan.putSearchResult();
        assignPan.putSearchResult();
    }

    // 09:00 미완료 과제 알림 / 강의 시작·종료 알림 / 종료 10분 전 과제 입력창 호출
    private void startAlarmTimer() {
        Timer timer = new Timer(10000, e -> {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
            String currentMinute = now.format(fmt);

            if (currentMinute.equals(lastCheckedMinute)) return;
            lastCheckedMinute = currentMinute;

            int todayDay = now.getDayOfWeek().getValue(); // 월(1) ~ 일(7)

            // 09:00 미완료 과제 알림
            if ("09:00".equals(currentMinute)) {
                ArrayList<Assignment> incompletes = assignRepo.select(true);
                if (!incompletes.isEmpty()) {
                    StringBuilder sb = new StringBuilder("[09:00 알림] 현재 미완료된 과제 목록입니다.\n\n");
                    for (Assignment a : incompletes) {
                        sb.append("- [").append(a.getCourse_name()).append("] ")
                                .append(a.getContent()).append(" (마감: ").append(a.getDue_date()).append(")\n");
                    }
                    JOptionPane.showMessageDialog(this, sb.toString(), "미완료 과제 알림", JOptionPane.INFORMATION_MESSAGE);
                }
            }

            // 강의 시간표 기반 시작 / 종료 / 종료 10분 전 팝업
            ArrayList<CourseSchedule> courses = courseRepo.selectAll();
            for (CourseSchedule cs : courses) {
                if (cs.getDay_of_week() == todayDay) {
                    if (cs.getStart_time().equals(currentMinute)) {
                        JOptionPane.showMessageDialog(this, "[" + cs.getCourse_name() + "] 강의가 시작되었습니다!", "강의 시작", JOptionPane.INFORMATION_MESSAGE);
                    }
                    if (cs.getEnd_time().equals(currentMinute)) {
                        JOptionPane.showMessageDialog(this, "[" + cs.getCourse_name() + "] 강의가 종료되었습니다!", "강의 종료", JOptionPane.INFORMATION_MESSAGE);
                    }
                    LocalTime endTime = LocalTime.parse(cs.getEnd_time(), fmt);
                    if (endTime.minusMinutes(10).format(fmt).equals(currentMinute)) {
                        new AutoAssignDialog(this, cs, this::refreshAllData).setVisible(true);
                    }
                }
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        new CourseController();
    }
}