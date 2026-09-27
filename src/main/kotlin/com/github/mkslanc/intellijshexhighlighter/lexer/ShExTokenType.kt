package com.github.mkslanc.intellijshexhighlighter.lexer

import com.github.mkslanc.intellijshexhighlighter.ShExLanguage
import com.intellij.psi.tree.IElementType

class ShExTokenType(debugName: String) : IElementType(debugName, ShExLanguage) {
    override fun toString() = "ShExTokenType.${super.toString()}"
}
