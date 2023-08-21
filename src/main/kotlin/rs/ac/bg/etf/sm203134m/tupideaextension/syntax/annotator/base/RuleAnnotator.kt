package rs.ac.bg.etf.sm203134m.tupideaextension.syntax.annotator.base

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import rs.ac.bg.etf.sm203134m.tupideaextension.syntax.PSIElementInitializer

abstract class RuleAnnotator: Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {

        if (PSIElementInitializer.getRuleElementType(getRule()) == element.node.elementType) {

            doAnnotate(element, holder)

        }

    }

    abstract fun getRule(): Int
    abstract fun doAnnotate(element: PsiElement, holder: AnnotationHolder)
}