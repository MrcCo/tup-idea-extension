package rs.ac.bg.etf.sm203134m.tupideaextension.template

import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.codeInsight.template.TemplateContextType

class UITestTemplate: TemplateContextType("api-tup", "Tup") {

    override fun isInContext(templateActionContext: TemplateActionContext): Boolean {
        // for whatever reason file.text has a suffix of IntellijIdeaRulezzz
        return templateActionContext.file.name.endsWith(".tup") && templateActionContext.file.text.length < 8 + "IntellijIdeaRulezzz".length

    }
}