package com.github.mkslanc.intellijshexhighlighter

import com.github.mkslanc.intellijshexhighlighter.lexer.ShExLexer
import com.github.mkslanc.intellijshexhighlighter.lexer.ShExTokenTypes
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class ShExLexerTest : BasePlatformTestCase() {

    fun testProvidedSchemaConstructs() {
        val tokens = lex(
            """
                PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
                PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
                PREFIX jsc: <did:ng:x:jscontact#>

                # ContactCard
                jsc:ContactCard {
                  a [jsc:Card] ;
                  jsc:version ["2.0"] ;
                  jsc:uid xsd:string ;
                  jsc:name @jsc:Name ? ;
                  jsc:email @jsc:EmailAddress *
                }
            """.trimIndent(),
        )

        assertToken(tokens, ShExTokenTypes.KEYWORD, "PREFIX")
        assertToken(tokens, ShExTokenTypes.PREFIXED_NAME, "jsc:ContactCard")
        assertToken(tokens, ShExTokenTypes.IRI, "<did:ng:x:jscontact#>")
        assertToken(tokens, ShExTokenTypes.COMMENT, "# ContactCard")
        assertToken(tokens, ShExTokenTypes.KEYWORD, "a")
        assertToken(tokens, ShExTokenTypes.STRING, "\"2.0\"")
        assertToken(tokens, ShExTokenTypes.SHAPE_REFERENCE, "@jsc:Name")
        assertToken(tokens, ShExTokenTypes.OPERATOR, "?")
        assertToken(tokens, ShExTokenTypes.OPERATOR, "*")
        assertFalse(tokens.any { it.type == TokenType.BAD_CHARACTER })
        assertFalse(tokens.any { it.type == ShExTokenTypes.OTHER })
    }

    fun testIriHashDoesNotStartComment() {
        val tokens = lex("PREFIX ex: <https://example.test/ns#> # actual comment")

        assertToken(tokens, ShExTokenTypes.IRI, "<https://example.test/ns#>")
        assertEquals(1, tokens.count { it.type == ShExTokenTypes.COMMENT })
        assertToken(tokens, ShExTokenTypes.COMMENT, "# actual comment")
    }

    fun testLiteralAndTerminalForms() {
        val tokens = lex(
            "\"hello\"@en '''multi\nline''' PATTERN /ab\\/c/im _:blank -1.2e3 true",
        )

        assertToken(tokens, ShExTokenTypes.STRING, "\"hello\"")
        assertToken(tokens, ShExTokenTypes.LANG_TAG, "@en")
        assertToken(tokens, ShExTokenTypes.STRING, "'''multi\nline'''")
        assertToken(tokens, ShExTokenTypes.REGEXP, "/ab\\/c/im")
        assertToken(tokens, ShExTokenTypes.BLANK_NODE, "_:blank")
        assertToken(tokens, ShExTokenTypes.NUMBER, "-1.2e3")
        assertToken(tokens, ShExTokenTypes.BOOLEAN, "true")
    }

    fun testFileTypeRegistration() {
        assertSame(
            ShExFileType.INSTANCE,
            FileTypeManager.getInstance().getFileTypeByExtension("shex"),
        )
    }

    fun testIncompleteIriRecoversAtNextLine() {
        val tokens = lex("<unterminated\nPREFIX ex: <https://example.test/>")

        assertToken(tokens, ShExTokenTypes.IRI, "<unterminated")
        assertToken(tokens, ShExTokenTypes.KEYWORD, "PREFIX")
        assertToken(tokens, ShExTokenTypes.IRI, "<https://example.test/>")
    }

    private fun lex(text: String): List<Token> {
        val lexer = ShExLexer()
        val tokens = mutableListOf<Token>()
        lexer.start(text)
        var previousEnd = 0
        while (lexer.tokenType != null) {
            assertEquals("Lexer left a gap", previousEnd, lexer.tokenStart)
            assertTrue("Lexer did not advance at ${lexer.tokenStart}", lexer.tokenEnd > lexer.tokenStart)
            tokens += Token(
                lexer.tokenType!!,
                text.substring(lexer.tokenStart, lexer.tokenEnd),
            )
            previousEnd = lexer.tokenEnd
            lexer.advance()
        }
        assertEquals("Lexer did not consume all input", text.length, previousEnd)
        return tokens
    }

    private fun assertToken(tokens: List<Token>, type: IElementType, text: String) {
        assertTrue(
            "Expected $type token '$text', got ${tokens.joinToString { "${it.type} '${it.text}'" }}",
            tokens.any { it.type == type && it.text == text },
        )
    }

    private data class Token(val type: IElementType, val text: String)
}
