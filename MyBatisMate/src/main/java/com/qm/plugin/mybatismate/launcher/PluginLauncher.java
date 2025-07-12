package com.qm.plugin.mybatismate.launcher;

import com.intellij.database.psi.DbTable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.qm.plugin.mybatismate.codegen.EntityGenerator;
import com.qm.plugin.mybatismate.codegen.MapperGenerator;
import com.qm.plugin.mybatismate.codegen.MapperXMLGenerator;
import com.qm.plugin.mybatismate.context.GlobalContext;
import com.qm.plugin.mybatismate.model.checkbox.CheckBoxMeta;
import com.qm.plugin.mybatismate.model.code.CodeGenMeta;
import com.qm.plugin.mybatismate.model.dir.DirMeta;
import com.qm.plugin.mybatismate.model.table.TableMeta;
import com.qm.plugin.mybatismate.ui.dialog.MainDialog;
import com.qm.plugin.mybatismate.ui.icon.PluginIcon;

import java.util.ArrayList;
import java.util.List;

public class PluginLauncher {
    private final Project project;
    private final List<DbTable> dbTables;
    private List<TableMeta> tableMetas;
    private MainDialog mainDialog;

    public PluginLauncher(List<DbTable> dbTables) {
        this.project = GlobalContext.getProject();
        this.dbTables = dbTables;
    }

    public void start() {
        // 在插件启动时构建表元数据
        buildTableMetas();
        // 创建主对话框并显示
        mainDialog = new MainDialog();
        // 设置表元数据
        addTableData();
        // 设置按钮回调
        mainDialog.setOkButtonCallback(this::execute);
        mainDialog.start();
    }

    /**
     * 执行插件的主要逻辑。
     * 这里可以添加生成代码、处理表数据等操作。
     *
     * @param code 插件执行的代码
     */
    private void execute(int code) {
        // 提示:生成文件将覆盖已有内容
        int result = Messages.showOkCancelDialog(
                project,
                "Tips:生成文件将覆盖已有内容，是否继续？",
                "确认操作",
                "继续生成",
                "取消",
                Messages.getQuestionIcon()
        );
        if (result != Messages.OK) {
            return;
        }

        // 主流程
        List<DirMeta> dirMetas = mainDialog.getDirMetas();
        if (!validateDirInfos(dirMetas)) {
            return; // 如果目录信息无效，直接返回
        }
        CheckBoxMeta checkBoxMeta = mainDialog.getCheckBoxMeta();
        for (int i = 0; i < tableMetas.size(); i++) {
            TableMeta tableMeta = tableMetas.get(i);
            CodeGenMeta codeGenMeta = CodeGenMeta.getInstance(tableMeta, dirMetas);
            codeGenMeta.setUseLombok(checkBoxMeta.isUseLombok());
            codeGenMeta.setGenerateMapperBaseMethod(checkBoxMeta.isGenerateMapperBaseMethod());
            try {
                // 设置代码生成元数据
                new EntityGenerator().generate(codeGenMeta);
                new MapperGenerator().generate(codeGenMeta);
                new MapperXMLGenerator().generate(codeGenMeta);
                mainDialog.updateTableColumn(PluginIcon.SUCCESS, i, 0);
            } catch (Exception e) {
                mainDialog.updateTableColumn(PluginIcon.ERROR, i, 0);
            }
        }
    }

    private boolean validateDirInfos(List<DirMeta> dirMetas) {
        // 检查目录信息是否有效
        for (DirMeta dirMeta : dirMetas) {
            String dirPath = dirMeta.getDirPath();
            String javaPattern = ".*src/main/java.*";
            String resourcePattern = ".*src/main/resources.*";
            switch (dirMeta.getDirType()) {
                case ENTITY -> {
                    if (dirPath.isEmpty()) {
                        showErrorDialog("实体类路径不能为空");
                        return false;
                    }
                    if (!dirPath.matches(javaPattern)) {
                        showErrorDialog("实体类路径必须包含 src/main/java");
                        return false;
                    }
                }
                case MAPPER -> {
                    if (dirPath.isEmpty()) {
                        showErrorDialog("Mapper路径不能为空");
                        return false;
                    }
                    if (!dirPath.matches(javaPattern)) {
                        showErrorDialog("Mapper路径必须包含 src/main/java");
                        return false;
                    }
                }
                case MAPPER_XML -> {
                    if (dirPath.isEmpty()) {
                        showErrorDialog("Mapper XML 路径不能为空");
                        return false;
                    }
                    if (!dirPath.matches(resourcePattern)) {
                        showErrorDialog("Mapper XML路径必须包含 src/main/resources");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 生成数据表格组件，展示所有表的元数据。
     * 表格包含表名、注释、列数和主键等信息。
     *
     */
    private void addTableData() {
        String[] headers = {"状态", "表名", "注释", "字段数量", "主键"};
        Object[][] data = new Object[tableMetas.size()][5];
        for (int i = 0; i < tableMetas.size(); i++) {
            TableMeta meta = tableMetas.get(i);
            data[i][0] = PluginIcon.READY;
            data[i][1] = meta.getTableName();            // 表名
            data[i][2] = meta.getComment();              // 备注
            data[i][3] = meta.getColumns().size();       // 字段数量
            data[i][4] = meta.getPrimaryKeys();          // 主键
        }
        // 设置表格的删除操作
        mainDialog.addData(headers, data, (rowIndex) -> {
            // 删除表元数据
            tableMetas.remove((int) rowIndex);
        });
    }

    private void buildTableMetas() {
        // 初始化表元数据列表
        tableMetas = new ArrayList<>();
        // 遍历所有数据库表
        for (DbTable table : dbTables) {
            // 获取表的元数据
            TableMeta tableMeta = TableMeta.getInstance(table);
            // 将表元数据添加到列表中
            tableMetas.add(tableMeta);
        }
    }

    private void showErrorDialog(String message) {
        Messages.showErrorDialog(project, message, "错误");
    }
}
