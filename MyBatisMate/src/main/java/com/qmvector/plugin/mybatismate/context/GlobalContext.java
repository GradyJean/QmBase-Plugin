package com.qmvector.plugin.mybatismate.context;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;

/**
 * 全局上下文
 */
public class GlobalContext {
    private static Project project;
    private static GlobalSearchScope globalSearchScope;

    public static Project getProject() {
        return project;
    }

    public static void init(Project project) {
        if (project != null) {
            GlobalContext.project = project;
            GlobalContext.globalSearchScope = GlobalSearchScope.allScope(project);
        }
    }

    public static GlobalSearchScope getGlobalSearchScope() {
        return globalSearchScope;
    }
}
