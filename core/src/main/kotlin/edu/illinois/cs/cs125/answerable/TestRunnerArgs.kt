@file: JvmName("Arguments")
package edu.illinois.cs.cs125.answerable

import edu.illinois.cs.cs125.answerable.api.Generator

/**
 * A class that holds configuration for [TestRunner]s.
 */
data class TestRunnerArgs(
    /** The total number of tests to execute. Defaults to 1024. */
    val numTests: Int? = null,
    /** The maximum number of tests which can be discarded before Answerable gives up. Defaults to 1024. */
    val maxDiscards: Int? = null,
    /** The maximum number of edge-case only tests to execute. Defaults to [numTests] / 16. */
    val maxOnlyEdgeCaseTests: Int? = null,
    /** The maximum number of simple-case only tests to execute. Defaults to [numTests] / 16. */
    val maxOnlySimpleCaseTests: Int? = null,
    /** The total number of mixed edge- and simple-case tests to execute. Defaults to [numTests] / 16. */
    val numSimpleEdgeMixedTests: Int? = null,
    /** The total number of all-generated tests to execute. Defaults to [numTests] / 2. */
    val numAllGeneratedTests: Int? = null,
    /** The upper bound on the 'complexity' @[Generator] parameter for this test run. Defaults to 100. */
    val maxComplexity: Int? = null
) {
    fun applyOver(base: TestRunnerArgs): TestRunnerArgs {
        return TestRunnerArgs(
            numTests = numTests ?: base.numTests,
            maxDiscards = maxDiscards ?: base.maxDiscards,
            maxOnlyEdgeCaseTests = maxOnlyEdgeCaseTests ?: base.maxOnlyEdgeCaseTests,
            maxOnlySimpleCaseTests = maxOnlySimpleCaseTests ?: base.maxOnlySimpleCaseTests,
            numSimpleEdgeMixedTests = numSimpleEdgeMixedTests ?: base.numSimpleEdgeMixedTests,
            numAllGeneratedTests = numAllGeneratedTests ?: base.numAllGeneratedTests,
            maxComplexity = maxComplexity ?: base.maxComplexity
        )
    }
    fun resolve(): TestRunnerArgs {
        val resolvedNumTests = numTests ?: 1024
        return TestRunnerArgs(
            numTests = resolvedNumTests,
            maxDiscards = maxDiscards ?: 1024,
            maxOnlyEdgeCaseTests = maxOnlyEdgeCaseTests ?: resolvedNumTests / 16,
            maxOnlySimpleCaseTests = maxOnlySimpleCaseTests ?: resolvedNumTests / 16,
            numSimpleEdgeMixedTests = numSimpleEdgeMixedTests ?: resolvedNumTests / 16,
            numAllGeneratedTests = numAllGeneratedTests ?: resolvedNumTests / 2,
            maxComplexity = maxComplexity ?: 100
        )
    }
}

/**
 * An empty (all-default) [TestRunnerArgs].
 */
val defaultArgs = TestRunnerArgs()