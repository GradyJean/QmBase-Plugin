package com.qm.plugin.mybatismate.codegen;

import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.qm.plugin.mybatismate.model.code.CodeGenMeta;
import com.qm.plugin.mybatismate.model.code.CodeSourceMeta;
import com.qm.plugin.mybatismate.model.code.EntityClassMeta;
import com.qm.plugin.mybatismate.model.code.JavaClassMeta;
import com.qm.plugin.mybatismate.model.table.ColumnMeta;
import com.qm.plugin.mybatismate.model.table.TableMeta;
import com.qm.plugin.mybatismate.psi.XmlPsiGenerator;
import com.qm.plugin.mybatismate.util.NameConverter;


public class MapperXMLGenerator extends XmlPsiGenerator {

    public void generate(CodeGenMeta codeGenMeta) {
        // 表元数据
        TableMeta tableMeta = codeGenMeta.getTableMeta();
        // 实体类元数据
        EntityClassMeta entityMeta = codeGenMeta.getEntityMeta();
        // mapper 元数据
        JavaClassMeta mapperMeta = codeGenMeta.getMapperMeta();
        // mapper xml 元数据
        CodeSourceMeta mapperXmlMeta = codeGenMeta.getMapperXmlMeta();
        // 是否生成 mapper 基础方法
        boolean generateMapperBaseMethod = codeGenMeta.isGenerateMapperBaseMethod();
        // 创建文件
        XmlFile xmlFile = generatorXmlFile(mapperXmlMeta.getClassName());
        // 获取document
        XmlDocument document = xmlFile.getDocument();
        if (document == null) {
            return;
        }
        // 创建 <mapper> 标签并设置 namespace
        XmlTag mapperTag = factory.createTagFromText("<mapper/>");
        mapperTag.setAttribute("namespace", mapperMeta.getImportName());

        XmlTag resultMapTag = factory.createTagFromText("<resultMap/>");
        resultMapTag.setAttribute("id", "BaseResultMap");
        resultMapTag.setAttribute("type", entityMeta.getImportName());
        String primaryKey = "";
        StringBuilder sqlText = new StringBuilder();
        for (ColumnMeta columnMeta : tableMeta.getColumns()) {
            if (columnMeta.isPrimaryKey() && primaryKey.isEmpty()) {
                primaryKey = columnMeta.getColumnName();
            }
            XmlTag resultTag = columnMeta.isPrimaryKey() ?
                    factory.createTagFromText("<id/>") :
                    factory.createTagFromText("<result/>");
            resultTag.setAttribute("property", columnMeta.getJavaName());
            resultTag.setAttribute("column", columnMeta.getColumnName());

            resultMapTag.addSubTag(resultTag, false);
            sqlText.append(columnMeta.getColumnName());
            sqlText.append(",");
        }

        if (!sqlText.isEmpty()) {
            sqlText.setLength(sqlText.length() - 1);
        }
        XmlTag sqlTextTag = factory.createTagFromText("<sql id=\"Base_Column_List\">" + sqlText + "</sql>");

        mapperTag.addSubTag(resultMapTag, false);
        mapperTag.addSubTag(sqlTextTag, false);

        if (generateMapperBaseMethod) {
            generateFindByXSql(mapperTag, tableMeta.getTableName(), primaryKey);
            generateFindAllSql(mapperTag, tableMeta.getTableName());
            generateInsertSql(mapperTag, tableMeta, entityMeta);
            generateUpdateByXSql(mapperTag, primaryKey, tableMeta);
            generateDeleteByXSql(mapperTag, tableMeta.getTableName(), primaryKey);
        }
        document.add(mapperTag);
        // 保存文件
        save(xmlFile, mapperXmlMeta.getOutputPath());
    }

