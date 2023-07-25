package rs.ac.bg.etf.sm203134m.tupideaextension.syntax.annotator.request

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import org.apache.commons.validator.routines.UrlValidator
import rs.ac.bg.etf.sm203134m.antlr4.TupLexer
import rs.ac.bg.etf.sm203134m.antlr4.TupParser
import rs.ac.bg.etf.sm203134m.tupideaextension.syntax.PSIElementInitializer

class UrlValidationAnnotator: Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {

        if(PSIElementInitializer.getRuleElementType(TupParser.RULE_request) == element.node.elementType) {

            val url = element.node.getChildren(TokenSet.create(
                PSIElementInitializer.getTokenElementType(TupLexer.STRING)
            )).first().text.substringAfter("\"").substringBefore("\"")

            if(!UrlValidator.getInstance().isValid(url)) {
                holder.newAnnotation(HighlightSeverity.ERROR, "$url is not a valid URL.")
                    .range(element.textRange)
                    .create()
            }

        }

    }
}