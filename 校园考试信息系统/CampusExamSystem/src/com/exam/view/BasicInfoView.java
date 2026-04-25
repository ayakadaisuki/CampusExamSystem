package com.exam.view;

import com.exam.system.DBUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

public class BasicInfoView extends JFrame {

    public BasicInfoView() {
        setTitle("基础信息管理 - 师生与资源维护");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        // 学生信息管理面板 
        tabbedPane.addTab(" 学生信息维护 ", createStudentPanel());

        // 教师信息管理面板
        tabbedPane.addTab(" 教师信息维护 ", createTeacherPanel());

        // 课程信息管理面板
        tabbedPane.addTab(" 课程信息维护 ", createCoursePanel());

        // 考场信息管理面板
        tabbedPane.addTab(" 考场状态设置 ", createClassroomPanel());

        add(tabbedPane);
    }

    //  通用面板构建器
    
    /**
     * 创建【学生】管理面板
     */
    private JPanel createStudentPanel() {
        String[] cols = {"学号", "姓名", "班级", "联系电话"};
        String querySql = "SELECT StudentID, Name, ClassName, PhoneNumber FROM Student";
        
        CRUDBasePanel panel = new CRUDBasePanel(cols, querySql);

        // 1. 录入
        panel.setAddAction(e -> {
            showFormDialog("录入学生", new String[]{"学号", "姓名", "班级", "电话"}, null, inputs -> {
                String sql = "INSERT INTO Student (StudentID, Name, ClassName, PhoneNumber) VALUES (?, ?, ?, ?)";
                DBUtil.executeUpdate(sql, inputs[0], inputs[1], inputs[2], inputs[3]);
            }, panel::refreshData);
        });

        // 2. 修改 (新增功能)
        panel.setEditAction(e -> {
            String[] oldValues = panel.getSelectedRowData();
            if (oldValues == null) return; // 没选中行

            showFormDialog("修改学生信息", new String[]{"学号", "姓名", "班级", "电话"}, oldValues, inputs -> {
                // SQL: 更新其他字段，条件是主键(学号)
                String sql = "UPDATE Student SET Name=?, ClassName=?, PhoneNumber=? WHERE StudentID=?";
                // inputs[0]是学号(主键), inputs[1]姓名, inputs[2]班级, inputs[3]电话
                DBUtil.executeUpdate(sql, inputs[1], inputs[2], inputs[3], inputs[0]);
            }, panel::refreshData);
        });

        // 3. 删除
        panel.setDeleteAction(e -> {
            String id = panel.getSelectedId();
            if (id != null && confirmDelete()) {
                try {
                    DBUtil.executeUpdate("DELETE FROM Student WHERE StudentID = ?", id);
                    panel.refreshData();
                    JOptionPane.showMessageDialog(this, "删除成功！");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "删除失败，可能存在关联数据：\n" + ex.getMessage());
                }
            }
        });

