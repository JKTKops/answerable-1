package examples.testgeneration.reference

import edu.illinois.cs.cs125.answerable.api.Precondition
import edu.illinois.cs.cs125.answerable.api.Solution

class KtPrecondition {

    @Solution
    fun firstLetter(text: String): Char {
        return if (text.isEmpty()) '?' else text.toCharArray()[0]
    }
}

@Precondition
fun precondition(text: String): Boolean {
    return text.isNotEmpty()
}