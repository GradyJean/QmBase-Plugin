package com.qmvector.plugin.mybatismate.action;

import com.intellij.database.Dbms;
import com.intellij.database.model.basic.BasicModTable;
import com.intellij.database.psi.DbTable;
import com.intellij.database.view.DataSourceNode;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.qmvector.plugin.mybatismate.context.GlobalContext;
import com.qmvector.plugin.mybatismate.launcher.PluginLauncher;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LauncherMenuAction extends AnAction {
    private final static Set<Dbms> DBMS_SET = new HashSet<>();

    public LauncherMenuAction() {
        super("MyBatis 生成器");
        DBMS_SET.add(Dbms.MYSQL);
        DBMS_SET.add(Dbms.ORACLE);
        DBMS_SET.add(Dbms.POSTGRES);
        DBMS_SET.add(Dbms.SQLITE);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        // 默认将按钮设置为不可见和不可用
        e.getPresentation().setEnabledAndVisible(false);

        // 获取当前上下文中被激活的 UI 组件（右键菜单在哪个组件上弹出）
        Component component = e.getData(PlatformDataKeys.CONTEXT_COMPONENT);

        // 判断是否为数据库面板中的 JTree 组件（即 Database View 中的树）
        if (component instanceof JTree tree) {
            // 获取用户当前选中的树节点路径
            TreePath selectedPath = tree.getSelectionPath();
            if (selectedPath == null) {
                // 如果没有选中任何节点，直接返回
                return;
            }

            // 节点路径长度必须至少为2：
            // path[0] 是根节点（如数据源根），path[1] 是 DataSourceNode
            if (selectedPath.getPathCount() < 2) {
                // 节点太浅，不是我们关心的目标结构
                return;
            }

            // 获取第一级（一般为数据源节点）
            Object dataSourceNode = selectedPath.getPathComponent(1);
            // 类型转换的目的：确认该节点为 DataSourceNode，用于后续数据库类型判断
            if (!(dataSourceNode instanceof DataSourceNode dataSource)) {
                // 如果不是数据源节点，说明结构不符合预期，返回
                return;
            }

            // 判断该数据源类型是否支持生成（如只支持 MySQL、Oracle 等）
            if (!DBMS_SET.contains(dataSource.getDbms())) {
                // 不支持的数据库类型，返回
                return;
            }

            // 获取用户真正选中的最后一个节点，通常是表节点
            Object tableNode = selectedPath.getLastPathComponent();

            // 仅当该节点是 BasicModTable 类型（表示是数据库表）时，才允许按钮可见
            // 依据：只有选中数据库表节点时，才允许生成 MyBatis 文件
            e.getPresentation().setEnabledAndVisible(tableNode instanceof BasicModTable);
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Object[] items = e.getData(PlatformDataKeys.PSI_ELEMENT_ARRAY);
        if (items == null) {
            // 如果没有选中任何项，直接返回
            return;
        }
        List<DbTable> dbTables = new ArrayList<>();
        for (Object item : items) {
            if (!(item instanceof DbTable table)) {
                continue;
            }
            dbTables.add(table);
        }
        if (dbTables.isEmpty()) {
            // 如果没有选中任何数据库表，直接返回
            return;
        }
        // 上下文初始化
        GlobalContext.init(e.getProject());
        // 执行
        new PluginLauncher(dbTables).start();
    }

}
