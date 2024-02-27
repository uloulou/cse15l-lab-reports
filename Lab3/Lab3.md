# Lab Report 3 - Bugs and Commands (WEEK 5)

## Part 1
1. A failure-inducing input for the buggy program, as a JUnit test and any associated code
`
   @Test
   public void testReversed1() {
     int[] input1 = {1,3,5,7 };
     assertArrayEquals(new int[]{ 7,5,3,1}, ArrayExamples.reversed(input1));
   }
`

3. An input that doesn't induce a failure, as a JUnit test and any associated code

4. The symptom, as the output of running the tests (provide it as a screenshot of running JUnit with at least the two inputs above)

5. The bug, as the before-and-after code change required to fix it (as two code blocks in Markdown)
