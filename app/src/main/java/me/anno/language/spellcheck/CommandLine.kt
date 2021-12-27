package me.anno.language.spellcheck

/*
// we first would need to filter out what really is needed for spellchecking; 100MB is just too large for this feature,
// and many games might not even use it
import me.anno.utils.strings.StringHelper.distance
import org.languagetool.JLanguageTool
import org.languagetool.Language
import kotlin.Throws
import org.languagetool.language.BritishEnglish
import java.io.IOException
import java.util.*

class CommandLine(langCode: String) {

    var langTool: JLanguageTool

    fun process(sentence: String): List<Suggestion> {
        return if (sentence.isNotBlank()) {
            val matches = langTool.check(sentence)
            val sentenceBytes = sentence.toByteArray()
            val results = ArrayList<Suggestion>(matches.size)
            for (match in matches) {
                val replacements = ArrayList(match.suggestedReplacements)
                val base = String(sentenceBytes, match.fromPos, match.toPos - match.fromPos)
                replacements.sortBy { base.distance(it, true) }
                results.add(
                    Suggestion(
                        match.fromPos,
                        match.toPos,
                        match.message,
                        match.shortMessage,
                        replacements
                    )
                )
            }
            results
        } else emptyList()
    }

    companion object {

        private val languageMap = hashMapOf(
            // this is a list of languages available in LanguageTool
            // in the Android build, for now, there only will be English available
            "ar" to "org.languagetool.language.Arabic",
            "en" to "org.languagetool.language.English",
            "en-us" to "org.languagetool.language.AmericanEnglish",
            "en-gb" to "org.languagetool.language.BritishEnglish",
            "en-au" to "org.languagetool.language.AustralianEnglish",
            "en-ca" to "org.languagetool.language.CanadianEnglish",
            "en-nz" to "org.languagetool.language.NewZealandEnglish",
            "en-za" to "org.languagetool.language.SouthAfricanEnglish",
            "fa" to "org.languagetool.language.Persian",
            "fr" to "org.languagetool.language.French",
            // "de" to "org.languagetool.language.German",
            "de-de" to "org.languagetool.language.GermanyGerman",
            "de-at" to "org.languagetool.language.AustrianGerman",
            "de-ch" to "org.languagetool.language.SwissGerman",
            "de-de-x-simple-language" to "org.languagetool.language.SimpleGerman",
            "pl-pl" to "org.languagetool.language.Polish",
            "ca-es" to "org.languagetool.language.Catalan",
            "ca-es-valencia" to "org.languagetool.language.ValencianCatalan",
            "it" to "org.languagetool.language.Italian",
            "br-fr" to "org.languagetool.language.Breton",
            "nl" to "org.languagetool.language.Dutch",
            "pt" to "org.languagetool.language.Portuguese",
            "pt-pt" to "org.languagetool.language.PortugalPortuguese",
            "pt-br" to "org.languagetool.language.BrazilianPortuguese",
            "pt-ao" to "org.languagetool.language.AngolaPortuguese",
            "pt-mz" to "org.languagetool.language.MozambiquePortuguese",
            "ru-ru" to "org.languagetool.language.Russian",
            "ast-es" to "org.languagetool.language.Asturian",
            "be-by" to "org.languagetool.language.Belarusian",
            "zh-cn" to "org.languagetool.language.Chinese",
            "da-dk" to "org.languagetool.language.Danish",
            "eo" to "org.languagetool.language.Esperanto",
            "ga-ie" to "org.languagetool.language.Irish",
            "gl-es" to "org.languagetool.language.Galician",
            "el-gr" to "org.languagetool.language.Greek",
            "ja-jp" to "org.languagetool.language.Japanese",
            "km-kh" to "org.languagetool.language.Khmer",
            "ro-ro" to "org.languagetool.language.Romanian",
            "sk-sk" to "org.languagetool.language.Slovak",
            "sl-si" to "org.languagetool.language.Slovenian",
            "es" to "org.languagetool.language.Spanish",
            "sv" to "org.languagetool.language.Swedish",
            "ta-in" to "org.languagetool.language.Tamil",
            "tl-ph" to "org.languagetool.language.Tagalog",
            "uk-ua" to "org.languagetool.language.Ukrainian"
        )

        private fun createLanguage(path: String?): Language? {
            path ?: return null
            try {
                val clazz = CommandLine::class.java.classLoader.loadClass(path)
                return clazz.newInstance() as Language
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            }
            return null
        }
    }

    init {
        val languageClass = languageMap[langCode.toLowerCase(Locale.ROOT)]
        val language = createLanguage(languageClass) ?: BritishEnglish()
        langTool = JLanguageTool(language)
    }

}*/