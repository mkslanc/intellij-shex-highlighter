package com.github.mkslanc.intellijshexhighlighter.highlighting

import com.github.mkslanc.intellijshexhighlighter.lexer.ShExLexer
import com.github.mkslanc.intellijshexhighlighter.lexer.ShExTokenTypes
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

class ShExSyntaxHighlighter : SyntaxHighlighterBase() {

    override fun getHighlightingLexer(): Lexer = ShExLexer()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> = when (tokenType) {
        ShExTokenTypes.COMMENT -> pack(COMMENT)
        ShExTokenTypes.KEYWORD, ShExTokenTypes.BOOLEAN -> pack(KEYWORD)
        ShExTokenTypes.IRI -> pack(IRI)
        ShExTokenTypes.PREFIXED_NAME -> pack(PREFIXED_NAME)
        ShExTokenTypes.SHAPE_REFERENCE -> pack(SHAPE_REFERENCE)
        ShExTokenTypes.BLANK_NODE -> pack(BLANK_NODE)
        ShExTokenTypes.STRING -> pack(STRING)
        ShExTokenTypes.LANG_TAG -> pack(LANG_TAG)
        ShExTokenTypes.REGEXP -> pack(REGEXP)
        ShExTokenTypes.NUMBER -> pack(NUMBER)
        ShExTokenTypes.IDENTIFIER -> pack(IDENTIFIER)
        ShExTokenTypes.OPERATOR -> pack(OPERATOR)
        ShExTokenTypes.BRACE -> pack(BRACES)
        ShExTokenTypes.BRACKET -> pack(BRACKETS)
        ShExTokenTypes.PARENTHESIS -> pack(PARENTHESES)
        ShExTokenTypes.SEMICOLON -> pack(SEMICOLON)
        ShExTokenTypes.COMMA -> pack(COMMA)
        ShExTokenTypes.DOT -> pack(DOT)
        TokenType.BAD_CHARACTER -> pack(BAD_CHARACTER)
        else -> EMPTY
    }

    companion object {
        @JvmField val COMMENT = key("SHEX_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
        @JvmField val KEYWORD = key("SHEX_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
        @JvmField val IRI = key("SHEX_IRI", DefaultLanguageHighlighterColors.STRING)
        @JvmField val PREFIXED_NAME = key("SHEX_PREFIXED_NAME", DefaultLanguageHighlighterColors.INSTANCE_FIELD)
        @JvmField val SHAPE_REFERENCE = key("SHEX_SHAPE_REFERENCE", DefaultLanguageHighlighterColors.CLASS_REFERENCE)
        @JvmField val BLANK_NODE = key("SHEX_BLANK_NODE", DefaultLanguageHighlighterColors.IDENTIFIER)
        @JvmField val STRING = key("SHEX_STRING", DefaultLanguageHighlighterColors.STRING)
        @JvmField val LANG_TAG = key("SHEX_LANG_TAG", DefaultLanguageHighlighterColors.METADATA)
        @JvmField val REGEXP = key("SHEX_REGEXP", DefaultLanguageHighlighterColors.STRING)
        @JvmField val NUMBER = key("SHEX_NUMBER", DefaultLanguageHighlighterColors.NUMBER)
        @JvmField val IDENTIFIER = key("SHEX_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER)
        @JvmField val OPERATOR = key("SHEX_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN)
        @JvmField val BRACES = key("SHEX_BRACES", DefaultLanguageHighlighterColors.BRACES)
        @JvmField val BRACKETS = key("SHEX_BRACKETS", DefaultLanguageHighlighterColors.BRACKETS)
        @JvmField val PARENTHESES = key("SHEX_PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES)
        @JvmField val SEMICOLON = key("SHEX_SEMICOLON", DefaultLanguageHighlighterColors.SEMICOLON)
        @JvmField val COMMA = key("SHEX_COMMA", DefaultLanguageHighlighterColors.COMMA)
        @JvmField val DOT = key("SHEX_DOT", DefaultLanguageHighlighterColors.DOT)
        @JvmField val BAD_CHARACTER = key("SHEX_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)

        private val EMPTY = emptyArray<TextAttributesKey>()

        private fun key(name: String, fallback: TextAttributesKey) =
            TextAttributesKey.createTextAttributesKey(name, fallback)
    }
}
