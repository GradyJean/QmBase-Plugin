package com.qm.plugin.mybatismate.psi;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.psi.XmlElementFactory;
import com.intellij.psi.xml.XmlFile;

public class XmlPsiGenerator extends PsiFileManager {
    protected final XmlElementFactory factory;

    public XmlPsiGenerator() {
        factory = XmlElementFactory.getInstance(project);
    }

    protected XmlFile generatorXmlFile(String fileName) {
        String xmlContent = """
                <?xml version="1.0" encoding="UTF-8" ?>
                <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
                        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
                """;
        return generatorPsiFile(XmlFileType.INSTANCE, fileName, xmlContent);
    }
}
