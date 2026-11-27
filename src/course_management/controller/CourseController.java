package course_management.controller;

import center.SetFrame;
import course_management.domain.Assignment;
import course_management.domain.CourseSchedule;
import course_management.repository.AssignmentRepository;
import course_management.repository.CourseRepository;
import course_management.view.AssignmentManagePan;
import course_management.view.AutoAssignDialog;
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

    String lastCheckedMinute = ""; // 중복 알림 방지

    public CourseController() {
        JTabbedPane tab = new JTabbedPane();

        // 초기 데이터 로드 및 뷰 초기화
        refreshAllData();
        timetablePan.initView();
        coursePan.initView();
        assignPan.initView();

        tab.add("강의 시간표", timetablePan);
        tab.add("강의 관리", coursePan);
        tab.add("과제 관리", assignPan);

        add(tab);

        // 1. 강의 관리 이벤트 연결 (삽입 / 수정 / 삭제 / 테이블 클릭)
        coursePan.getTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = coursePan.getTable().getSelectedRow();
                if (row != -1) coursePan.setTextField(row);
            }
        });

        coursePan.getBtnInsert().addActionListener(e -> {
            courseRepo.insert(coursePan.getCourseSchedule());
            refreshAllData();
            JOptionPane.showMessageDialog(this, "강의가 추가되었습니다.");
        });

        coursePan.getBtnUpdate().addActionListener(e -> {
            courseRepo.update(coursePan.getCourseSchedule());
            refreshAllData();
            JOptionPane.showMessageDialog(this, "강의가 수정되었습니다.");
        });

        coursePan.getBtnDelete().addActionListener(e -> {
            CourseSchedule cs = coursePan.getCourseSchedule();
            if (cs.getCourse_id() != 0) {
                courseRepo.delete(cs.getCourse_id());
                refreshAllData();
                JOptionPane.showMessageDialog(this, "강의가 삭제되었습니다.");
            }
        });

        // 2. 과제 관리 이벤트 연결 (삽입 / 수정 / 삭제 / 테이블 클릭)
        assignPan.getTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = assignPan.getTable().getSelectedRow();
                if (row != -1) assignPan.setTextField(row);
            }
        });

        assignPan.getBtnInsert().addActionListener(e -> {
            assignRepo.insert(assignPan.getAssignment());
            refreshAllData();
            JOptionPane.showMessageDialog(this, "과제가 등록되었습니다.");
        });

        assignPan.getBtnUpdate().addActionListener(e -> {
            assignRepo.update(assignPan.getAssignment());
            refreshAllData();
            JOptionPane.showMessageDialog(this, "과제가 수정되었습니다.");
        });

        assignPan.getBtnDelete().addActionListener(e -> {
            Assignment a = assignPan.getAssignment();
            if (a.getAssignment_id() != 0) {
                assignRepo.delete(a.getAssignment_id());
                refreshAllData();
                JOptionPane.showMessageDialog(this, "과제가 삭제되었습니다.");
            }
        });

        // 3. 실시간 알림 및 팝업 타이머 시작 (10초 주기 체크)
        startAlarmTimer();

        SetFrame.setFrame(this, 900, 600, "강의 시간표 및 과제 관리 프로그램");
    }

    // 모든 패널의 데이터를 DB에서 최신화
    private void refreshAllData() {
        ArrayList<CourseSchedule> courses = courseRepo.selectAll();
        ArrayList<Assignment> assignments = assignRepo.select(false);

        timetablePan.setCourseList(courses);
        coursePan.setCourseList(courses);
        assignPan.setCourseCombo(courses);
        assignPan.setAssignmentList(assignments);

        if (timetablePan.getComponentCount() > 0) timetablePan.putSearchResult();
        if (coursePan.getComponentCount() > 0) coursePan.putSearchResult();
        if (assignPan.getComponentCount() > 0) assignPan.putSearchResult();
    }

    // 요구사항 5, 6, 7 처리 스케줄러
    private void startAlarmTimer() {
        Timer timer = new Timer(10000, e -> {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
            String currentMinute = now.format(fmt);

            if (currentMinute.equals(lastCheckedMinute)) return;
            lastCheckedMinute = currentMinute;

            int todayDay = now.getDayOfWeek().getValue(); // 월(1) ~ 일(7)

            // [요구사항 5] 09:00 미완료 과제 알림
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

            // [요구사항 6 & 7] 강의 시작 / 종료 / 종료 10분 전 팝업
            ArrayList<CourseSchedule> courses = courseRepo.selectAll();
            for (CourseSchedule cs : courses) {
                if (cs.getDay_of_week() == todayDay) {
                    // 강의 시작 알림
                    if (cs.getStart_time().equals(currentMinute)) {
                        JOptionPane.showMessageDialog(this, "[" + cs.getCourse_name() + "] 강의가 시작되었습니다!", "강의 시작", JOptionPane.INFORMATION_MESSAGE);
                    }
                    // 강의 종료 알림
                    if (cs.getEnd_time().equals(currentMinute)) {
                        JOptionPane.showMessageDialog(this, "[" + cs.getCourse_name() + "] 강의가 종료되었습니다!", "강의 종료", JOptionPane.INFORMATION_MESSAGE);
                    }
                    // 강의 종료 10분 전 과제 입력창 호출
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