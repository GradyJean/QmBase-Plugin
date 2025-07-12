package com.qmvector.plugin.mybatismate.ui.component;

import com.qmvector.plugin.mybatismate.model.checkbox.CheckBoxMeta;

import javax.swing.*;

public class GroupCheckBox extends JPanel {
    private JCheckBox useLombokCheckBox;
    private JCheckBox generateMapperBaseMethodCheckBox;

    public GroupCheckBox() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        init();
    }

    private void init() {
        useLombokCheckBox = new JCheckBox("使用 Lombok");
        useLombokCheckBox.setToolTipText("启用 Lombok 注解以简化代码编写");
        useLombokCheckBox.setSelected(false); // 默认选中
        add(useLombokCheckBox);
        add(Box.createHorizontalStrut(10)); // 添加间隔
        generateMapperBaseMethodCheckBox = new JCheckBox("生成 Mapper 基础方法");
        generateMapperBaseMethodCheckBox.setToolTipText("生成 Mapper CRUD基础方法");
        generateMapperBaseMethodCheckBox.setSelected(true); // 默认选中
        add(generateMapperBaseMethodCheckBox);
    }

    public CheckBoxMeta getCheckBoxMeta() {
        return new CheckBoxMeta(
                useLombokCheckBox.isSelected(),
                generateMapperBaseMethodCheckBox.isSelected()
        );
    }
}
