package com.qmvector.plugin.mybatismate.codegen;

import com.intellij.codeInsight.generation.GenerateMembersUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.qmvector.plugin.mybatismate.model.code.CodeGenMeta;
import com.qmvector.plugin.mybatismate.model.code.EntityClassMeta;
import com.qmvector.plugin.mybatismate.model.table.ColumnMeta;
import com.qmvector.plugin.mybatismate.model.table.TableMeta;
import com.qmvector.plugin.mybatismate.psi.JavaPsiGenerator;

import java.util.List;

public class EntityGenerator extends JavaPsiGenerator {
    public void generate(CodeGenMeta codeGenMeta) {
        // 表元数据
        TableMeta tableMeta = codeGenMeta.getTableMeta();
        // 实体类元数据
        EntityClassMeta entityMeta = codeGenMeta.getEntityMeta();
        // 是否使用 Lombok
        boolean isUseLombok = codeGenMeta.isUseLombok();
        // 创建 java 文件
        PsiJavaFile javaFile = generatorJavaFile(entityMeta.getClassName());
        // 设置包名
        javaFile.setPackageName(entityMeta.getPackageName());
        // 创建 class
        PsiClass psiClass = factory.createClass(entityMeta.getClassName());
        // 添加类注释
        addClassComment(psiClass, tableMeta.getComment());
        // 遍历字段信息 添加字段
        List<ColumnMeta> columns = tableMeta.getColumns();

        for (ColumnMeta column : columns) {
            PsiField psiField = factory.createField(column.getJavaName(), column.getPsiType());
            // 字段添加注释
            addFieldComment(psiField, column.getComment());
            // 设置主键的 Field
            if (column.isPrimaryKey() && entityMeta.getPrimaryKeyField() == null) {
                entityMeta.setPrimaryKeyField(psiField);
            }
            psiClass.add(psiField);
        }

        // 如果项目使用了 Lombok
        if (isUseLombok) {
            addAnnotation(javaFile, psiClass, "lombok.Data");
        } else {
            for (PsiField field : psiClass.getFields()) {
                PsiMethod setterMethod = GenerateMembersUtil.generateSetterPrototype(field);
                PsiMethod getterMethod = GenerateMembersUtil.generateGetterPrototype(field);
                psiClass.add(setterMethod);
                psiClass.add(getterMethod);
            }
        }
        entityMeta.setPsiClass(psiClass);
        javaFile.add(psiClass);
        save(javaFile, entityMeta.getOutputPath());
    }
}

