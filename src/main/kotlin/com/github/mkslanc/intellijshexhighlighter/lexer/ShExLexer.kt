package com.github.mkslanc.intellijshexhighlighter.lexer

import com.intellij.lexer.LexerBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

class ShExLexer : LexerBase() {

    private lateinit var buffer: CharSequence
    private var bufferEnd = 0
    private var tokenStart = 0
    private var tokenEnd = 0
    private var tokenType: IElementType? = null

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.buffer = buffer
        bufferEnd = endOffset
        tokenStart = startOffset
        tokenEnd = startOffset
        locateToken()
    }

    override fun getState() = 0

    override fun getTokenType() = tokenType

    override fun getTokenStart() = tokenStart

    override fun getTokenEnd() = tokenEnd

    override fun advance() {
        tokenStart = tokenEnd
        locateToken()
    }

    override fun getBufferSequence() = buffer

    override fun getBufferEnd() = bufferEnd

    private fun locateToken() {
        if (tokenStart >= bufferEnd) {
            tokenEnd = tokenStart
            tokenType = null
            return
        }

        val current = buffer[tokenStart]
        when {
            current.isWhitespace() -> {
                tokenEnd = consumeWhile(tokenStart + 1) { it.isWhitespace() }
                tokenType = TokenType.WHITE_SPACE
            }

            current == '#' -> {
                tokenEnd = consumeUntilLineEnd(tokenStart + 1)
                tokenType = ShExTokenTypes.COMMENT
            }

            current == '<' -> {
                tokenEnd = scanIri(tokenStart)
                tokenType = ShExTokenTypes.IRI
            }

            current == '\'' || current == '"' -> {
                tokenEnd = scanString(tokenStart, current)
                tokenType = ShExTokenTypes.STRING
            }

            current == '@' -> scanAtToken()

            current == '/' -> {
                val regexpEnd = scanRegexp(tokenStart)
                if (regexpEnd != null) {
                    tokenEnd = regexpEnd
                    tokenType = ShExTokenTypes.REGEXP
                } else {
                    tokenEnd = if (startsWith(tokenStart, "//")) tokenStart + 2 else tokenStart + 1
                    tokenType = ShExTokenTypes.OPERATOR
                }
            }

            isNumberStart(tokenStart) -> {
                tokenEnd = scanNumber(tokenStart)
                tokenType = ShExTokenTypes.NUMBER
            }

            startsWith(tokenStart, "_:") -> {
                tokenEnd = scanName(tokenStart + 2)
                tokenType = ShExTokenTypes.BLANK_NODE
            }

            isNameStart(current) -> scanWord()

            current == '{' || current == '}' -> single(ShExTokenTypes.BRACE)
            current == '[' || current == ']' -> single(ShExTokenTypes.BRACKET)
            current == '(' || current == ')' -> single(ShExTokenTypes.PARENTHESIS)
            current == ';' -> single(ShExTokenTypes.SEMICOLON)
            current == ',' -> single(ShExTokenTypes.COMMA)
            current == '.' -> single(ShExTokenTypes.DOT)
            current in OPERATOR_CHARS -> {
                tokenEnd = if (
                    startsWith(tokenStart, "^^") ||
                    startsWith(tokenStart, "%{") ||
                    startsWith(tokenStart, "%}")
                ) tokenStart + 2 else tokenStart + 1
                tokenType = ShExTokenTypes.OPERATOR
            }

            else -> single(ShExTokenTypes.OTHER)
        }
    }

    private fun scanAtToken() {
        if (tokenStart + 1 < bufferEnd && buffer[tokenStart + 1] == '<') {
            tokenEnd = scanIri(tokenStart + 1)
            tokenType = ShExTokenTypes.SHAPE_REFERENCE
            return
        }

        tokenEnd = scanName(tokenStart + 1)
        if (tokenEnd == tokenStart + 1) {
            single(ShExTokenTypes.OPERATOR)
            return
        }

        val text = buffer.subSequence(tokenStart + 1, tokenEnd)
        tokenType = if (text.contains(':')) {
            ShExTokenTypes.SHAPE_REFERENCE
        } else {
            ShExTokenTypes.LANG_TAG
        }
    }

    private fun scanWord() {
        tokenEnd = scanName(tokenStart + 1)
        val text = buffer.subSequence(tokenStart, tokenEnd).toString()
        val upper = text.uppercase()
        tokenType = when {
            ':' in text -> ShExTokenTypes.PREFIXED_NAME
            upper in KEYWORDS -> ShExTokenTypes.KEYWORD
            text == "true" || text == "false" -> ShExTokenTypes.BOOLEAN
            else -> ShExTokenTypes.IDENTIFIER
        }
    }

    private fun scanIri(start: Int): Int {
        var index = start + 1
        while (index < bufferEnd) {
            when (buffer[index]) {
                '\\' -> index += if (index + 1 < bufferEnd) 2 else 1
                '>' -> return index + 1
                '\n', '\r' -> return index
                else -> index++
            }
        }
        return bufferEnd
    }

    private fun scanString(start: Int, quote: Char): Int {
        val longString = start + 2 < bufferEnd &&
            buffer[start + 1] == quote && buffer[start + 2] == quote
        var index = start + if (longString) 3 else 1

        while (index < bufferEnd) {
            val current = buffer[index]
            if (current == '\\') {
                index += if (index + 1 < bufferEnd) 2 else 1
                continue
            }
            if (longString && startsWith(index, "$quote$quote$quote")) {
                return index + 3
            }
            if (!longString && current == quote) {
                return index + 1
            }
            if (!longString && (current == '\n' || current == '\r')) {
                return index
            }
            index++
        }
        return bufferEnd
    }

    private fun scanRegexp(start: Int): Int? {
        if (start + 1 >= bufferEnd || buffer[start + 1] == '/') return null
        var index = start + 1
        var hasContent = false
        while (index < bufferEnd) {
            when (val current = buffer[index]) {
                '\\' -> {
                    hasContent = true
                    index += if (index + 1 < bufferEnd) 2 else 1
                }

                '\n', '\r' -> return null
                '/' -> {
                    if (!hasContent) return null
                    index++
                    while (index < bufferEnd && buffer[index] in REGEXP_FLAGS) index++
                    return index
                }

                else -> {
                    hasContent = true
                    index++
                }
            }
        }
        return null
    }

    private fun isNumberStart(start: Int): Boolean {
        val current = buffer[start]
        if (current.isDigit()) return true
        if (current == '.' && start + 1 < bufferEnd) return buffer[start + 1].isDigit()
        if ((current == '+' || current == '-') && start + 1 < bufferEnd) {
            val next = buffer[start + 1]
            return next.isDigit() ||
                (next == '.' && start + 2 < bufferEnd && buffer[start + 2].isDigit())
        }
        return false
    }

    private fun scanNumber(start: Int): Int {
        var index = start
        if (buffer[index] == '+' || buffer[index] == '-') index++
        while (index < bufferEnd && buffer[index].isDigit()) index++
        if (index < bufferEnd && buffer[index] == '.') {
            index++
            while (index < bufferEnd && buffer[index].isDigit()) index++
        }
        if (index < bufferEnd && (buffer[index] == 'e' || buffer[index] == 'E')) {
            val exponentStart = index
            index++
            if (index < bufferEnd && (buffer[index] == '+' || buffer[index] == '-')) index++
            val digitsStart = index
            while (index < bufferEnd && buffer[index].isDigit()) index++
            if (digitsStart == index) return exponentStart
        }
        return index
    }

    private fun scanName(start: Int): Int {
        var index = start
        while (index < bufferEnd) {
            val current = buffer[index]
            if (isNamePart(current)) {
                index++
            } else if (current == '\\' && index + 1 < bufferEnd) {
                index += 2
            } else {
                break
            }
        }
        return index
    }

    private fun consumeUntilLineEnd(start: Int): Int {
        var index = start
        while (index < bufferEnd && buffer[index] != '\n' && buffer[index] != '\r') index++
        return index
    }

    private inline fun consumeWhile(start: Int, predicate: (Char) -> Boolean): Int {
        var index = start
        while (index < bufferEnd && predicate(buffer[index])) index++
        return index
    }

    private fun single(type: IElementType) {
        tokenEnd = tokenStart + 1
        tokenType = type
    }

    private fun startsWith(start: Int, text: String): Boolean {
        if (start + text.length > bufferEnd) return false
        for (index in text.indices) {
            if (buffer[start + index] != text[index]) return false
        }
        return true
    }

    private fun isNameStart(char: Char) = char == ':' || char == '_' || Character.isLetter(char)

    private fun isNamePart(char: Char) =
        Character.isLetterOrDigit(char) || char == '_' || char == '-' || char == '.' || char == ':' || char == '%'

    companion object {
        private val KEYWORDS = setOf(
            "BASE", "PREFIX", "IMPORT", "START", "EXTERNAL", "CLOSED", "EXTRA",
            "AND", "OR", "NOT", "IRI", "BNODE", "NONLITERAL", "LITERAL",
            "LENGTH", "MINLENGTH", "MAXLENGTH", "PATTERN", "FLAGS",
            "MININCLUSIVE", "MINEXCLUSIVE", "MAXINCLUSIVE", "MAXEXCLUSIVE",
            "TOTALDIGITS", "FRACTIONDIGITS", "A",
        )
        private val OPERATOR_CHARS = setOf('!', '$', '%', '&', '*', '+', '-', '/', '=', '?', '@', '^', '|', '~')
        private val REGEXP_FLAGS = setOf('s', 'm', 'i', 'x')
    }
}