    // findById
    private void generateFindByXSql(XmlTag mapperTag, String tableName, String primaryKey) {
        String methodName = NameConverter.toCamelCase("find_By_" + primaryKey, false);
        StringBuilder sqlText = new StringBuilder();
        sqlText.append("<select id=\"").append(methodName).append("\" resultMap=\"BaseResultMap\"> \n");
        sqlText.append("SELECT \n");
        sqlText.append("<include refid=\"Base_Column_List\"/> \n");
        sqlText.append("FROM ").append(tableName).append("\n");
        sqlText.append("WHERE ").append(primaryKey).append(" = #{").append(NameConverter.columnNameToFieldName(primaryKey)).append("}\n");
        sqlText.append("</select>");
        XmlTag findByXTag = factory.createTagFromText(sqlText);
        mapperTag.addSubTag(findByXTag, false);
    }

    private void generateFindAllSql(XmlTag mapperTag, String tableName) {
        StringBuilder sqlText = new StringBuilder();
        sqlText.append("<select id=\"findAll\" resultMap=\"BaseResultMap\"> \n");
        sqlText.append("SELECT \n");
        sqlText.append("<include refid=\"Base_Column_List\"/> \n");
        sqlText.append("FROM ").append(tableName).append("\n");
        sqlText.append("</select>");
        XmlTag findByXTag = factory.createTagFromText(sqlText);
        mapperTag.addSubTag(findByXTag, false);
    }

    private void generateInsertSql(XmlTag mapperTag, TableMeta tableMeta, EntityClassMeta entityMeta) {
        StringBuilder sqlText = new StringBuilder();
        sqlText.append("<insert id=\"insert\" parameterType=\"").append(entityMeta.getImportName()).append("\"> \n");
        sqlText.append("INSERT INTO ").append(tableMeta.getTableName()).append("\n");
        sqlText.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n");
        StringBuilder into = new StringBuilder();
        StringBuilder values = new StringBuilder();
        for (ColumnMeta columnMeta : tableMeta.getColumns()) {
            into.append("<if test=\"").append(columnMeta.getJavaName()).append(" != null\">").append(columnMeta.getColumnName()).append(",</if>\n");
            values.append("<if test=\"").append(columnMeta.getJavaName()).append(" != null\"> #{").append(columnMeta.getJavaName()).append("}, </if>\n");
        }
        sqlText.append(into);
        sqlText.append("</trim>\n");
        sqlText.append("<trim prefix=\"VALUES (\" suffix=\")\" suffixOverrides=\",\">\n");
        sqlText.append(values);
        sqlText.append("</trim>\n");
        sqlText.append("</insert>");
        XmlTag findByXTag = factory.createTagFromText(sqlText);
        mapperTag.addSubTag(findByXTag, false);
    }

    private void generateUpdateByXSql(XmlTag mapperTag, String primaryKey, TableMeta tableMeta) {
        String methodName = NameConverter.toCamelCase("update_By_" + primaryKey, false);
        StringBuilder sqlText = new StringBuilder();
        sqlText.append("<update id=\"").append(methodName).append("\"> \n");
        sqlText.append("UPDATE ").append(tableMeta.getTableName()).append(" \n");
        sqlText.append("<set> \n");
        for (ColumnMeta columnMeta : tableMeta.getColumns()) {
            sqlText.append("<if test=\"").append(columnMeta.getJavaName()).append(" != null\">").append(columnMeta.getColumnName()).append(" = #{").append(columnMeta.getJavaName()).append("},</if>\n");
        }
        sqlText.append("</set> \n");
        sqlText.append("WHERE ").append(primaryKey).append(" = #{").append(NameConverter.columnNameToFieldName(primaryKey)).append("}");
        sqlText.append("</update>");
        XmlTag findByXTag = factory.createTagFromText(sqlText);
        mapperTag.addSubTag(findByXTag, false);
    }

    private void generateDeleteByXSql(XmlTag mapperTag, String tableName, String primaryKey) {
        String methodName = NameConverter.toCamelCase("delete_By_" + primaryKey, false);
        StringBuilder sqlText = new StringBuilder();
        sqlText.append("<update id=\"").append(methodName).append("\"> \n");
        sqlText.append("DELETE FROM ").append(tableName).append("\n");
        sqlText.append("WHERE  ").append(primaryKey).append(" = #{").append(NameConverter.columnNameToFieldName(primaryKey)).append("}\n");
        sqlText.append("</update>");
        XmlTag findByXTag = factory.createTagFromText(sqlText);
        mapperTag.addSubTag(findByXTag, false);
    }
}