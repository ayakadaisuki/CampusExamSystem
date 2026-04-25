package com.exam.view;

import javax.swing.*;
import java.awt.*;

public class ExamMgmtView extends JFrame {

    public ExamMgmtView() {
        setTitle("考试考务管理");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 顶部工具栏：排考操作
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAutoSchedule = new JButton("智能自动排考()");
        JButton btnPrintCard = new JButton("生成准考证()");
        JButton btnPrintSeat = new JButton("打印座次表()");
        
        // 模拟点击事件
        btnAutoSchedule.addActionListener(e -> JOptionPane.showMessageDialog(this, "正在执行智能排考算法...\n(此处需调用后端算法逻辑)"));
        
        toolBar.add(btnAutoSchedule);
        toolBar.add(new JSeparator(SwingConstants.VERTICAL));
        toolBar.add(btnPrintCard);
        toolBar.add(btnPrintSeat);

        add(toolBar, BorderLayout.NORTH);

        // 中间显示当前的考试安排表 (读取 Exam 表)
        JTextArea textArea = new JTextArea("\n  当前考试安排预览：\n\n  1. 数据库原理 - 2025/01/10 - 201教室\n  2. 高等数学 - 2025/01/11 - 101教室\n  ...");
        textArea.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        textArea.setEditable(false);
        add(new JScrollPane(textArea), BorderLayout.CENTER);
    }
}