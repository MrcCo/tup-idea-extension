package rs.ac.bg.etf.sm203134m.tupideaextension.syntax.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import rs.ac.bg.etf.sm203134m.antlr4.TupParser
import rs.ac.bg.etf.sm203134m.tupideaextension.syntax.PSIElementInitializer
import rs.ac.bg.etf.sm203134m.tupideaextension.syntax.annotator.base.RuleAnnotator

class AssertionsBeforeRequestAnnotator : RuleAnnotator() {

    override fun getRule(): Int {
        return TupParser.RULE_testSteps
    }


    override fun doAnnotate(element: PsiElement, holder: AnnotationHolder) {


        var requestMade = false
        for (it in element.node.getChildren(TokenSet.create(PSIElementInitializer.getRuleElementType(TupParser.RULE_step)))) {

            val isRequestAssertion = it.getChildren(
                TokenSet.create(
                    PSIElementInitializer.getRuleElementType(TupParser.RULE_assertResponseCode),
                    PSIElementInitializer.getRuleElementType(TupParser.RULE_assertResponseBody),
                    PSIElementInitializer.getRuleElementType(TupParser.RULE_assertResponseBodyContainsField)
                )
            ).isNotEmpty()


            if (isRequestAssertion && !requestMade) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Assertion done without a request!")
                    .range(it.textRange)
                    .create()
            }

            val isRequest = it.getChildren(
                TokenSet.create(
                    PSIElementInitializer.getRuleElementType(TupParser.RULE_executeApiRequest),
                )
            ).isNotEmpty()

            if (isRequest) {
                requestMade = true
            }

        }


    }

}