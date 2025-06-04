package com.github.kadeyoo.kotestgenerator.common

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.ComboBox
import java.awt.GridLayout
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSpinner
import javax.swing.SpinnerNumberModel

class KotestGeneratorSettingsConfigurable : Configurable {
    // UI 컴포넌트 필드
    private var mockLibCombo: JComboBox<String>? = null
    private var indentSpinner: JSpinner? = null
    private var insertCommentCheckbox: JCheckBox? = null
    private var testStyleCombo: JComboBox<String>? = null

    override fun createComponent(): JComponent {
        // UI 영역 정의
        val panel = JPanel(GridLayout(0, 2)) // 2열 레이아웃

        // Mock 라이브러리 선택
        mockLibCombo = ComboBox(arrayOf("mockk", "Fake"))
        panel.add(JLabel("Mock 라이브러리 선택:"))
        panel.add(mockLibCombo)

        // 인덴트 범위 설정
        indentSpinner = JSpinner(SpinnerNumberModel(4, 2, 8, 1)) // 기본값 4, 범위 2~8
        panel.add(JLabel("인덴트(공백 수):"))
        panel.add(indentSpinner)

        // 자동 주석 삽입 설정
        insertCommentCheckbox = JCheckBox("자동 주석 삽입", true) // 기본값: true
        panel.add(insertCommentCheckbox)
        panel.add(JLabel("")) // 레이아웃 맞추기

        // 테스트 스타일 선택
        testStyleCombo = ComboBox(arrayOf("Describe", "Should", "FunSpec", "BehaviorSpec"))
        panel.add(JLabel("테스트 스타일:"))
        panel.add(testStyleCombo)

        return panel
    }

    override fun isModified(): Boolean {
        val state = KotestGeneratorSettings.getInstance().state
        return mockLibCombo?.selectedItem != state.mockLib ||
                (indentSpinner?.value as Int) != state.indent ||
                insertCommentCheckbox?.isSelected != state.insertComment ||
                testStyleCombo?.selectedItem != state.testStyle
    }

    override fun apply() {
        val state = KotestGeneratorSettings.State(
            mockLib = mockLibCombo?.selectedItem as String,
            indent = indentSpinner?.value as Int,
            insertComment = insertCommentCheckbox?.isSelected ?: true,
            testStyle = testStyleCombo?.selectedItem as String
        )
        KotestGeneratorSettings.getInstance().loadState(state) // 상태 저장
    }

    override fun reset() {
        val state = KotestGeneratorSettings.getInstance().state
        mockLibCombo?.selectedItem = state.mockLib
        indentSpinner?.value = state.indent
        insertCommentCheckbox?.isSelected = state.insertComment
        testStyleCombo?.selectedItem = state.testStyle
    }

    override fun getDisplayName(): String = "Kotest Generator Settings"

}