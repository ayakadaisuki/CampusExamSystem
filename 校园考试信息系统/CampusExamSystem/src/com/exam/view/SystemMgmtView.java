package com.exam.view;

import com.exam.system.DBUtil;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SystemMgmtView extends JFrame {

    public SystemMgmtView() {
        setTitle("系统权限管理");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        
        JButton btnUserRole = new JButton("用户角色分配 ()");
        JButton btnPassword = new JButton("账号密码维护()");
        JButton btnBackup = new JButton("数据库备份");

        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        

        btnBackup.addActionListener(e -> backupDatabase());

        panel.add(btnUserRole);
        panel.add(btnPassword);
        panel.add(btnBackup);

        add(panel);
    }

    private void backupDatabase() {
        // 1. 获取当前项目路径
        String projectPath = System.getProperty("user.dir");
        
        // 2. 创建备份文件夹
        File backupDir = new File(projectPath, "db_backups");
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }

        // 3. 生成带时间戳的文件名
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "CampusExam_" + timeStamp + ".bak";
        
        File backupFile = new File(backupDir, fileName);
        String absolutePath = backupFile.getAbsolutePath();

        // 4. 执行 SQL 备份命令
        // 注意：数据库名称 [CampusExamSystem] 需与您 SQL Server 中的名称一致
        String sql = "BACKUP DATABASE [CampusExamSystem] TO DISK = '" + absolutePath + "'";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // 提示用户正在处理
            JOptionPane.showMessageDialog(this, "正在备份数据库，请稍候...", "系统提示", JOptionPane.INFORMATION_MESSAGE);
            
            stmt.execute(sql);
            
            JOptionPane.showMessageDialog(this, "备份成功！\n文件已保存至：\n" + absolutePath);
            
        } catch (Exception e) {
            e.printStackTrace();
            // 权限不足的常见提示
            if (e.getMessage().contains("Access is denied") || e.getMessage().contains("拒绝访问")) {
                JOptionPane.showMessageDialog(this, 
                    "备份失败：SQL Server 权限不足。\n请尝试将项目移动到非系统盘(如 D盘)运行。", 
                    "权限错误", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "备份失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


}