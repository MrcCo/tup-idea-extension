package rs.ac.bg.etf.sm203134m.tupideaextension.file

import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.LanguageFileType
import rs.ac.bg.etf.sm203134m.tupideaextension.TupLanguage
import javax.swing.Icon

class TupFileType: LanguageFileType(TupLanguage.INSTANCE) {

    override fun getName(): String {
        return "Tup File"
    }

    override fun getDescription(): String {
        return "Simple language for automated web application testing"
    }

    override fun getDefaultExtension(): String {
        return "tup"
    }

    override fun getIcon(): Icon {
        return TupFileIcon.INSTANCE
    }

    companion object {
        val INSTANCE: FileType = TupFileType()
    }


}