        return panel;
    }

    /**
     * 创建【教师】管理面板
     */
    private JPanel createTeacherPanel() {
        String[] cols = {"教师号", "姓名", "职称"};
        String querySql = "SELECT TeacherID, Name, Title FROM Teacher";
        CRUDBasePanel panel = new CRUDBasePanel(cols, querySql);

        // 录入
        panel.setAddAction(e -> {
            showFormDialog("录入教师", new String[]{"教师号", "姓名", "职称"}, null, inputs -> {
                String sql = "INSERT INTO Teacher (TeacherID, Name, Title) VALUES (?, ?, ?)";
                DBUtil.executeUpdate(sql, inputs[0], inputs[1], inputs[2]);
            }, panel::refreshData);
        });

        // 修改 (新增功能)
        panel.setEditAction(e -> {
            String[] oldValues = panel.getSelectedRowData();
            if (oldValues == null) return;

            showFormDialog("修改教师信息", new String[]{"教师号", "姓名", "职称"}, oldValues, inputs -> {
                String sql = "UPDATE Teacher SET Name=?, Title=? WHERE TeacherID=?";
                DBUtil.executeUpdate(sql, inputs[1], inputs[2], inputs[0]);
            }, panel::refreshData);
        });

        // 删除
        panel.setDeleteAction(e -> {
            String id = panel.getSelectedId();
            if (id != null && confirmDelete()) {
                try {
                    DBUtil.executeUpdate("DELETE FROM Teacher WHERE TeacherID = ?", id);
                    panel.refreshData();
                    JOptionPane.showMessageDialog(this, "删除成功！");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "删除失败，可能该教师有授课记录：\n" + ex.getMessage());
                }
            }
        });
        return panel;
    }

    /**
     * 创建【课程】管理面板 
     */
    private JPanel createCoursePanel() {
        String[] cols = {"课程号", "课程名称", "学分", "任课教师号"};
        String querySql = "SELECT CourseID, CourseName, Credits, TeacherID FROM Course";
        CRUDBasePanel panel = new CRUDBasePanel(cols, querySql);

        panel.setAddAction(e -> {
            showFormDialog("录入课程", new String[]{"课程号", "课程名", "学分", "教师号"}, null, inputs -> {
                String sql = "INSERT INTO Course (CourseID, CourseName, Credits, TeacherID) VALUES (?, ?, ?, ?)";
                DBUtil.executeUpdate(sql, inputs[0], inputs[1], inputs[2], inputs[3]);
            }, panel::refreshData);
        });

        panel.setEditAction(e -> {
            String[] oldValues = panel.getSelectedRowData();
            if (oldValues == null) return;

            showFormDialog("修改课程信息", new String[]{"课程号", "课程名", "学分", "教师号"}, oldValues, inputs -> {
                String sql = "UPDATE Course SET CourseName=?, Credits=?, TeacherID=? WHERE CourseID=?";
                DBUtil.executeUpdate(sql, inputs[1], inputs[2], inputs[3], inputs[0]);
            }, panel::refreshData);
        });

        panel.setDeleteAction(e -> {
            String id = panel.getSelectedId();
            if (id != null && confirmDelete()) {
                try {
                    DBUtil.executeUpdate("DELETE FROM Course WHERE CourseID = ?", id);
                    panel.refreshData();
                    JOptionPane.showMessageDialog(this, "删除成功！");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "删除失败：" + ex.getMessage());
                }
            }
        });
        return panel;
    }

    /**
     * 创建【考场】管理面板
     */
    private JPanel createClassroomPanel() {
        String[] cols = {"教室号", "地点", "座位数"};
        String querySql = "SELECT RoomID, Location, Seats FROM Classroom";
        CRUDBasePanel panel = new CRUDBasePanel(cols, querySql);

        panel.setAddAction(e -> {
            showFormDialog("录入考场", new String[]{"教室号", "地点", "座位数"}, null, inputs -> {
                String sql = "INSERT INTO Classroom (RoomID, Location, Seats) VALUES (?, ?, ?)";
                DBUtil.executeUpdate(sql, inputs[0], inputs[1], inputs[2]);
            }, panel::refreshData);
        });

        panel.setEditAction(e -> {
            String[] oldValues = panel.getSelectedRowData();
            if (oldValues == null) return;

            showFormDialog("修改考场信息", new String[]{"教室号", "地点", "座位数"}, oldValues, inputs -> {
                String sql = "UPDATE Classroom SET Location=?, Seats=? WHERE RoomID=?";
                DBUtil.executeUpdate(sql, inputs[1], inputs[2], inputs[0]);
            }, panel::refreshData);
        });

        panel.setDeleteAction(e -> {
            String id = panel.getSelectedId();
            if (id != null && confirmDelete()) {
                try {
                    DBUtil.executeUpdate("DELETE FROM Classroom WHERE RoomID = ?", id);
                    panel.refreshData();
                    JOptionPane.showMessageDialog(this, "删除成功！");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "删除失败：" + ex.getMessage());
                }
            }
        });
        return panel;
    }

    //  辅助工具方法

    private boolean confirmDelete() {
        return JOptionPane.showConfirmDialog(this, "确定要删除选中的记录吗？(无法恢复)", "警告", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    /**
     * 通用的表单弹窗生成器 (升级，支持数据回填)
     * @param initialValues 如果不为null，则表示是【修改模式】，会填充输入框并锁定第一列
     */
    private void showFormDialog(String title, String[] labels, String[] initialValues, DataSaver saver, Runnable onSuccess) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setSize(300, labels.length * 50 + 80);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(labels.length + 1, 2, 10, 10));

        JTextField[] fields = new JTextField[labels.length];
        for (int i = 0; i < labels.length; i++) {
            dialog.add(new JLabel("  " + labels[i] + ":"));
            fields[i] = new JTextField();
            
            // 如果有初始值，进行回填 (修改模式)
            if (initialValues != null && i < initialValues.length) {
                fields[i].setText(initialValues[i]);
            }
            
            // 如果是修改模式，且是第一列(通常是主键ID)，则禁止修改，防止破坏关联
            if (initialValues != null && i == 0) {
                fields[i].setEditable(false);
                fields[i].setBackground(new Color(235, 235, 235)); // 灰色背景提示只读
                fields[i].setToolTipText("主键 ID 不可修改");
            }
            
            dialog.add(fields[i]);
        }

        JButton btnSave = new JButton("保存");
        dialog.add(new JLabel("")); 
        dialog.add(btnSave);

        btnSave.addActionListener(e -> {
            try {
                String[] values = new String[labels.length];
                for (int i = 0; i < labels.length; i++) values[i] = fields[i].getText();
                saver.save(values); // 执行保存回调
                JOptionPane.showMessageDialog(dialog, "保存成功！");
                dialog.dispose();
                onSuccess.run(); // 刷新回调
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "保存失败：" + ex.getMessage());
            }
        });
        dialog.setVisible(true);
    }

    interface DataSaver {
        void save(String[] inputs) throws Exception;
    }

    /**
     * 内部类：通用的CRUD基础面板 (升级版：增加了 Edit 按钮和数据获取方法)
     */
    class CRUDBasePanel extends JPanel {
        private JTable table;
        private DefaultTableModel model;
        private String querySql;
        private JButton btnAdd, btnEdit, btnDelete; // 新增 btnEdit

        public CRUDBasePanel(String[] columns, String sql) {
            this.querySql = sql;
            setLayout(new BorderLayout());

            // 顶部工具栏
            JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton btnRefresh = new JButton("刷新");
            btnAdd = new JButton("录入");
            btnEdit = new JButton("修改"); // 新增按钮
            btnDelete = new JButton("删除");
            
            // 样式优化
            btnAdd.setBackground(new Color(220, 255, 220));   // 绿
            btnEdit.setBackground(new Color(255, 240, 200));  // 黄
            btnDelete.setBackground(new Color(255, 220, 220));// 红

            toolBar.add(btnRefresh);
            toolBar.add(btnAdd);
            toolBar.add(btnEdit); // 添加到界面
            toolBar.add(btnDelete);
            add(toolBar, BorderLayout.NORTH);

            // 表格区域
            model = new DefaultTableModel(columns, 0) {
                public boolean isCellEditable(int row, int col) { return false; }
            };
            table = new JTable(model);
            table.setRowHeight(25);
            add(new JScrollPane(table), BorderLayout.CENTER);

            // 绑定刷新事件
            btnRefresh.addActionListener(e -> refreshData());
            
            // 初始加载
            refreshData();
        }

        public void setAddAction(ActionListener l) { btnAdd.addActionListener(l); }
        public void setEditAction(ActionListener l) { btnEdit.addActionListener(l); } // 新增接口
        public void setDeleteAction(ActionListener l) { btnDelete.addActionListener(l); }

        public String getSelectedId() {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "请先选中一行！");
                return null;
            }
            return table.getValueAt(row, 0).toString();
        }

        // 新增方法：获取选中行所有列的数据，用于回填修改框
        public String[] getSelectedRowData() {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "请先选中一行要修改的数据！");
                return null;
            }
            int colCount = table.getColumnCount();
            String[] data = new String[colCount];
            for (int i = 0; i < colCount; i++) {
                Object val = table.getValueAt(row, i);
                data[i] = val == null ? "" : val.toString();
            }
            return data;
        }

        public void refreshData() {
            model.setRowCount(0);
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                conn = DBUtil.getConnection();
                ps = conn.prepareStatement(querySql);
                rs = ps.executeQuery();
                int colCount = model.getColumnCount();
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    for (int i = 1; i <= colCount; i++) row.add(rs.getObject(i));
                    model.addRow(row);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                DBUtil.close(conn, ps, rs);
            }
        }
    }
}