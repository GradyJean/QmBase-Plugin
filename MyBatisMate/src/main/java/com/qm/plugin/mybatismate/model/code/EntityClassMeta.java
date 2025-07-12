package com.qm.plugin.mybatismate.model.code;

import com.intellij.psi.PsiField;

public class EntityClassMeta extends JavaClassMeta {
    private PsiField primaryKeyField;

    public PsiField getPrimaryKeyField() {
        return primaryKeyField;
    }

    public void setPrimaryKeyField(PsiField primaryKeyField) {
        this.primaryKeyField = primaryKeyField;
    }
}
