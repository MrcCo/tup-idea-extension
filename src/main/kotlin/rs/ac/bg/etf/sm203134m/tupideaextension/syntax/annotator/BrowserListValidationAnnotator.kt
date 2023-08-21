package rs.ac.bg.etf.sm203134m.tupideaextension.syntax.annotator

import com.intellij.codeInspection.LocalQuickFixOnPsiElement
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.elementType
import com.intellij.testFramework.MockProblemDescriptor
import rs.ac.bg.etf.sm203134m.antlr4.TupLexer
import rs.ac.bg.etf.sm203134m.antlr4.TupParser
import rs.ac.bg.etf.sm203134m.tupideaextension.syntax.PSIElementInitializer.getTokenElementType
import rs.ac.bg.etf.sm203134m.tupideaextension.syntax.annotator.base.RuleAnnotator

class BrowserListValidationAnnotator : RuleAnnotator() {

    private val supportedBrowsers = listOf(
        "chrome", "edge", "safari", "firefox"
    )

    override fun getRule(): Int {
        return TupParser.RULE_browserList
    }

    override fun doAnnotate(element: PsiElement, holder: AnnotationHolder) {

        val definedChoices = element.node.getChildren(
            TokenSet.create(
                getTokenElementType(TupLexer.IDENTIFIER)
            )
        ).toMutableList()
        val definedChoicesStrings = definedChoices.map { it.text }.toList()
        val definedChoicesStringsSet = definedChoices.map { it.text }.toSet()

        if (definedChoicesStringsSet.size != definedChoicesStrings.size) {
            holder.newAnnotation(
                HighlightSeverity.WARNING,
                "There are duplicate browsers in the list!"
            ).range(element.textRange).create()
        }

        val suggestions = supportedBrowsers.toMutableList()
        suggestions.removeAll(definedChoicesStringsSet)

        definedChoices.forEach { astNode ->
            if (!supportedBrowsers.contains(astNode.text)) {
                val currentPsiElement = astNode.psi
                val annotation = holder.newAnnotation(
                    HighlightSeverity.ERROR,
                    "Browser ${astNode.text} is not supported, try one of these: $supportedBrowsers!"
                ).range(astNode.textRange);

                suggestions.forEach {
                    val fix = BrowserQuickFix(currentPsiElement, it)
                    annotation
                        .newLocalQuickFix(
                            fix,
                            MockProblemDescriptor(currentPsiElement, "Fixing unsupported browser", ProblemHighlightType.ERROR, fix)
                        ).registerFix()
                }

                val removeFix = RemoveBrowserQuickFix(currentPsiElement)

                annotation
                    .newLocalQuickFix(
                        removeFix,
                        MockProblemDescriptor(currentPsiElement, "Fixing unsupported browser", ProblemHighlightType.ERROR, removeFix)
                    ).registerFix()

                annotation.create()
            }
        }

    }
}

class BrowserQuickFix(psiElement: PsiElement, private val browser: String) :
    LocalQuickFixOnPsiElement(psiElement) {


    override fun getFamilyName(): String {
        return "Browser fix."
    }

    override fun getText(): String {
        return "Change selected browser to $browser"
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
            browser
        )


    }

}

class RemoveBrowserQuickFix(requestBodyDefinition: PsiElement) :
    LocalQuickFixOnPsiElement(requestBodyDefinition) {


    override fun getFamilyName(): String {
        return "Remove browser fix."
    }

    override fun getText(): String {
        return "Remove unsupported browser."
    }

    override fun invoke(
        project: Project,
        file: PsiFile,
        startElement: PsiElement,
        endElement: PsiElement
    ) {

        val range = startElement.textRange
        val hasCommaAfter = startElement.nextSibling.elementType == getTokenElementType(TupLexer.COMMA)
        file.viewProvider.document.replaceString(
            range.startOffset - 1,
            range.endOffset + if(hasCommaAfter) 1 else 0,
            ""
        )


    }

}