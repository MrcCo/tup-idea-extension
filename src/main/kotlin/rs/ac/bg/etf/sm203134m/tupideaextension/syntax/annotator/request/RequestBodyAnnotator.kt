package rs.ac.bg.etf.sm203134m.tupideaextension.syntax.annotator.request

import com.intellij.codeInspection.LocalQuickFixOnPsiElement
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.TokenSet
import com.intellij.testFramework.MockProblemDescriptor
import rs.ac.bg.etf.sm203134m.antlr4.TupLexer
import rs.ac.bg.etf.sm203134m.antlr4.TupParser
import rs.ac.bg.etf.sm203134m.tupideaextension.syntax.PSIElementInitializer
import rs.ac.bg.etf.sm203134m.tupideaextension.syntax.annotator.base.RuleAnnotator

class RequestBodyAnnotator : RuleAnnotator() {

    private var httpMethodsWithoutRequestBody = listOf("GET", "DELETE")
    private val httpMethodsWithRequestBody = listOf("POST", "PUT", "PATCH")

    override fun getRule(): Int {
        return TupParser.RULE_executeApiRequest
    }

    override fun doAnnotate(element: PsiElement, holder: AnnotationHolder) {

        val requestMethodIfPresent = element.node.getChildren(
            TokenSet.create(
                PSIElementInitializer.getRuleElementType(TupParser.RULE_request),
            )
        ).first().getChildren(
            TokenSet.create(
                PSIElementInitializer.getRuleElementType(TupParser.RULE_httpMethod),
            )
        ).getOrNull(0)

        val requestMethod = requestMethodIfPresent?.text ?: "GET"
        val requestBodies = element.node.getChildren(
            TokenSet.create(
                PSIElementInitializer.getRuleElementType(TupParser.RULE_requestBody),
            )
        )
        val hasRequestBody = requestBodies.isNotEmpty()

        if (hasRequestBody && httpMethodsWithoutRequestBody.contains(requestMethod)) {
            val removalFix = RemoveRequestBodyQuickFix(requestBodies.first().psi)
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                "HTTP method $requestMethod does not have a request body."
            )
                .range(element.textRange)
                .newLocalQuickFix(
                    removalFix,
                    MockProblemDescriptor(
                        element, "Removing request body",
                        ProblemHighlightType.ERROR, removalFix
                    )
                ).registerFix().create()

            createReplaceFixes(holder, element, requestMethod)
        }
    }

    private fun createReplaceFixes(holder: AnnotationHolder, element: PsiElement, requestMethod: String) {
        val annotation = holder.newAnnotation(
            HighlightSeverity.ERROR,
            "HTTP method $requestMethod does not support request body, try one of these: $httpMethodsWithRequestBody!"
        ).range(element.textRange);

        httpMethodsWithRequestBody.forEach {
            val fix = ChangeRequestMethodBodyQuickFix(element, requestMethod, it)
            annotation
                .newLocalQuickFix(
                    fix,
                    MockProblemDescriptor(element, "Changing HTTP method", ProblemHighlightType.ERROR, fix)
                ).registerFix()
        }

        annotation.create()
    }
}


class RemoveRequestBodyQuickFix(requestBodyDefinition: PsiElement) :
    LocalQuickFixOnPsiElement(requestBodyDefinition) {


    override fun getFamilyName(): String {
        return "Remove request body method fix."
    }

    override fun getText(): String {
        return "Remove request body from method that does not support it."
    }

    override fun invoke(
        project: Project,
        file: PsiFile,
        startElement: PsiElement,
        endElement: PsiElement
    ) {

        val psiFile = startElement.containingFile
        val range = startElement.textRange

        psiFile.viewProvider.document.replaceString(
            range.startOffset - 1,
            range.endOffset,
            ""
        )


    }

}


class ChangeRequestMethodBodyQuickFix(executeRequest: PsiElement, val from: String, val to: String) :
    LocalQuickFixOnPsiElement(executeRequest) {


    override fun getFamilyName(): String {
        return "Remove request body method fix."
    }

    override fun getText(): String {
        return "Change request method from: $from, to: $to"
    }

    override fun invoke(
        project: Project,
        file: PsiFile,
        startElement: PsiElement,
        endElement: PsiElement
    ) {

        val requestDefinition = startElement.node.getChildren(
            TokenSet.create(
                PSIElementInitializer.getRuleElementType(TupParser.RULE_request),
            )
        ).first()

        val requestMethodIfPresent = requestDefinition.getChildren(
            TokenSet.create(
                PSIElementInitializer.getRuleElementType(TupParser.RULE_httpMethod),
            )
        ).getOrNull(0)

        val psiFile = startElement.containingFile
        if (requestMethodIfPresent == null) {
            val urlString =
                requestDefinition.getChildren(TokenSet.create(PSIElementInitializer.getTokenElementType(TupLexer.STRING)))
                    .first()
            val start = urlString.startOffset - 1
            val range = TextRange(start, start)
            psiFile.viewProvider.document.replaceString(
                range.startOffset,
                range.endOffset,
                " $to "
            )
        } else {
            val range = requestMethodIfPresent.textRange
            psiFile.viewProvider.document.replaceString(
                range.startOffset,
                range.endOffset,
                to
            )
        }


    }

}