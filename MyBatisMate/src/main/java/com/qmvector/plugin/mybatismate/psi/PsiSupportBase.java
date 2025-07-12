package com.qmvector.plugin.mybatismate.psi;

import com.intellij.openapi.project.Project;
import com.qmvector.plugin.mybatismate.context.GlobalContext;

public class PsiSupportBase {
    protected final Project project;

    protected PsiSupportBase() {
        project = GlobalContext.getProject();
    }
}
