package rs.ac.bg.etf.sm203134m.tupideaextension.syntax

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.PsiTreeUtil
import rs.ac.bg.etf.sm203134m.antlr4.TupParser
import rs.ac.bg.etf.sm203134m.tupideaextension.file.TupFile
import rs.ac.bg.etf.sm203134m.tupideaextension.file.TupFileType
import rs.ac.bg.etf.sm203134m.tupideaextension.syntax.TupLanguageTokenType.getRuleElementType
import java.util.*


class UniqueNameAnnotator: Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {

        if(getRuleElementType(TupParser.RULE_testName) == element.node.elementType) {

            var testName = element.text.substringAfter(":").substringBefore(".")
            var fileName = element.containingFile.name
            var project = element.project

            if(checkTestInOtherFiles(testName, fileName, project)) {
                println("IDEMO")
            }
        }

    }

    fun checkTestInOtherFiles(testName: String, fileName: String, project: Project): Boolean {

        println(project)

        val virtualFiles = FileTypeIndex.getFiles(TupFileType.INSTANCE, GlobalSearchScope.allScope(project))

        println(virtualFiles)

        for (virtualFile in virtualFiles) {

            val file = PsiManager.getInstance(project).findFile(virtualFile) as TupFile
            println(file.name)
            if(file.name != fileName) {
                // avoid current file

                val properties = PsiTreeUtil.getChildrenOfType(
                    file,
                    TupFile::class.java
                )
                println(properties)
            }


        }
        return true
    }

}