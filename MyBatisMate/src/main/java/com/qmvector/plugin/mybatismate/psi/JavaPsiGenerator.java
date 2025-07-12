package com.qmvector.plugin.mybatismate.psi;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;

import java.util.Objects;

public class JavaPsiGenerator extends PsiFileManager {
    protected final JavaPsiFacade facade;
    protected final PsiElementFactory factory;

    protected JavaPsiGenerator() {
        facade = JavaPsiFacade.getInstance(project);
        factory = facade.getElementFactory();
    }

    protected PsiJavaFile generatorJavaFile(String className) {
        return generatorPsiFile(JavaFileType.INSTANCE, className, "");
    }

    private PsiImportStatement generatorImportStatement(String importName) {
        String importText = "import " + importName + ";";
        PsiJavaFile dummyFile = (PsiJavaFile) PsiFileFactory.getInstance(project)
                .createFileFromText("Dummy.java", JavaFileType.INSTANCE, importText);
        return Objects.requireNonNull(dummyFile.getImportList()).getImportStatements()[0];
    }

    // 添加注解
    protected void addAnnotation(PsiJavaFile javaFile, PsiClass psiClass, String annotationName) {
        addImport(javaFile, annotationName);
        String annotationText = annotationName.substring(annotationName.lastIndexOf('.') + 1);
        Objects.requireNonNull(psiClass.getModifierList()).addAnnotation(annotationText);
    }

    // 导包
    protected void addImport(PsiJavaFile javaFile, String importName) {
        PsiImportStatement entityImportStatement = generatorImportStatement(importName);
        Objects.requireNonNull(javaFile.getImportList()).add(entityImportStatement);
    }

    // 添加类注释
    protected void addClassComment(PsiClass psiClass, String comment) {
        addComment(psiClass, comment);
    }

    // 添加字段注释
    protected void addFieldComment(PsiField psiField, String comment) {
        addComment(psiField, comment);
    }

    protected void addMethodComment(PsiMethod psiMethod, String comment) {
        addComment(psiMethod, comment);
    }

    // 添加注释
    private void addComment(PsiElement psiElement, String comment) {
        if (psiElement == null) {
            return;
        }
        if (comment == null || comment.isEmpty()) {
            return;
        }
        PsiDocComment classComment = factory.createDocCommentFromText("/**" + comment + "*/");
        psiElement.addBefore(classComment, psiElement.getFirstChild());
    }
}
