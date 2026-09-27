# ShEx Syntax Highlighter

![Build](https://github.com/mkslanc/intellij-shex-highlighter/workflows/Build/badge.svg)

A small JetBrains IDE plugin that adds syntax highlighting for
[Shape Expressions Compact Syntax (ShExC)][shex]. It recognizes files with the
`.shex` extension in WebStorm and other IntelliJ Platform IDEs.

```shex
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
PREFIX jsc: <did:ng:x:jscontact#>

jsc:ContactCard {
  jsc:version ["2.0"] ;
  jsc:uid xsd:string ;
  jsc:name @jsc:Name ? ;
  jsc:email @jsc:EmailAddress *
}
```

The plugin highlights directives and keywords, IRIs, prefixed names, shape
references, strings, language tags, regular expressions, numbers, comments,
operators, and delimiters. Colors can be adjusted under
<kbd>Settings</kbd> > <kbd>Editor</kbd> > <kbd>Color Scheme</kbd> >
<kbd>ShEx</kbd>.

This first version intentionally provides lexical highlighting only. It does
not parse or validate schemas and does not provide completion, navigation,
formatting, or support specific to the ShExJ and ShExR serializations.

## Installation

- Download a ZIP from [GitHub Releases][releases], then choose
  <kbd>Settings</kbd> > <kbd>Plugins</kbd> > <kbd>gear icon</kbd> >
  <kbd>Install Plugin from Disk...</kbd>.
- Marketplace installation will be documented after the first publication.

## Development

Java 21 is required.

```shell
./gradlew check
./gradlew buildPlugin
```

The installable ZIP is written to `build/distributions`. Run a development
WebStorm instance with `./gradlew runIde`.

[shex]: https://shex.io/shex-semantics/#shexc
[releases]: https://github.com/mkslanc/intellij-shex-highlighter/releases
