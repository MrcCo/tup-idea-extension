package rs.ac.bg.etf.sm203134m.tupideaextension

import com.intellij.lang.Language

class TupLanguage private constructor(): Language("TupLanguage") {

    companion object {
        val INSTANCE: Language = TupLanguage()
    }

}