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
        var mockLib: String = "mockk", // Mock 라이브러리의 기본값
        var indent: Int = 4, // 들여쓰기 기본값
        var insertComment: Boolean = true, // 주석 기본값
        var testStyle: String = "Describe" // Kotest 스타일 기본값
    )
    {
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

    // 내부에 실제 값 보관
    private var state = State()

    // 플러그인이 설정값 저장할 때 호출됨 (내부 state를 리턴)
    override fun getState(): State {
        return state
    }

    // 플러그인이 설정값 로딩할 때 호출됨 (내부 state에 복사)
    override fun loadState(state: State) {
        this.state = state
    }

    companion object {
        fun getInstance(): KotestGeneratorSettings =
            ApplicationManager.getApplication().getService(KotestGeneratorSettings::class.java)
    }
}