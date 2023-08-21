package rs.ac.bg.etf.sm203134m.tupideaextension.syntax.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import rs.ac.bg.etf.sm203134m.antlr4.TupLexer
import rs.ac.bg.etf.sm203134m.antlr4.TupParser
import rs.ac.bg.etf.sm203134m.tupideaextension.syntax.PSIElementInitializer
import rs.ac.bg.etf.sm203134m.tupideaextension.syntax.annotator.base.RuleAnnotator

class StatusCodeAnnotator : RuleAnnotator() {

    private val validStatusCodes = listOf(
        (100..103) +
                (200..208) + 228 +
                (300..308) +
                (400..418) + (421..426) + 428 + 429 + 431 + 451 +
                (500..508) + 510 + 511
    ).flatten()

    override fun getRule(): Int {
        return TupParser.RULE_assertResponseCode
    }

    override fun doAnnotate(element: PsiElement, holder: AnnotationHolder) {

        val statusCode =
            element.node.getChildren(TokenSet.create(PSIElementInitializer.getTokenElementType(TupLexer.INTEGER)))
                .first()
        val statusCodeInteger = statusCode.text.toInt()

        if (!validStatusCodes.contains(statusCodeInteger)) {
            holder.newAnnotation(HighlightSeverity.WARNING, "$statusCodeInteger is not a valid HTTP status code.")
                .range(statusCode.textRange)
                .create()
        }


    }
}