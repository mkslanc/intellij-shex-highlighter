package com.github.mkslanc.intellijshexhighlighter

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

class ShExFileType private constructor() : LanguageFileType(ShExLanguage) {

    override fun getName() = "ShEx"

    override fun getDescription() = "Shape Expressions Compact Syntax"

    override fun getDefaultExtension() = "shex"

    override fun getIcon(): Icon? = null

    companion object {
        @JvmField
        val INSTANCE = ShExFileType()
    }
}
