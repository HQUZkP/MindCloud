package com.kaipingzhou.mindcloud.bean;

/**
 * Created by 周开平 on 2017/3/28 23:12.
 * qq 275557625@qq.com
 * 作用：用户信息实体类
 */

public class UserInfo {

    private String StudentId;

    private String StudentName;

    private String StudentAcademy;

    private String StednetGrade;

    private String StudentSpecialty;

    private String StudentClass;

    public String getStudentId() {
        return StudentId;
    }

    public void setStudentId(String studentId) {
        StudentId = studentId;
    }

    public String getStudentName() {
        return StudentName;
    }

    public void setStudentName(String studentName) {
        StudentName = studentName;
    }

    public String getStudentAcademy() {
        return StudentAcademy;
    }

    public void setStudentAcademy(String studentAcademy) {
        StudentAcademy = studentAcademy;
    }

    public String getStednetGrade() {
        return StednetGrade;
    }

    public void setStednetGrade(String stednetGrade) {
        StednetGrade = stednetGrade;
    }

    public String getStudentSpecialty() {
        return StudentSpecialty;
    }

    public void setStudentSpecialty(String studentSpecialty) {
        StudentSpecialty = studentSpecialty;
    }

    public String getStudentClass() {
        return StudentClass;
    }

    public void setStudentClass(String studentClass) {
        StudentClass = studentClass;
    }

    @Override
    public String toString() {
        return StudentId + "," + StudentName + "," + StudentAcademy + ","
                + StednetGrade + "," + StudentSpecialty + "," + StudentClass;
    }
}
