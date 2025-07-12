package com.qm.plugin.mybatismate.psi;

import com.intellij.openapi.project.Project;
import com.qm.plugin.mybatismate.context.GlobalContext;

public class PsiSupportBase {
    protected final Project project;

    protected PsiSupportBase() {
        project = GlobalContext.getProject();
    }
}
