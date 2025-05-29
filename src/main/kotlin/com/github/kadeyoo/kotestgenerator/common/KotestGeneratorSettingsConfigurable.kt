package com.github.kadeyoo.kotestgenerator.common

import com.intellij.openapi.options.Configurable
import java.awt.GridLayout
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSpinner
import javax.swing.SpinnerNumberModel

class KotestGeneratorSettingsConfigurable : Configurable {
    private var mockLibCombo: JComboBox<String>? = null
    private var indentSpinner: JSpinner? = null
    private var insertCommentCheckbox: JCheckBox? = null

    override fun createComponent(): JComponent? {
        val panel = JPanel(GridLayout(0, 2))
        mockLibCombo = JComboBox(arrayOf("mockk", "Mockito"))
        indentSpinner = JSpinner(SpinnerNumberModel(4, 2, 8, 1))
        insertCommentCheckbox = JCheckBox("자동 주석 삽입", true)
        panel.add(JLabel("Mock 라이브러리 선택:"))
        panel.add(mockLibCombo)
        panel.add(JLabel("인덴트(공백 수):"))
        panel.add(indentSpinner)
        panel.add(insertCommentCheckbox)
        return panel
    }
    override fun isModified(): Boolean { /* ... */ return true }
    override fun apply() {
        val state = KotestGeneratorSettings.State(
            mockLib = mockLibCombo?.selectedItem as String,
            indent = indentSpinner?.value as Int,
            insertComment = insertCommentCheckbox?.isSelected ?: true
        )
        KotestGeneratorSettings.getInstance().loadState(state)
    }
    override fun getDisplayName(): String = "Kotest Generator Settings"
}