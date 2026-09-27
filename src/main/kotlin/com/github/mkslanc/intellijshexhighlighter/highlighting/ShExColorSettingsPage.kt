package com.github.mkslanc.intellijshexhighlighter.highlighting

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import javax.swing.Icon

class ShExColorSettingsPage : ColorSettingsPage {

    override fun getIcon(): Icon? = null

    override fun getHighlighter(): SyntaxHighlighter = ShExSyntaxHighlighter()

    override fun getDemoText() = """
        # A compact ShEx schema
        PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
        PREFIX ex: <https://example.com/ns#>

        ex:Person CLOSED {
          ex:name xsd:string ;
          ex:email @ex:Email + ;
          ex:age xsd:integer MININCLUSIVE 0 ? ;
          ex:status [ex:active ex:inactive] ;
          ex:label "Person"@en
        }
    """.trimIndent()

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey>? = null

    override fun getAttributeDescriptors() = DESCRIPTORS

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getDisplayName() = "ShEx"

    companion object {
        private val DESCRIPTORS = arrayOf(
            AttributesDescriptor("Comment", ShExSyntaxHighlighter.COMMENT),
            AttributesDescriptor("Keyword", ShExSyntaxHighlighter.KEYWORD),
            AttributesDescriptor("IRI", ShExSyntaxHighlighter.IRI),
            AttributesDescriptor("Prefixed name", ShExSyntaxHighlighter.PREFIXED_NAME),
            AttributesDescriptor("Shape reference", ShExSyntaxHighlighter.SHAPE_REFERENCE),
            AttributesDescriptor("Blank node", ShExSyntaxHighlighter.BLANK_NODE),
            AttributesDescriptor("String", ShExSyntaxHighlighter.STRING),
            AttributesDescriptor("Language tag", ShExSyntaxHighlighter.LANG_TAG),
            AttributesDescriptor("Regular expression", ShExSyntaxHighlighter.REGEXP),
            AttributesDescriptor("Number", ShExSyntaxHighlighter.NUMBER),
            AttributesDescriptor("Identifier", ShExSyntaxHighlighter.IDENTIFIER),
            AttributesDescriptor("Operator", ShExSyntaxHighlighter.OPERATOR),
            AttributesDescriptor("Braces", ShExSyntaxHighlighter.BRACES),
            AttributesDescriptor("Brackets", ShExSyntaxHighlighter.BRACKETS),
            AttributesDescriptor("Parentheses", ShExSyntaxHighlighter.PARENTHESES),
            AttributesDescriptor("Semicolon", ShExSyntaxHighlighter.SEMICOLON),
            AttributesDescriptor("Comma", ShExSyntaxHighlighter.COMMA),
            AttributesDescriptor("Dot", ShExSyntaxHighlighter.DOT),
        )
    }
}
