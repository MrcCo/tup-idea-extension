package rs.ac.bg.etf.sm203134m.tupideaextension.file

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import rs.ac.bg.etf.sm203134m.tupideaextension.TupLanguage

class TupFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, TupLanguage.INSTANCE) {
    override fun getFileType(): FileType {
        return TupFileType.INSTANCE
    }

}