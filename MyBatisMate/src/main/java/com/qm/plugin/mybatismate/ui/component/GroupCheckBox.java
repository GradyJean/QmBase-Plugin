package com.qm.plugin.mybatismate.ui.component;

import com.qm.plugin.mybatismate.model.checkbox.CheckBoxMeta;
import com.qm.plugin.mybatismate.ui.i18n.MessageBundle;

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
        useLombokCheckBox = new JCheckBox(MessageBundle.message("component.GroupCheckBox.useLombok.title"));
        useLombokCheckBox.setToolTipText(MessageBundle.message("component.GroupCheckBox.useLombok.tip"));
        useLombokCheckBox.setSelected(false); // 默认选中
        add(useLombokCheckBox);
        add(Box.createHorizontalStrut(10)); // 添加间隔
        generateMapperBaseMethodCheckBox = new JCheckBox(MessageBundle.message("component.GroupCheckBox.generateMapperBase.title"));
        generateMapperBaseMethodCheckBox.setToolTipText(MessageBundle.message("component.GroupCheckBox.generateMapperBase.tip"));
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
