package rs.ac.bg.etf.sm203134m.tupideaextension.syntax.annotator.request

import com.intellij.codeInspection.LocalQuickFixOnPsiElement
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.testFramework.MockProblemDescriptor
import rs.ac.bg.etf.sm203134m.antlr4.TupParser
import rs.ac.bg.etf.sm203134m.tupideaextension.syntax.PSIElementInitializer

class HttpMethodAnnotator : Annotator {

    private val httpMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE")

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {

        if (PSIElementInitializer.getRuleElementType(TupParser.RULE_httpMethod) == element.node.elementType) {

            if (!httpMethods.contains(element.node.text)) {
                createFixes(element, holder)
            }

        }

    }

    private fun createFixes(element: PsiElement, annotationHolder: AnnotationHolder) {

        val annotation = annotationHolder.newAnnotation(
            HighlightSeverity.ERROR,
            "HTTP method ${element.node.text} is not a valid HTTP method, try one of these: $httpMethods!"
        ).range(element.textRange);

        httpMethods.forEach {
            val fix = HttpMethodQuickFix(element, it)
            annotation
                .newLocalQuickFix(
                    fix,
                    MockProblemDescriptor(element, "Fixing invalid HTTP method", ProblemHighlightType.ERROR, fix)
                ).registerFix()
        }

        annotation.create()

    }
}

class HttpMethodQuickFix(psiElement: PsiElement, private val httpMethod: String) :
    LocalQuickFixOnPsiElement(psiElement) {


    override fun getFamilyName(): String {
        return "HTTP method fix."
    }

    override fun getText(): String {
        return httpMethod
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
            range.startOffset,
            range.endOffset,
            httpMethod
        )


    }

}