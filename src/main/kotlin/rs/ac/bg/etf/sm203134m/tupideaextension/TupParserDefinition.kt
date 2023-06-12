package rs.ac.bg.etf.sm203134m.tupideaextension

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import org.antlr.intellij.adaptor.lexer.ANTLRLexerAdaptor
import org.antlr.intellij.adaptor.lexer.PSIElementTypeFactory
import org.antlr.intellij.adaptor.lexer.TokenIElementType
import org.antlr.intellij.adaptor.parser.ANTLRParserAdaptor
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.tree.ParseTree
import org.intellij.lang.annotations.MagicConstant
import rs.ac.bg.etf.sm203134m.antlr4.TupLexer
import rs.ac.bg.etf.sm203134m.antlr4.TupParser
import rs.ac.bg.etf.sm203134m.tupideaextension.file.TupFile

class TupParserDefinition : ParserDefinition {


    companion object {
        val FILE = IFileElementType(TupLanguage.INSTANCE)
    }

    init {
        PSIElementTypeFactory.defineLanguageIElementTypes(
            TupLanguage.INSTANCE,
            TupLexer.tokenNames,
            TupParser.ruleNames
        )
    }
    val tokenElementTypes = PSIElementTypeFactory.getTokenIElementTypes(TupLanguage.INSTANCE)


    override fun createLexer(project: Project): Lexer {
        return ANTLRLexerAdaptor(TupLanguage.INSTANCE, TupLexer(null))
    }

    override fun createParser(project: Project): PsiParser {
        val parser = TupParser(null)
        return object : ANTLRParserAdaptor(TupLanguage.INSTANCE, parser) {
            override fun parse(parser: Parser, root: IElementType): ParseTree {
                return (parser as TupParser).test()
            }
        }
    }

    override fun getFileNodeType(): IFileElementType {
        return FILE
    }

    override fun getCommentTokens(): TokenSet {
        return TokenSet.EMPTY
    }

    override fun getStringLiteralElements(): TokenSet {
        val stringType = getTokenElementType(TupLexer.STRING)
        return TokenSet.create(stringType)
    }

    override fun createElement(node: ASTNode): PsiElement {
        return ASTWrapperPsiElement(node)
    }

    override fun createFile(viewProvider: FileViewProvider): PsiFile {
        return TupFile(viewProvider)
    }

    override fun getWhitespaceTokens(): TokenSet {
        val wsType = getTokenElementType(TupLexer.WS)
        return TokenSet.create(wsType)
    }

    private fun getTokenElementType(@MagicConstant(valuesFromClass = TupLexer::class) tokenIndex: Int): TokenIElementType? {
        return tokenElementTypes[tokenIndex]
    }



}