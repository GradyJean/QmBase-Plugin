package com.qm.plugin.mybatismate.psi;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.codeStyle.CodeStyleManager;

import java.io.IOException;

public class PsiFileManager extends PsiSupportBase implements Runnable {
    private PsiFile psiFile;
    private PsiDirectory directory;

    @Override
    public void run() {
        // 先删除后更新
        PsiFile file = directory.findFile(psiFile.getName());
        if (file != null) {
            file.delete();
        }
        // 格式化代码
        CodeStyleManager.getInstance(project).reformat(psiFile, false);
        directory.add(psiFile);
    }

    protected void save(PsiFile psiFile, String outputPath) {
        this.psiFile = psiFile;
        this.directory = getDirectory(outputPath);
        WriteCommandAction.runWriteCommandAction(project, this);
    }

    private PsiDirectory getDirectory(String outputPath) {
        try {
            VirtualFile vFile = VfsUtil.createDirectoryIfMissing(outputPath);
            if (vFile != null) {
                return PsiManager.getInstance(project).findDirectory(vFile);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    protected <F extends PsiFile> F generatorPsiFile(FileType fileType, String fileName, String content) {
        content = content == null ? "" : content;
        fileName = fileName + "." + fileType.getDefaultExtension();
        PsiFile psiFile = PsiFileFactory.getInstance(project)
                .createFileFromText(fileName,
                        fileType,
                        content);
        return (F) psiFile;
    }
}
