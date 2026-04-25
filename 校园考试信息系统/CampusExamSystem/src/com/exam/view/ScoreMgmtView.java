package com.exam.view;

import com.exam.system.DBUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

public class ScoreMgmtView extends JFrame {

    // 定义组件
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;

    // 将按钮定义为成员变量，以便在权限控制中访问
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnSearch;
    private JButton btnRefresh;

    /**
     * 构造函数
     * @param userRole 用户身份 ("ADMIN" 或 "USER")
     */
    public ScoreMgmtView(String userRole) {
        setTitle("成绩数据管理 (录入/查询/分析)");
        setSize(900, 600);
        setLocationRelativeTo(null);
        // 子模块关闭模式：仅销毁当前窗口
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 1. 顶部操作栏 
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // 搜索区域
        topPanel.add(new JLabel("输入姓名/学号/班级:")); 
        txtSearch = new JTextField(15);
        txtSearch.setToolTipText("例如：输入 '张博文' 或 'B24-3'"); 
        topPanel.add(txtSearch);
        
        // 初始化按钮
        btnSearch = new JButton("查询");
        btnRefresh = new JButton("显示全部");
        
        // 分隔符
        topPanel.add(btnSearch);
        topPanel.add(btnRefresh);
        topPanel.add(new JSeparator(SwingConstants.VERTICAL));
        
        // CRUD 操作按钮
        btnAdd = new JButton("录入成绩");
        btnEdit = new JButton("修改成绩");
        btnDelete = new JButton("删除记录");
        
        // ---【UI优化】---
        btnSearch.setFocusPainted(false);
        btnRefresh.setFocusPainted(false);
        btnAdd.setFocusPainted(false);
        btnEdit.setFocusPainted(false);
        btnDelete.setFocusPainted(false);

        // 设置按钮背景色以便区分
        btnAdd.setBackground(new Color(220, 255, 220)); // 浅绿
        btnEdit.setBackground(new Color(255, 240, 200)); // 浅黄
        btnDelete.setBackground(new Color(255, 220, 220)); // 浅红

        topPanel.add(btnAdd);
        topPanel.add(btnEdit);
        topPanel.add(btnDelete);

        add(topPanel, BorderLayout.NORTH);

        //  2. 中间表格区域 
        // 表头
        String[] columnNames = {"学号", "姓名", "班级", "课程号", "课程名称", "成绩"};
        
        // 禁止直接双击编辑表格
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // 3. 底部状态栏 
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblStatus = new JLabel(" 提示：支持输入班级名称（如 B24-3）查看全班成绩...");
        bottomPanel.add(lblStatus);
        add(bottomPanel, BorderLayout.SOUTH);

        // 4. 事件监听器绑定 

        // A. 查询按钮
        btnSearch.addActionListener(e -> loadExamData(txtSearch.getText().trim()));

        // B. 刷新/显示全部
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadExamData("");
        });

        // C. 录入成绩 (Add)
        btnAdd.addActionListener(e -> showAddDialog());

        // D. 修改成绩 (Update)
        btnEdit.addActionListener(e -> showEditDialog());

        // E. 删除记录 (Delete)
        btnDelete.addActionListener(e -> deleteRecord());

        //  5. 【核心权限控制】 
        if ("USER".equals(userRole)) {
            // 如果是普通用户：禁用增删改
            btnAdd.setEnabled(false);
            btnAdd.setToolTipText("权限不足：仅管理员可操作");
            
            btnEdit.setEnabled(false);
            btnEdit.setToolTipText("权限不足：仅管理员可操作");
            
            btnDelete.setEnabled(false);
            btnDelete.setToolTipText("权限不足：仅管理员可操作");
            
            // 底部增加红色提示
            JLabel lblLimit = new JLabel("  [当前模式：访客/学生 - 仅供查询]");
            lblLimit.setForeground(Color.RED);
            lblLimit.setFont(new Font("宋体", Font.BOLD, 12));
            bottomPanel.add(lblLimit);
        }

        // 初始化加载所有数据
        loadExamData("");
    }

    /**
     * 核心方法：加载数据 (支持按 姓名/学号/班级 模糊搜索)
     * @param keyword 搜索关键词，为空则查全部
     */
    private void loadExamData(String keyword) {
        tableModel.setRowCount(0); // 清空表格
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            if (conn == null) return;

            StringBuilder sql = new StringBuilder();
            sql.append("SELECT s.StudentID, s.Name, s.ClassName, c.CourseID, c.CourseName, sc.ScoreValue ");
            sql.append("FROM Score sc ");
            sql.append("JOIN Student s ON sc.StudentID = s.StudentID ");
            sql.append("JOIN Course c ON sc.CourseID = c.CourseID ");

            // 多条件模糊查询
            if (!keyword.isEmpty()) {
                sql.append("WHERE s.Name LIKE ? OR s.StudentID LIKE ? OR s.ClassName LIKE ? ");
            }
            sql.append("ORDER BY s.ClassName ASC, s.StudentID ASC"); // 先按班级，再按学号排序

            ps = conn.prepareStatement(sql.toString());
            
            if (!keyword.isEmpty()) {
                String k = "%" + keyword + "%";
                ps.setString(1, k);
                ps.setString(2, k);
                ps.setString(3, k);
            }

            rs = ps.executeQuery();

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("StudentID"));
                row.add(rs.getString("Name"));
                row.add(rs.getString("ClassName"));
                row.add(rs.getString("CourseID"));
                row.add(rs.getString("CourseName"));
                row.add(rs.getDouble("ScoreValue"));
                tableModel.addRow(row);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "查询失败：" + e.getMessage());
        } finally {
            DBUtil.close(conn, ps, rs);
        }
    }

    /**
     * 功能：删除选中的记录
     */
    private void deleteRecord() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选中一行数据！");
            return;
        }

        String stuID = (String) table.getValueAt(selectedRow, 0);
        String courseID = (String) table.getValueAt(selectedRow, 3);
        String stuName = (String) table.getValueAt(selectedRow, 1);
        String courseName = (String) table.getValueAt(selectedRow, 4);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "确定要删除学生 [" + stuName + "] 的 [" + courseName + "] 成绩吗？", "确认删除", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            Connection conn = null;
            PreparedStatement ps = null;
            try {
                conn = DBUtil.getConnection();
                String sql = "DELETE FROM Score WHERE StudentID = ? AND CourseID = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, stuID);
                ps.setString(2, courseID);
                
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "删除成功！");
                    loadExamData(""); // 刷新表格
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "删除失败：" + e.getMessage());
            } finally {
                DBUtil.close(conn, ps, null);
            }
        }
    }

    /**
     * 功能：显示“录入成绩”弹窗 (带课程选择器)
     */
    private void showAddDialog() {
        JDialog dialog = new JDialog(this, "录入新成绩", true);
        dialog.setSize(350, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(5, 2, 10, 10));

        JTextField txtStuID = new JTextField();
        
        // 课程选择控件
        JTextField txtCourseID = new JTextField();
        txtCourseID.setEditable(false); 
        txtCourseID.setBackground(new Color(240, 240, 240)); 
        
        JButton btnSelectCourse = new JButton("选择...");
        btnSelectCourse.setFocusPainted(false); // 去掉蓝框

        JPanel pnlCourseSelect = new JPanel(new BorderLayout());
        pnlCourseSelect.add(txtCourseID, BorderLayout.CENTER);
        pnlCourseSelect.add(btnSelectCourse, BorderLayout.EAST);

        JTextField txtScore = new JTextField();

        dialog.add(new JLabel("  学号 (StudentID):"));
        dialog.add(txtStuID);
        dialog.add(new JLabel("  课程 (Course):"));
        dialog.add(pnlCourseSelect);
        dialog.add(new JLabel("  分数 (0-100):"));
        dialog.add(txtScore);

        JButton btnSave = new JButton("保存");
        btnSave.setFocusPainted(false);
        dialog.add(new JLabel("")); 
        dialog.add(btnSave);
        
        JLabel lblTip = new JLabel("  提示: 点击'选择'查找课程", SwingConstants.CENTER);
        lblTip.setForeground(Color.GRAY);
        dialog.add(lblTip);

        // 绑定“选择”按钮
        btnSelectCourse.addActionListener(e -> {
            String selectedID = showCourseSelectorDialog(dialog); 
            if (selectedID != null && !selectedID.isEmpty()) {
                txtCourseID.setText(selectedID);
            }
        });

        // 绑定保存按钮
        btnSave.addActionListener(e -> {
            if(txtCourseID.getText().trim().isEmpty()){
                JOptionPane.showMessageDialog(dialog, "请先选择一门课程！");
                return;
            }
            Connection conn = null;
            PreparedStatement ps = null;
            try {
                conn = DBUtil.getConnection();
                String sql = "INSERT INTO Score (StudentID, CourseID, ScoreValue) VALUES (?, ?, ?)";
                ps = conn.prepareStatement(sql);
                ps.setString(1, txtStuID.getText());
                ps.setString(2, txtCourseID.getText());
                ps.setDouble(3, Double.parseDouble(txtScore.getText()));

                ps.executeUpdate();
                JOptionPane.showMessageDialog(dialog, "录入成功！");
                dialog.dispose();
                loadExamData(""); 
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "录入失败：\n" + ex.getMessage());
            } finally {
                DBUtil.close(conn, ps, null);
            }
        });
        dialog.setVisible(true);
    }

    /**
     * 辅助功能：课程选择器对话框
     */
    private String showCourseSelectorDialog(JDialog owner) {
        JDialog selectDialog = new JDialog(owner, "选择课程", true);
        selectDialog.setSize(500, 400);
        selectDialog.setLocationRelativeTo(owner);
        selectDialog.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtKeyword = new JTextField(15);
        JButton btnFind = new JButton("搜索");
        btnFind.setFocusPainted(false);
        topPanel.add(new JLabel("课程名称关键字:"));
        topPanel.add(txtKeyword);
        topPanel.add(btnFind);
        selectDialog.add(topPanel, BorderLayout.NORTH);

        String[] headers = {"课程号", "课程名称", "学分"};
        DefaultTableModel model = new DefaultTableModel(headers, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable listTable = new JTable(model);
        selectDialog.add(new JScrollPane(listTable), BorderLayout.CENTER);

        JButton btnConfirm = new JButton("确认选择");
        btnConfirm.setFocusPainted(false);
        selectDialog.add(btnConfirm, BorderLayout.SOUTH);

        Runnable loadData = () -> {
            model.setRowCount(0);
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                conn = DBUtil.getConnection();
                String sql = "SELECT CourseID, CourseName, Credits FROM Course WHERE CourseName LIKE ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + txtKeyword.getText().trim() + "%");
                rs = ps.executeQuery();
                while(rs.next()) {
                    model.addRow(new Object[]{rs.getString("CourseID"), rs.getString("CourseName"), rs.getInt("Credits")});
                }
            } catch (Exception e) { e.printStackTrace(); } finally { DBUtil.close(conn, ps, rs); }
        };

        btnFind.addActionListener(e -> loadData.run());
        final String[] result = {null};
        btnConfirm.addActionListener(e -> {
            int row = listTable.getSelectedRow();
            if (row != -1) {
                result[0] = (String) listTable.getValueAt(row, 0);
                selectDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(selectDialog, "请先在表格中选中一行！");
            }
        });

        loadData.run();
        selectDialog.setVisible(true);
        return result[0];
    }

    /**
     * 功能：显示“修改成绩”弹窗
     */
    private void showEditDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选中一行数据！");
            return;
        }

        String stuID = (String) table.getValueAt(selectedRow, 0);
        String name = (String) table.getValueAt(selectedRow, 1);
        String courseID = (String) table.getValueAt(selectedRow, 3);
        String courseName = (String) table.getValueAt(selectedRow, 4);
        Double oldScore = (Double) table.getValueAt(selectedRow, 5);

        JDialog dialog = new JDialog(this, "修改成绩", true);
        dialog.setSize(300, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(5, 2, 10, 10));

        dialog.add(new JLabel("  学生姓名:"));
        dialog.add(new JLabel(name)); 
        dialog.add(new JLabel("  课程名称:"));
        dialog.add(new JLabel(courseName)); 
        dialog.add(new JLabel("  当前分数:"));
        JTextField txtScore = new JTextField(String.valueOf(oldScore));
        dialog.add(txtScore);

        JButton btnUpdate = new JButton("确认修改");
        btnUpdate.setFocusPainted(false);
        dialog.add(new JLabel(""));
        dialog.add(btnUpdate);

        btnUpdate.addActionListener(e -> {
            Connection conn = null;
            PreparedStatement ps = null;
            try {
                conn = DBUtil.getConnection();
                String sql = "UPDATE Score SET ScoreValue = ? WHERE StudentID = ? AND CourseID = ?";
                ps = conn.prepareStatement(sql);
                ps.setDouble(1, Double.parseDouble(txtScore.getText()));
                ps.setString(2, stuID);
                ps.setString(3, courseID);

                ps.executeUpdate();
                JOptionPane.showMessageDialog(dialog, "修改成功！");
                dialog.dispose();
                loadExamData(""); 
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "修改失败：" + ex.getMessage());
            } finally {
                DBUtil.close(conn, ps, null);
            }
        });

        dialog.setVisible(true);
    }
}