package com.qmvector.plugin.mybatismate.model.code;

import com.intellij.psi.PsiClass;

public class JavaClassMeta extends CodeSourceMeta {

    // 包名
    private String packageName;
    // class 对象
    private PsiClass psiClass;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getImportName() {
        return packageName + "." + getClassName();
    }

    public PsiClass getPsiClass() {
        return psiClass;
    }

    public void setPsiClass(PsiClass psiClass) {
        this.psiClass = psiClass;
    }
}
