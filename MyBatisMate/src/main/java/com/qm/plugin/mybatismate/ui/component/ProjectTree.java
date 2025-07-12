package com.qm.plugin.mybatismate.ui.component;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.IconUtil;
import com.qm.plugin.mybatismate.context.GlobalContext;
import com.qm.plugin.mybatismate.ui.i18n.MessageBundle;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.BiConsumer;

public class ProjectTree extends JScrollPane {
    private final Project project;
    private Tree tree;
    private BiConsumer<String, String> menuActionCallback;

    public ProjectTree() {
        this.project = GlobalContext.getProject();
        treeInit();
    }

    private void treeInit() {
        VirtualFile baseDir = ProjectUtil.guessProjectDir(project);
        DefaultMutableTreeNode root = null;
        if (baseDir != null) {
            root = new DefaultMutableTreeNode(baseDir.getName());
            buildFileTree(baseDir, root);
        }

        tree = new Tree(root);
        tree.setCellRenderer(new FileTreeCellRenderer(project));
        // 添加右键菜单
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem entityItem = new JMenuItem(MessageBundle.message("component.ProjectTree.menuItem.entity"));
        JMenuItem mapperItem = new JMenuItem(MessageBundle.message("component.ProjectTree.menuItem.mapper"));
        JMenuItem xmlItem = new JMenuItem(MessageBundle.message("component.ProjectTree.menuItem.mapperXml"));
        entityItem.setActionCommand("entity");
        mapperItem.setActionCommand("mapper");
        xmlItem.setActionCommand("xml");

        // 统一添加 ActionListener
        ActionListener menuAction = e -> {
            String type = e.getActionCommand(); // "entity", "mapper", "xml"
            TreePath selectedPath = tree.getSelectionPath();
            if (selectedPath == null) return;
            Object nodeObj = selectedPath.getLastPathComponent();
            if (nodeObj instanceof DefaultMutableTreeNode node) {
                Object userObject = node.getUserObject();
                if (userObject instanceof VirtualFile vf) {
                    String dirPath = vf.getPath();
                    if (menuActionCallback != null) {
                        menuActionCallback.accept(type, dirPath);
                    }
                }
            }
        };

        entityItem.addActionListener(menuAction);
        mapperItem.addActionListener(menuAction);
        xmlItem.addActionListener(menuAction);
        popupMenu.add(entityItem);
        popupMenu.add(mapperItem);
        popupMenu.add(xmlItem);

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = tree.getClosestRowForLocation(e.getX(), e.getY());
                    tree.setSelectionRow(row);
                    Object obj = tree.getLastSelectedPathComponent();
                    if (obj instanceof DefaultMutableTreeNode node) {
                        Object userObject = node.getUserObject();
                        if (userObject instanceof VirtualFile vf && vf.isDirectory()) {
                            popupMenu.show(tree, e.getX(), e.getY());
                        }
                    }
                }
            }
        });
        setViewportView(tree);
    }

    public void selectPath(String absolutePath) {
        if (tree == null || absolutePath == null) return;

        TreePath foundPath = findPath((DefaultMutableTreeNode) tree.getModel().getRoot(), absolutePath);
        if (foundPath != null) {
            tree.expandPath(foundPath);
            tree.setSelectionPath(foundPath);
            tree.scrollPathToVisible(foundPath);
        }
    }

    private TreePath findPath(DefaultMutableTreeNode node, String targetPath) {
        Object userObject = node.getUserObject();
        if (userObject instanceof VirtualFile vf && vf.getPath().equals(targetPath)) {
            return new TreePath(node.getPath());
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
            TreePath result = findPath(child, targetPath);
            if (result != null) return result;
        }

        return null;
    }

    private void buildFileTree(VirtualFile dir, DefaultMutableTreeNode parent) {
        for (VirtualFile child : dir.getChildren()) {
            if (child.isDirectory() && child.isInLocalFileSystem()) {
                String name = child.getName();
                if (name.startsWith(".") || "target".equals(name)) {
                    continue;
                }

                DefaultMutableTreeNode node = new DefaultMutableTreeNode(child);
                parent.add(node);
                buildFileTree(child, node);
            }
        }
    }

    private static class FileTreeCellRenderer extends DefaultTreeCellRenderer {
        private final Project project;

        public FileTreeCellRenderer(Project project) {
            this.project = project;
            setBackgroundNonSelectionColor(UIManager.getColor("Tree.background"));
            setBackgroundSelectionColor(UIManager.getColor("Tree.selectionBackground"));
            setTextNonSelectionColor(UIManager.getColor("Tree.textForeground"));
            setTextSelectionColor(UIManager.getColor("Tree.selectionForeground"));
            setBorderSelectionColor(null); // ✅ 关键：去掉蓝色线框
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean selected, boolean expanded,
                                                      boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

            if (value instanceof DefaultMutableTreeNode node) {
                Object userObject = node.getUserObject();
                if (userObject instanceof VirtualFile file) {
                    Icon icon = IconUtil.getIcon(file, 0, project);
                    setIcon(icon);
                    setText(file.getName());
                }
            }

            return this;
        }
    }

    public void setMenuActionCallback(BiConsumer<String, String> menuActionCallback) {
        this.menuActionCallback = menuActionCallback;
    }
}
