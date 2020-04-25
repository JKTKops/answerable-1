package examples.testgeneration.validation.reference;

import edu.illinois.cs.cs125.answerable.DefaultTestRunArguments;
import edu.illinois.cs.cs125.answerable.Solution;
import edu.illinois.cs.cs125.answerable.api.TestOutput;
import edu.illinois.cs.cs125.answerable.Verify;
import org.junit.jupiter.api.Assertions;

public class ArgsOnInvalidVerify {

    @Solution
    public static int zero() {
        return 0;
    }

    @Verify
    @DefaultTestRunArguments(numTests = 32)
    public static void verify(TestOutput<Void> ours, TestOutput<Void> theirs) {
        Assertions.assertEquals(ours.getOutput(), theirs.getOutput());
    }

}
