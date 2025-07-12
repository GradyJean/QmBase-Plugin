package com.qm.plugin.mybatismate.util;

import com.intellij.pom.java.LanguageLevel;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiType;
import com.qm.plugin.mybatismate.context.GlobalContext;

public class PsiTypeResolver {
    private static final JavaPsiFacade FACADE = JavaPsiFacade.getInstance(GlobalContext.getProject());
    private static final PsiElementFactory FACTORY = FACADE.getElementFactory();

    public static PsiType javaClassToPsiType(Class<?> clazz) {
        if (clazz.isArray()) {
            PsiType componentType = javaClassToPsiType(clazz.getComponentType());
            return componentType != null
                    ? FACTORY.getArrayClassType(componentType, LanguageLevel.HIGHEST)
                    : null;
        }

        if (clazz.isPrimitive()) {
            return PsiType.getTypeByName(clazz.getName(), GlobalContext.getProject(), GlobalContext.getGlobalSearchScope());
        }

        PsiClass psiClass = FACADE.findClass(clazz.getName(), GlobalContext.getGlobalSearchScope());
        if (psiClass == null) {
            return null;
        }
        return FACTORY.createType(psiClass);
    }
}
