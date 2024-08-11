package com.odesa.musicMatters.utils

import androidx.annotation.FloatRange
import com.odesa.musicMatters.core.data.utils.subListNonStrict
import kotlin.math.max
import kotlin.math.min

/**
 * A fuzzy searcher is a type of search algorithm that is used to find approximate matches for a
 * given query, even if the query contains spelling errors, typos, or other variations.
 *
 * Traditional search algorithms typically rely on exact matches between the search query and the
 * indexed data. However, in many real-world scenarios, users may make mistakes while typing, or the
 * data being searched may contain variations or errors. In such cases, a fuzzy searcher comes in
 * handy by allowing for a degree of flexibility in matching.
 *
 * Fuzzy searching algorithms use techniques such as Levenshtein distance, which measures the
 * number of single-character edits ( insertions, deletions, or substitutions ) needed to change
 * one word into another. By calculating similarity between the query and the indexed data using
 * such techniques, fuzzy searchers can return results that are close matches to the original
 * query, even if they are not exact matches.
 *
 * Fuzzy searchers are commonly used in applications such as search engines, spell checkers, and
 * data cleansing tools to improve the accuracy of search results and enhance user experience.
 */

class FuzzySearcher<T>( val options: List<FuzzySearchOption<T>> ) {
    fun search(
        terms: String,
        entities: List<T>,
        maxLength: Int = -1,
        minScore: Float = 0.25f,
    ): List<FuzzyResult<T>> {
        var results = entities
            .map { compare( terms, it ) }
            .sortedByDescending { it.ratio }
        if ( maxLength > -1 )
            results = results.subListNonStrict( maxLength )
        return results.filter { it.ratio > minScore }
    }

    private fun compare(
        terms: String,
        entity: T,
    ): FuzzyResult<T> {
        var ratio = 0f
        var totalWeight = 0
        val comparator = FuzzyComparator( terms )
        options.forEach { searchOption ->
            searchOption.fuzzyComparator.invoke( comparator, entity )?.let {
                ratio += it * searchOption.weight
                totalWeight += searchOption.weight
            }
        }
        return FuzzyResult( ratio / totalWeight, entity )
    }
}

data class FuzzySearchOption<T>(
    val fuzzyComparator: FuzzyComparator.(T) -> Float?,
    val weight: Int = 1,
)

class FuzzyComparator( val input: String ) {
    fun compareString( value: String ) = Fuzzy.compare( input, value )

    fun compareCollection( values: Collection<String> ): Float {
        var weight = 0f
        values.forEach {
            weight += compareString( it )
        }
        return weight / min( 1, values.size )
    }
}

data class FuzzyResult<T>(
    @FloatRange( from = 0.0, to = 100.0 ) val ratio: Float,
    val entity: T,
)

object Fuzzy {

    private const val MATCH_BONUS = 2f
    private const val DISTANCE_PENALTY_MULTIPLIER = 0.15f
    private const val NO_MATCH_PENALTY = -0.3f

    private val whitespaceRegex = Regex(  """\s+""" )

    fun compare( input: String, against: String ) =
        compareStrict( normalizeTerms( input ), normalizeTerms( against ) )

    private fun compareStrict( input: String, against: String ): Float {
        val inputLength = input.length
        val againstLength = against.length
        var currentPosition = 0
        var previousPosition = 0
        var score = 0f

        for ( inputPosition in 0 until inputLength ) {
            val valueAtCurrentInputPosition = input[ inputPosition ]
            var matched = false
            for ( againstPosition in currentPosition until againstLength ) {
                val valueAtCurrentAgainstPosition = against[ againstPosition ]
                if ( valueAtCurrentInputPosition == valueAtCurrentAgainstPosition ) {
                    previousPosition = currentPosition
                    currentPosition = againstPosition
                    matched = true
                    break
                }
            }
            score += if ( matched ) MATCH_BONUS - ( DISTANCE_PENALTY_MULTIPLIER * ( currentPosition - previousPosition - 1 ) )
            else NO_MATCH_PENALTY
            currentPosition++
        }
        return max( 0f, score ) / againstLength
    }

    private fun normalizeTerms( terms: String ) = terms.lowercase().replace(
        whitespaceRegex,
        " "
    )
}