package rs.ac.bg.etf.sm203134m.tupideaextension.syntax.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.elementType
import rs.ac.bg.etf.sm203134m.antlr4.TupLexer
import rs.ac.bg.etf.sm203134m.antlr4.TupParser
import rs.ac.bg.etf.sm203134m.tupideaextension.syntax.PSIElementInitializer

class AssertionsBeforeRequestAnnotator: Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {

        if (PSIElementInitializer.getRuleElementType(TupParser.RULE_testSteps) == element.node.elementType) {

            var requestMade = false
            for (it in element.node.getChildren(TokenSet.create(PSIElementInitializer.getRuleElementType(TupParser.RULE_step)))) {

                if(it.text.startsWith("assert", true) && !requestMade) {
                    holder.newAnnotation(HighlightSeverity.ERROR, "Assertion done without a request!")
                        .range(it.textRange)
                        .create()
                }


                it.getPsi()

                if(it.text.startsWith("execute", true)) {
                    requestMade = true
                }

            }

        }


    }
}