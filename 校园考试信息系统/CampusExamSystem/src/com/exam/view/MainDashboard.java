package com.exam.view;

import javax.swing.*;
import java.awt.*;

public class MainDashboard extends JFrame {

    // 构造函数接收 userRole (ADMIN 或 USER)
    public MainDashboard(String userRole) {
        // 设置窗口标题，包含当前身份
        String roleName = "ADMIN".equals(userRole) ? "管理员" : "访客/学生";
        setTitle("校园考试信息系统 - 当前身份: " + roleName);
        
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 使用网格布局 (2行2列)
        setLayout(new GridLayout(2, 2, 20, 20));
        
        // 设置窗口边距，让按钮不要贴着边框
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // === 1. 创建四个功能按钮 ===
        // 这里去掉了 createModuleButton 方法中复杂的颜色设置，直接用默认样式
        JButton btnBasic = new JButton("基础信息管理");
        btnBasic.setFont(new Font("微软雅黑", Font.PLAIN, 20)); // 只保留字体设置，不改颜色

        JButton btnExam = new JButton("考试考务管理");
        btnExam.setFont(new Font("微软雅黑", Font.PLAIN, 20));

        JButton btnScore = new JButton("成绩数据管理");
        btnScore.setFont(new Font("微软雅黑", Font.PLAIN, 20));

        JButton btnSystem = new JButton("系统权限管理");
        btnSystem.setFont(new Font("微软雅黑", Font.PLAIN, 20));

        // === 2. 绑定点击事件 ===
        btnBasic.addActionListener(e -> new BasicInfoView().setVisible(true));
        btnExam.addActionListener(e -> new ExamMgmtView().setVisible(true));
        
        // 【关键】将 userRole 继续传递给成绩管理界面
        btnScore.addActionListener(e -> new ScoreMgmtView(userRole).setVisible(true));
        
        btnSystem.addActionListener(e -> new SystemMgmtView().setVisible(true));

        // === 3. 核心权限控制逻辑 (保留) ===
        if ("USER".equals(userRole)) {
            // 如果是普通用户：禁用管理员专属功能
            // 按钮会自动变灰，这是 Swing 原生的效果
            
            btnBasic.setEnabled(false);
            btnBasic.setToolTipText("权限不足：仅管理员可用");

            btnExam.setEnabled(false);
            btnExam.setToolTipText("权限不足：仅管理员可用");

            btnSystem.setEnabled(false);
            btnSystem.setToolTipText("权限不足：仅管理员可用");

            // 成绩管理 (btnScore) 保持可用，但进入后功能受限
        }

        // === 4. 将按钮添加到窗口 ===
        add(btnBasic);
        add(btnExam);
        add(btnScore);
        add(btnSystem);
    }
}