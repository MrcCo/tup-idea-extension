package rs.ac.bg.etf.sm203134m.tupideaextension.syntax.annotator

import com.intellij.codeInspection.LocalQuickFixOnPsiElement
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.TokenSet
import com.intellij.testFramework.MockProblemDescriptor
import rs.ac.bg.etf.sm203134m.antlr4.TupParser
import rs.ac.bg.etf.sm203134m.tupideaextension.syntax.PSIElementInitializer
import rs.ac.bg.etf.sm203134m.tupideaextension.syntax.annotator.base.RuleAnnotator

class BrowserDefinitionAnnotator : RuleAnnotator() {
    override fun getRule(): Int {
        return TupParser.RULE_browserDefinition
    }

    override fun doAnnotate(element: PsiElement, holder: AnnotationHolder) {

        val steps = element.parent.parent.node.getChildren(
            TokenSet.create(
                PSIElementInitializer.getRuleElementType(TupParser.RULE_testSteps)
            )
        )


        val noBrowserUsageInStepsList = steps.first()
            .getChildren(
                TokenSet.create(PSIElementInitializer.getRuleElementType(TupParser.RULE_step))
            ).map {
                it.getChildren(
                    TokenSet.create(
                        PSIElementInitializer.getRuleElementType(TupParser.RULE_openWebPage)
                    )
                )
            }.filter { it.isNotEmpty() }
        noBrowserUsageInStepsList.forEach { it.forEach { it1-> println(it1.text) } }

        println(noBrowserUsageInStepsList.size)
        noBrowserUsageInStepsList.forEach { println(it.isEmpty()) }

        if (noBrowserUsageInStepsList.isEmpty()) {

            val fix = RemoveBrowserDefinitionQuickFix(element)

            holder.newAnnotation(
                HighlightSeverity.WARNING,
                "Browser is defined, but not used."
            ).newLocalQuickFix(
                fix,
                MockProblemDescriptor(element, "Fixing unused browser", ProblemHighlightType.ERROR, fix)
            ).registerFix()
                .create()

        }

    }


}

class RemoveBrowserDefinitionQuickFix(requestBodyDefinition: PsiElement) :
    LocalQuickFixOnPsiElement(requestBodyDefinition) {


    override fun getFamilyName(): String {
        return "Remove browser definition fix."
    }

    override fun getText(): String {
        return "Remove unused browser definition    ."
    }

    override fun invoke(
        project: Project,
        file: PsiFile,
        startElement: PsiElement,
        endElement: PsiElement
    ) {

        val range = startElement.textRange
        file.viewProvider.document.replaceString(
            range.startOffset - 1,
            range.endOffset,
            ""
        )

    }

}

class OpenBrowserQuickFixQuickFix(requestBodyDefinition: PsiElement) :
    LocalQuickFixOnPsiElement(requestBodyDefinition) {


    override fun getFamilyName(): String {
        return "Open browser fix."
    }

    override fun getText(): String {
        return "Open web page in browser."
    }

    override fun invoke(
        project: Project,
        file: PsiFile,
        startElement: PsiElement,
        endElement: PsiElement
    ) {

        val range = startElement.textRange
        file.viewProvider.document.replaceString(
            range.startOffset - 1,
            range.endOffset,
            ""
        )

    }

}
