package rs.ac.bg.etf.sm203134m.tupideaextension.syntax

import org.antlr.intellij.adaptor.lexer.PSIElementTypeFactory
import org.antlr.intellij.adaptor.lexer.RuleIElementType
import org.antlr.intellij.adaptor.lexer.TokenIElementType
import org.intellij.lang.annotations.MagicConstant
import rs.ac.bg.etf.sm203134m.antlr4.TupLexer
import rs.ac.bg.etf.sm203134m.antlr4.TupParser
import rs.ac.bg.etf.sm203134m.tupideaextension.TupLanguage

object TupLanguageTokenType {
    init {
        PSIElementTypeFactory.defineLanguageIElementTypes(
            TupLanguage.INSTANCE,
            TupLexer.tokenNames,
            TupParser.ruleNames
        )
    }

    private val TOKEN_ELEMENT_TYPES = PSIElementTypeFactory.getTokenIElementTypes(TupLanguage.INSTANCE)

    fun getTokenElementType(@MagicConstant(valuesFromClass = TupLexer::class) tokenIndex: Int): TokenIElementType {
        return TOKEN_ELEMENT_TYPES[tokenIndex]
    }
}