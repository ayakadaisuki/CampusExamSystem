package com.exam.system;

import com.exam.view.MainDashboard;

import javax.swing.*;
import java.awt.*;

public class App {
    public static void main(String[] args) {

        // 启动登录窗口
        SwingUtilities.invokeLater(() -> new LoginDialog().setVisible(true));
    }
}

/**
 * 登录对话框
 */
class LoginDialog extends JDialog {
    public LoginDialog() {
        setTitle("系统登录");
        setSize(320, 220); 
        setLocationRelativeTo(null); 
        setLayout(new GridLayout(4, 1, 10, 10)); 
        setModal(true); 

        // 设置内容面板边距
        JPanel container = (JPanel) this.getContentPane();
        container.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // 1. 用户名
        JPanel pUser = new JPanel(new BorderLayout(10, 0));
        pUser.add(new JLabel("账  号:"), BorderLayout.WEST);
        JTextField txtUser = new JTextField();
        pUser.add(txtUser, BorderLayout.CENTER);
        add(pUser);

        // 2. 密码
        JPanel pPass = new JPanel(new BorderLayout(10, 0));
        pPass.add(new JLabel("密  码:"), BorderLayout.WEST);
        JPasswordField txtPass = new JPasswordField();
        pPass.add(txtPass, BorderLayout.CENTER);
        add(pPass);

        // 3. 按钮区域
        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0)); 
        JButton btnLogin = new JButton("立即登录");
        JButton btnCancel = new JButton("退出系统");
        
        // ---【样式统一】---
        // 1. 去除焦点框
        btnLogin.setFocusPainted(false);
        btnCancel.setFocusPainted(false);
        
        // 2. 设置颜色 (自定义颜色)
        btnLogin.setBackground(new Color(60, 120, 220)); // 深蓝色
        btnLogin.setForeground(Color.WHITE);             // 白字
        
        btnCancel.setBackground(new Color(230, 230, 230)); // 浅灰背景
        btnCancel.setForeground(Color.BLACK);

        // 3. 统一大小
        btnLogin.setPreferredSize(new Dimension(100, 30)); 
        btnCancel.setPreferredSize(new Dimension(100, 30));
        
        pBtn.add(btnLogin);
        pBtn.add(btnCancel);
        add(pBtn);

        // 4. 底部提示
        JLabel lblTip = new JLabel("提示: admin/123456 为管理员", SwingConstants.CENTER);
        lblTip.setForeground(Color.GRAY);
        lblTip.setFont(new Font("宋体", Font.PLAIN, 12));
        add(lblTip);

        // ================== 事件监听 ==================

        btnCancel.addActionListener(e -> System.exit(0));

        // 登录逻辑
        btnLogin.addActionListener(e -> {
            String uid = txtUser.getText().trim();
            String pwd = new String(txtPass.getPassword());

            if (uid.isEmpty() || pwd.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入账号和密码！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String role = "";
            // 硬编码权限判断
            if ("admin".equals(uid) && "123456".equals(pwd)) {
                role = "ADMIN";
                JOptionPane.showMessageDialog(this, "管理员身份验证成功！");
            } else {
                role = "USER";
                JOptionPane.showMessageDialog(this, "访客/学生身份登录成功！\n当前为【受限模式】。");
            }

            dispose(); 
            new MainDashboard(role).setVisible(true); // 进入主界面
        });
        
        // 手动实现回车登录 
        txtPass.addActionListener(e -> btnLogin.doClick());
        txtUser.addActionListener(e -> txtPass.requestFocusInWindow());
    }
}