package com.github.kadeyoo.kotestgenerator.common

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@Service
@State(
    name = "KotestGeneratorSettings",
    storages = [Storage("kotest-generator-settings.xml")],
)
class KotestGeneratorSettings : PersistentStateComponent<KotestGeneratorSettings.State> {
    data class State(
        var mockLib: String = "mockk",
        var indent: Int = 4,
        var insertComment: Boolean = true
    ) {
        fun getIndentString(): String {
            return " ".repeat(this.indent)
        }

        fun getMockLibImport(): String {
            return when (this.mockLib) {
                "mockk" -> "import io.mockk.mockk"
                "Mockito" -> "import org.mockito.Mockito"
                else -> ""
            }
        }
    }

    private var state = State() // 내부에 실제 값 보관

    override fun getState(): State {
        // 플러그인이 설정값 저장할 때 호출됨 (내부 state를 리턴)
        return state
    }

    override fun loadState(state: State) {
        // 플러그인이 설정값 로딩할 때 호출됨 (내부 state에 복사)
        this.state = state
    }

    companion object {
        fun getInstance(): KotestGeneratorSettings =
            ApplicationManager.getApplication().getService(KotestGeneratorSettings::class.java)
    }
}