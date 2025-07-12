package com.qm.plugin.mybatismate.codegen;

import com.intellij.psi.*;
import com.qm.plugin.mybatismate.context.GlobalContext;
import com.qm.plugin.mybatismate.model.code.CodeGenMeta;
import com.qm.plugin.mybatismate.model.code.EntityClassMeta;
import com.qm.plugin.mybatismate.model.code.JavaClassMeta;
import com.qm.plugin.mybatismate.model.table.TableMeta;
import com.qm.plugin.mybatismate.psi.JavaPsiGenerator;
import com.qm.plugin.mybatismate.util.NameConverter;
import com.qm.plugin.mybatismate.util.PsiTypeResolver;

import java.util.List;
import java.util.Objects;

public class MapperGenerator extends JavaPsiGenerator {

    public void generate(CodeGenMeta codeGenMeta) {
        // 表元数据
        TableMeta tableMeta = codeGenMeta.getTableMeta();
        // Mapper 接口元数据
        JavaClassMeta mapperMeta = codeGenMeta.getMapperMeta();
        // 实体类元数据
        EntityClassMeta entityMeta = codeGenMeta.getEntityMeta();
        // 是否自动生成 Mapper 基础方法
        boolean generateMapperBaseMethod = codeGenMeta.isGenerateMapperBaseMethod();
        // 创建 java 文件
        PsiJavaFile javaFile = generatorJavaFile(mapperMeta.getClassName());
        // 设置包名
        javaFile.setPackageName(mapperMeta.getPackageName());
        // 创建 class
        PsiClass psiClass = factory.createInterface(mapperMeta.getClassName());
        // 设置类注释
        addClassComment(psiClass, tableMeta.getComment() + mapperMeta.getClassName());
        // 设置 mybatis Mapper 注解
        addAnnotation(javaFile, psiClass, "org.apache.ibatis.annotations.Mapper");
        // 添加基础方法
        if (generateMapperBaseMethod) {
            PsiField primaryKeyField = entityMeta.getPrimaryKeyField();
            // 如果没有主键列，则不生成基础方法
            if (primaryKeyField == null) {
                return;
            }
            // 获取 entityClass 如果没有就不生成
            PsiClass entityClass = entityMeta.getPsiClass();
            if (entityClass == null) {
                return;
            }
            // 导入 entity 的包
            addImport(javaFile, entityMeta.getImportName());
            PsiType entityPsiType = factory.createType(entityClass);
            // findById
            generateFindByXMethod(psiClass, entityPsiType, primaryKeyField);
            // findAll
            generateFindAllMethod(psiClass, entityPsiType);
            // insert
            generateInsertMethod(psiClass, entityPsiType);
            // updateById
            generateUpdateByXMethod(psiClass, entityPsiType, primaryKeyField);
            // deleteById
            generateDeleteByXMethod(psiClass, primaryKeyField);
        }
        mapperMeta.setPsiClass(psiClass);
        javaFile.add(psiClass);
        save(javaFile, mapperMeta.getOutputPath());
    }

    // findById 方法
    private void generateFindByXMethod(PsiClass psiClass, PsiType entityPsiType, PsiField primaryKeyField) {
        String primaryKey = primaryKeyField.getName();
        String methodName = NameConverter.toCamelCase("find_By_" + primaryKey, false);
        // 创建方法
        PsiMethod method = factory.createMethod(methodName, entityPsiType);
        Objects.requireNonNull(method.getBody()).delete();
        // 添加注释
        addMethodComment(method, "根据主键查询实体");
        // 创建参数
        PsiParameter primaryKeyParameter = factory.createParameter(primaryKey, primaryKeyField.getType());
        // 添加参数
        method.getParameterList().add(primaryKeyParameter);
        psiClass.add(method);

    }

    // findAll 方法
    private void generateFindAllMethod(PsiClass psiClass, PsiType entityPsiType) {

        PsiClass listClass = facade.findClass(List.class.getName(), GlobalContext.getGlobalSearchScope());
        if (listClass == null) {
            return;
        }
        PsiType listEntityType = factory.createType(listClass, entityPsiType);
        // 创建方法
        PsiMethod method = factory.createMethod("findAll", listEntityType);
        Objects.requireNonNull(method.getBody()).delete();
        // 添加注释
        addMethodComment(method, "查询所有实体记录");
        psiClass.add(method);
    }

    // insert 方法
    private void generateInsertMethod(PsiClass psiClass, PsiType entityPsiType) {
        // 添加基础方法
        // 创建方法
        PsiMethod method = factory.createMethod("insert", PsiTypeResolver.javaClassToPsiType(int.class));
        Objects.requireNonNull(method.getBody()).delete();
        addMethodComment(method, "插入数据");
        // 创建参数
        PsiParameter primaryKeyParameter = factory.createParameter("entity", entityPsiType);
        // 添加参数
        method.getParameterList().add(primaryKeyParameter);
        psiClass.add(method);
    }

    // updateById
    private void generateUpdateByXMethod(PsiClass psiClass, PsiType entityPsiType, PsiField primaryKeyField) {
        String primaryKey = primaryKeyField.getName();
        String methodName = NameConverter.toCamelCase("update_By_" + primaryKey, false);
        // 创建方法
        PsiMethod method = factory.createMethod(methodName, PsiTypeResolver.javaClassToPsiType(int.class));
        Objects.requireNonNull(method.getBody()).delete();
        // 添加注释
        addMethodComment(method, "根据主键更新记录");
        // 创建参数
        PsiParameter primaryKeyParameter = factory.createParameter("entity", entityPsiType);
        // 添加参数
        method.getParameterList().add(primaryKeyParameter);
        psiClass.add(method);
    }

    // deleteById
    private void generateDeleteByXMethod(PsiClass psiClass, PsiField primaryKeyField) {
        String primaryKey = primaryKeyField.getName();
        String methodName = NameConverter.toCamelCase("delete_By_" + primaryKey, false);
        // 创建方法
        PsiMethod method = factory.createMethod(methodName, PsiTypeResolver.javaClassToPsiType(int.class));
        Objects.requireNonNull(method.getBody()).delete();
        // 添加注释
        addMethodComment(method, "根据主键删除记录");
        // 创建参数
        PsiParameter primaryKeyParameter = factory.createParameter(primaryKey, primaryKeyField.getType());
        // 添加参数
        method.getParameterList().add(primaryKeyParameter);
        psiClass.add(method);
    }
}
