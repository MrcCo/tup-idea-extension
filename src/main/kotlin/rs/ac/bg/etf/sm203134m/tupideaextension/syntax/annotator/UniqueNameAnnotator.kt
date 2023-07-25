package rs.ac.bg.etf.sm203134m.tupideaextension.syntax.annotator

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import rs.ac.bg.etf.sm203134m.antlr4.TupParser
import rs.ac.bg.etf.sm203134m.tupideaextension.file.TupFile
import rs.ac.bg.etf.sm203134m.tupideaextension.file.TupFileType
import rs.ac.bg.etf.sm203134m.tupideaextension.syntax.PSIElementInitializer.getRuleElementType


class UniqueNameAnnotator: Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {

        if(getRuleElementType(TupParser.RULE_testName) == element.node.elementType) {

            val testName = element.text.substringAfter(":").substringBefore(".")
            val fileName = element.containingFile.name
            val project = element.project

            if(testNameIsDefinedInOtherTupFileInProject(testName, fileName, project)) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Test name $testName exists in other .tup file!")
                    .range(element.textRange)
                    .create()
            }
        }

    }

    private fun testNameIsDefinedInOtherTupFileInProject(testName: String, fileName: String, project: Project): Boolean {

        val virtualFiles = FileTypeIndex.getFiles(TupFileType.INSTANCE, GlobalSearchScope.allScope(project))

        for (virtualFile in virtualFiles) {

            val tupFile = PsiManager.getInstance(project).findFile(virtualFile) as TupFile

            // avoid current file
            if(tupFile.name != fileName) {

                val rootPsiElement = PsiTreeUtil.getChildrenOfType(
                    tupFile,
                    ASTWrapperPsiElement::class.java
                )
                val virtualFileTestName = rootPsiElement!![0].firstChild.node.text.substringAfter(":").substringBefore(".")

                if(virtualFileTestName == testName) {
                    return true;
                }
            }

        }

        return false
    }

}