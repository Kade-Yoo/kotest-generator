package com.github.kadeyoo.kotestgenerator.action

import com.github.kadeyoo.kotestgenerator.dispatcher.SpecGeneratorDispatcher
import com.github.kadeyoo.kotestgenerator.service.ClassService
import com.github.kadeyoo.kotestgenerator.service.MethodService
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.psi.JavaDirectoryService
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil

/**
 * IntelliJ Plugin 액션 클래스
 * Ctrl + Alt + G 단축키로 실행 시 현재 커서 위치의 Kotlin 클래스명을 기반으로 Kotest Spec 파일을 생성함
 */
class GenerateSpecAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: throw IllegalArgumentException("Project is null")
        val file = e.getData(CommonDataKeys.PSI_FILE) ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val fileDirectory = file.containingDirectory
        val testRoot = generateSequence(fileDirectory.virtualFile) { it.parent }
            .firstOrNull() { it.name == "src" }
            ?.findChild("test")
            ?.findChild("kotlin")
        val psiManager = PsiManager.getInstance(project)
        val dispatcher = SpecGeneratorDispatcher(listOf(ClassService(), MethodService()))
        val findElementAt = file.findElementAt(editor.caretModel.offset)
        val psiElement: PsiElement? = PsiTreeUtil.getParentOfType(findElementAt, PsiElement::class.java)
        val content = psiElement?.let { dispatcher.generateSpec(it) } ?: ""
        val filePackage = psiManager.findDirectory(fileDirectory.virtualFile)
            ?.let { JavaDirectoryService.getInstance().getPackage(it)?.qualifiedName }
        val testPackagePath = filePackage?.replace(".", "/")
        val finalTestDir = testRoot?.let { root ->
            val virtualTarget = VfsUtil.createDirectories("${root.path}/$testPackagePath")
            psiManager.findDirectory(virtualTarget)
        } ?: fileDirectory

        WriteCommandAction.runWriteCommandAction(project) {
            val existingFile = finalTestDir.findFile(file.name)
            if (existingFile != null) return@runWriteCommandAction

            val newFile = PsiFileFactory.getInstance(project).createFileFromText(file.name, file.fileType, content)
            finalTestDir.add(newFile)
        }
    }
}