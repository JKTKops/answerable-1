package examples.testgeneration.generators.reference;

import edu.illinois.cs.cs125.answerable.annotations.Solution;

public class DefaultCtorClassAsParam {

  @Solution(prints = true)
  public void accept(DefaultCtorClassAsParam other) {
    System.out.println("OK");
  }
}
