package com.github.mkslanc.intellijshexhighlighter.lexer

object ShExTokenTypes {
    @JvmField val COMMENT = ShExTokenType("COMMENT")
    @JvmField val KEYWORD = ShExTokenType("KEYWORD")
    @JvmField val IRI = ShExTokenType("IRI")
    @JvmField val PREFIXED_NAME = ShExTokenType("PREFIXED_NAME")
    @JvmField val SHAPE_REFERENCE = ShExTokenType("SHAPE_REFERENCE")
    @JvmField val BLANK_NODE = ShExTokenType("BLANK_NODE")
    @JvmField val STRING = ShExTokenType("STRING")
    @JvmField val LANG_TAG = ShExTokenType("LANG_TAG")
    @JvmField val REGEXP = ShExTokenType("REGEXP")
    @JvmField val NUMBER = ShExTokenType("NUMBER")
    @JvmField val BOOLEAN = ShExTokenType("BOOLEAN")
    @JvmField val IDENTIFIER = ShExTokenType("IDENTIFIER")
    @JvmField val OPERATOR = ShExTokenType("OPERATOR")
    @JvmField val BRACE = ShExTokenType("BRACE")
    @JvmField val BRACKET = ShExTokenType("BRACKET")
    @JvmField val PARENTHESIS = ShExTokenType("PARENTHESIS")
    @JvmField val SEMICOLON = ShExTokenType("SEMICOLON")
    @JvmField val COMMA = ShExTokenType("COMMA")
    @JvmField val DOT = ShExTokenType("DOT")
    @JvmField val OTHER = ShExTokenType("OTHER")
}
