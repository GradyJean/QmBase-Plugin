package com.qm.plugin.mybatismate.ui.dialog;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.ui.JBUI;
import com.qm.plugin.mybatismate.context.GlobalContext;
import com.qm.plugin.mybatismate.model.checkbox.CheckBoxMeta;
import com.qm.plugin.mybatismate.model.dir.DirChooserMeta;
import com.qm.plugin.mybatismate.model.dir.DirMeta;
import com.qm.plugin.mybatismate.model.dir.DirType;
import com.qm.plugin.mybatismate.ui.component.DataTable;
import com.qm.plugin.mybatismate.ui.component.DirChooserBox;
import com.qm.plugin.mybatismate.ui.component.GroupCheckBox;
import com.qm.plugin.mybatismate.ui.component.ProjectTree;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class MainDialog extends DialogWrapper {
    private final JPanel centerPanel;
    // ------------------组件------------------
    // 数据表格
    private DataTable dataTable;
    // 实例目录选择框
    private DirChooserBox entityChooserBox;
    // Mapper 目录选择框
    private DirChooserBox mapperChooserBox;
    // Mapper XML 目录选择框
    private DirChooserBox mapperXmlChooserBox;
    // 项目树
    private ProjectTree projectTree;
    // 复选框组
    private GroupCheckBox groupCheckBox;
    // ------------------回调函数------------------
    // 确认按钮回调函数
    private Consumer<Integer> okButtonCallback;
    // ------------------布局------------------
    private final GridBagConstraints gbc;

    public MainDialog() {
        super(GlobalContext.getProject());
        // 设置对话框不允许调整大小
        setResizable(false);
        // 设置对话框标题
        setTitle("Mybatis 生成器");
        centerPanel = new JPanel();
        // 设置面板大小
        centerPanel.setPreferredSize(new Dimension(800, 600));
        // 使用 GridBagLayout 布局管理器
        centerPanel.setLayout(new GridBagLayout());
        // 初始化 GridBagConstraints
        gbc = new GridBagConstraints();
        gbc.insets = JBUI.insets(2); // 每个组件的间距
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        // 设置对话框的默认按钮
        getOKAction().putValue(Action.NAME, "开始生成");
        getCancelAction().putValue(Action.NAME, "取消");
    }

    /**
     * 初始化对话框
     */
    public void start() {
        init();
        show();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        projectTree = new ProjectTree();

        // 左侧 ProjectTree 占据左侧，第一列
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;// 占据一行
        gbc.weightx = 0.3;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        centerPanel.add(projectTree, gbc);

        // 右侧 DataTable 占据右侧，第一列
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.weightx = 0.7;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        centerPanel.add(dataTable, gbc);

        // entityChooserBox
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 0;
        gbc.gridwidth = 2;
        entityChooserBox = new DirChooserBox(new DirChooserMeta("实体类", "", "java"));
        gbc.fill = GridBagConstraints.HORIZONTAL;
        centerPanel.add(entityChooserBox, gbc);

        // mapperChooserBox
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 0;
        gbc.gridwidth = 2;
        mapperChooserBox = new DirChooserBox(new DirChooserMeta("Mapper", "Mapper", "java"));
        centerPanel.add(mapperChooserBox, gbc);

        // mapperXmlChooserBox
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weighty = 0;
        gbc.gridwidth = 2;
        mapperXmlChooserBox = new DirChooserBox(new DirChooserMeta("Mapper XML", "Mapper", "xml"));
        centerPanel.add(mapperXmlChooserBox, gbc);

        // groupCheckBox
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weighty = 0;
        gbc.gridwidth = 2;
        groupCheckBox = new GroupCheckBox();
        centerPanel.add(groupCheckBox, gbc);
        // 设置 ProjectTree 的回调函数
        projectTree.setMenuActionCallback((type, dirPath) -> {
            switch (type) {
                case "entity" -> entityChooserBox.setDirPath(dirPath);
                case "mapper" -> mapperChooserBox.setDirPath(dirPath);
                case "xml" -> mapperXmlChooserBox.setDirPath(dirPath);
            }
        });
        // 设置 DirChooserBox 的回调函数
        entityChooserBox.setDirFieldClickCallback(this::projectTreeSelectionChanged);
        mapperChooserBox.setDirFieldClickCallback(this::projectTreeSelectionChanged);
        mapperXmlChooserBox.setDirFieldClickCallback(this::projectTreeSelectionChanged);
        return centerPanel;
    }

    private void projectTreeSelectionChanged(String dirPath) {
        if (dirPath != null) {
            projectTree.selectPath(dirPath);
        }
    }

    /**
     * 设置数据表格
     *
     * @param headers        表头
     * @param data           数据
     * @param deleteCallback 删除回调函数
     */
    public void addData(String[] headers, Object[][] data, Consumer<Integer> deleteCallback) {
        this.dataTable = new DataTable(headers, data);
        this.dataTable.setDeleteCallback(deleteCallback);
    }

    public void updateTableColumn(Object value, int rowIndex, int columnIndex) {
        if (dataTable != null) {
            dataTable.updateColumn(value, rowIndex, columnIndex);
        }
    }

    /**
     * 获取目录信息列表
     *
     * @return 目录信息列表
     */
    public List<DirMeta> getDirMetas() {
        DirMeta entityDirMeta = entityChooserBox.getDirInfo();
        entityDirMeta.setDirType(DirType.ENTITY);
        DirMeta mapperDirMeta = mapperChooserBox.getDirInfo();
        mapperDirMeta.setDirType(DirType.MAPPER);
        DirMeta mapperXmlDirMeta = mapperXmlChooserBox.getDirInfo();
        mapperXmlDirMeta.setDirType(DirType.MAPPER_XML);
        return List.of(
                entityDirMeta,
                mapperDirMeta,
                mapperXmlDirMeta
        );
    }

    @Override
    protected void doOKAction() {
        if (okButtonCallback != null) {
            okButtonCallback.accept(1);
        } else {
            super.doOKAction();
        }
    }

    public void setOkButtonCallback(Consumer<Integer> okButtonCallback) {
        this.okButtonCallback = okButtonCallback;
    }

    public CheckBoxMeta getCheckBoxMeta() {
        return groupCheckBox.getCheckBoxMeta();
    }
}
