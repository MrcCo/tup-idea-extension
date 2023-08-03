package rs.ac.bg.etf.sm203134m.tupideaextension.syntax.highlighter

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.tree.IElementType
import org.antlr.intellij.adaptor.lexer.ANTLRLexerAdaptor
import rs.ac.bg.etf.sm203134m.antlr4.TupLexer
import rs.ac.bg.etf.sm203134m.tupideaextension.TupLanguage
import rs.ac.bg.etf.sm203134m.tupideaextension.syntax.PSIElementInitializer.getTokenElementType

class TupSyntaxHighlighter : SyntaxHighlighterBase() {

    companion object {
        val TUP_KEYWORD = TextAttributesKey.createTextAttributesKey(
            "TUP.KEYWORD",
            DefaultLanguageHighlighterColors.KEYWORD
        )

        val TUP_STRING = TextAttributesKey.createTextAttributesKey(
            "TUP.STRING",
            DefaultLanguageHighlighterColors.STRING
        )

        val TUP_IDENTIFIER = TextAttributesKey.createTextAttributesKey(
            "TUP.IDENTIFIER",
            DefaultLanguageHighlighterColors.CONSTANT // this highlighting feels better
        )

        val TUP_INTEGER = TextAttributesKey.createTextAttributesKey(
            "TUP.INTEGER",
            DefaultLanguageHighlighterColors.NUMBER
        )
    }

    private val keywordTokenTypes = listOf(
        TupLexer.TEST,
        TupLexer.NAME,
        TupLexer.DESCRIPTION,
        TupLexer.STEPS
    ).map { getTokenElementType(it) }

    private val stringTokenTypes = listOf(
        TupLexer.STRING,
        TupLexer.STRING_START,
        TupLexer.STRING_END,
        TupLexer.TEXT
    ).map { getTokenElementType(it) }

    private val identifierTokenTypes = listOf(
        TupLexer.IDENTIFIER,
    ).map { getTokenElementType(it) }

    private val integerTokenTypes = listOf(
        TupLexer.INTEGER
    ).map { getTokenElementType(it) }

    override fun getHighlightingLexer(): Lexer {
        return ANTLRLexerAdaptor(TupLanguage.INSTANCE, TupLexer(null))
    }

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> {
        return if (keywordTokenTypes.contains(tokenType)) {
            arrayOf(TUP_KEYWORD)
        } else if (stringTokenTypes.contains(tokenType)) {
            arrayOf(TUP_STRING)
        } else if (identifierTokenTypes.contains(tokenType)) {
            arrayOf(TUP_IDENTIFIER)
        } else if (integerTokenTypes.contains(tokenType)) {
            arrayOf(TUP_INTEGER)
        } else {
            emptyArray()
        }
    }

}

class TupSyntaxHighlighterFactory : SyntaxHighlighterFactory() {
    override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?): SyntaxHighlighter {
        return TupSyntaxHighlighter()
    }
}