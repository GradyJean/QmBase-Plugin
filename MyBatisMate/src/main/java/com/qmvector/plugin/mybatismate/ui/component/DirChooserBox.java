package com.qmvector.plugin.mybatismate.ui.component;

import com.qmvector.plugin.mybatismate.model.dir.DirChooserMeta;
import com.qmvector.plugin.mybatismate.model.dir.DirMeta;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class DirChooserBox extends JPanel {
    // 组件的最小、首选和最大尺寸
    private final Dimension LABEL_SIZE = new Dimension(100, 36);
    private final Dimension SUFFIX_FIELD_SIZE = new Dimension(200, 36);
    private final Dimension PATH_FIELD_SIZE = new Dimension(400, 36);
    private final DirChooserMeta dirChooserMeta;
    private final GridBagConstraints gbc;
    private final DirMeta dirMeta;
    private Consumer<String> dirFieldClickCallback;
    private JTextField suffixField;
    private JTextField dirPathField;

    public DirChooserBox(DirChooserMeta dirChooserMeta) {
        super();
        this.dirChooserMeta = dirChooserMeta;
        this.dirMeta = new DirMeta();
        setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2); // 每个组件的间距
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        init();
    }

    private void init() {
        // 设置配置名称
        gbc.gridx = 0;
        JLabel titleLabel = generateLabel(dirChooserMeta.getTitle());
        add(titleLabel, gbc);

        // 后缀输入框
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        suffixField = generateTextField("统一名称后缀,例如: User" + dirChooserMeta.getDefaultSuffix() + "." + dirChooserMeta.getFileSuffix(), SUFFIX_FIELD_SIZE);
        add(suffixField, gbc);

        // 路径输入框
        gbc.gridx = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        dirPathField = generateTextField("请选择目录", PATH_FIELD_SIZE);
        dirPathField.setEditable(true);
        dirPathField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (dirFieldClickCallback != null) {
                    dirFieldClickCallback.accept(dirPathField.getText());
                }
            }
        });

        add(dirPathField, gbc);
    }

    private JLabel generateLabel(String text) {
        JLabel label = new JLabel(text + ":");
        label.setMinimumSize(LABEL_SIZE);
        label.setPreferredSize(LABEL_SIZE);
        label.setMaximumSize(LABEL_SIZE);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        return label;
    }

    private JTextField generateTextField(String placeholder, Dimension size) {
        JTextField textField = new JTextField();
        // 存储默认颜色（系统主题下的前景色）
        Color defaultColor = UIManager.getColor("TextField.foreground");

        textField.setMaximumSize(size);
        textField.setMinimumSize(size);
        textField.setPreferredSize(size);
        textField.setText(placeholder);
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);
        textField.setToolTipText(textField.getText());
        textField.setForeground(Color.GRAY);
        // 初始文字为灰色
        // 焦点监听器
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(defaultColor); // 用户输入时用黑色
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY);
                }
            }
        });
        return textField;
    }

    public void setDirPath(String dirPath) {
        if (dirPath == null || dirPath.isEmpty()) {
            dirPathField.setText("请选择目录");
            dirPathField.setForeground(Color.GRAY);
        } else {
            dirPathField.setText(dirPath);
            dirPathField.setToolTipText(dirPath);
            dirPathField.setForeground(UIManager.getColor("TextField.foreground")); // 恢复默认颜色
        }
    }

    public DirMeta getDirInfo() {
        // Color.GRAY equals(dirPathField.getForeground()) 判断是否为默认提示文字
        String dirPath = Color.GRAY.equals(dirPathField.getForeground()) ? "" : dirPathField.getText().trim();
        String suffix = Color.GRAY.equals(suffixField.getForeground()) ? dirChooserMeta.getDefaultSuffix() : suffixField.getText().trim();
        dirMeta.setSuffix(suffix);
        dirMeta.setDirPath(dirPath);
        return dirMeta;
    }

    public void setDirFieldClickCallback(Consumer<String> callback) {
        this.dirFieldClickCallback = callback;
    }
}
