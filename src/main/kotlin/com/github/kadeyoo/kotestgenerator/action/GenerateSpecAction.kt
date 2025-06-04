package com.github.kadeyoo.kotestgenerator.action

import com.github.kadeyoo.kotestgenerator.dispatcher.SpecGeneratorDispatcher
import com.github.kadeyoo.kotestgenerator.service.ClassService
import com.github.kadeyoo.kotestgenerator.service.MethodService
import com.github.kadeyoo.kotestgenerator.util.CodeGeneratorUtil
import com.intellij.codeInsight.actions.OptimizeImportsProcessor
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.JavaDirectoryService
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiManager
import com.intellij.psi.codeStyle.JavaCodeStyleManager
import com.intellij.psi.util.PsiTreeUtil

/**
 * IntelliJ Plugin 액션 클래스
 * Ctrl + Alt + G 단축키로 실행 시 현재 커서 위치의 Kotlin PSI 기반(클래스/메소드 등)으로 Kotest Spec 파일을 생성 또는 갱신
 */
class GenerateSpecAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: throw IllegalArgumentException("Project is null")
        val file = e.getData(CommonDataKeys.PSI_FILE) ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return

        // src/test/kotlin의 실제 패키지 경로 찾기
        val fileDirectory = file.containingDirectory
        val testRoot = findTestRoot(fileDirectory)

        val psiManager = PsiManager.getInstance(project)
        val dispatcher = SpecGeneratorDispatcher(listOf(ClassService(), MethodService()))

        // 커서 위치에서 가장 가까운 의미있는 PSI 노드 찾기 (클래스, 메소드 등)
        val psiElement = findNearestPsiElement(file, editor.caretModel.offset)

        // 현재 파일의 패키지 경로
        val finalTestDir = findOrCreateTestDirectory(
            psiManager, fileDirectory, testRoot
        )

        // 동일한 이름의 테스트 파일이 이미 있는지 확인
        val findFile = finalTestDir.findFile(file.name)
        val importNames = CodeGeneratorUtil.extractImportNamesFromFile(file)
        val content = psiElement?.let { dispatcher.generateSpec(it, importNames, findFile != null) } ?: ""

        ApplicationManager.getApplication().invokeAndWait {
            WriteCommandAction.runWriteCommandAction(project) {
                val createdFile: PsiFile
                if (findFile != null) {
                    // 기존 파일이 있으면 파일 끝 '}' 앞에 자동 생성 코드 추가
                    createdFile = appendContentToFile(project, findFile, content)
                } else {
                    // 파일이 없으면 새로 생성
                    createdFile = createNewFile(project, file, content, finalTestDir)
                }

                // 2. 파일 열기
                this.openFileInEditor(project, createdFile)

                // 3. 자동 임포트/최적화
                this.optimizeImports(project, createdFile)
            }
        }
    }

    /** src/test/kotlin 디렉토리 루트 찾기 */
    private fun findTestRoot(fileDirectory: PsiDirectory) =
        generateSequence(fileDirectory.virtualFile) { it.parent }
            .firstOrNull { it.name == "src" }
            ?.findChild("test")
            ?.findChild("kotlin")

    /** 커서에서 가장 가까운 PSI Element 찾기 */
    private fun findNearestPsiElement(file: PsiFile, offset: Int): PsiElement? =
        PsiTreeUtil.getParentOfType(file.findElementAt(offset), PsiElement::class.java)

    /** test 패키지 디렉토리 생성/반환 */
    private fun findOrCreateTestDirectory(
        psiManager: PsiManager,
        fileDirectory: PsiDirectory,
        testRoot: VirtualFile?
    ): PsiDirectory {
        val filePackage = psiManager.findDirectory(fileDirectory.virtualFile)
            ?.let { JavaDirectoryService.getInstance().getPackage(it)?.qualifiedName }
        val testPackagePath = filePackage?.replace(".", "/")
        return testRoot?.let { root ->
            val virtualTarget = VfsUtil.createDirectories("${root.path}/$testPackagePath")
            psiManager.findDirectory(virtualTarget)
        } ?: fileDirectory
    }

    /** 기존 파일에 코드 추가 */
    private fun appendContentToFile(project: Project, findFile: PsiFile, content: String): PsiFile {
        val document = FileDocumentManager.getInstance().getDocument(findFile.virtualFile)
        if (document != null) {
            val insertOffset = document.text.lastIndexOf("}")
            val codeToInsert = if (insertOffset > 0) "\n$content\n" else content
            if (insertOffset > 0) {
                document.insertString(insertOffset, codeToInsert)
                PsiDocumentManager.getInstance(project).commitDocument(document)
            }
        }

        return findFile
    }

    private fun createNewFile(project: Project, file: PsiFile, content: String, finalTestDir: PsiDirectory): PsiFile {
        val newFile = PsiFileFactory.getInstance(project).createFileFromText(file.name, file.fileType, content)
        return finalTestDir.add(newFile) as PsiFile
    }

    private fun openFileInEditor(project: Project, file: PsiFile) {
        FileEditorManager.getInstance(project).openFile(file.virtualFile, true)
    }

    private fun optimizeImports(project: Project, psiFile: PsiFile) {
        OptimizeImportsProcessor(project, psiFile).run()
    }
}