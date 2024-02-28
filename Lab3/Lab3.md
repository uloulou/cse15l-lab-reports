# Lab Report 3 - Bugs and Commands (WEEK 5)

## Part 1 - Bugs
1. A failure-inducing input for the buggy program, as a JUnit test and any associated code

```
   @Test
   public void testReversed1() {
     int[] input1 = {1,3,5,7 };
     assertArrayEquals(new int[]{ 7,5,3,1}, ArrayExamples.reversed(input1));
   }
```

2. An input that doesn't induce a failure, as a JUnit test and any associated code

```
   @Test
   public void testReversed2() {
     int[] input2 = {0};
     assertArrayEquals(new int[]{0}, ArrayExamples.reversed(input2));
   }
```

3. The symptom, as the output of running the tests (provide it as a screenshot of running JUnit with at least the two inputs above)

JUnit test code:

![Image](Images/Bugcode.png)

Output of Running the Tests:

![Image](Images/bugoutput.png)

4. The bug, as the before-and-after code change required to fix it (as two code blocks in Markdown)

```
@Before

static int[] reversed(int[] arr) {
  int[] newArray = new int[arr.length];
  for(int i = 0; i < arr.length; i += 1) {
    arr[i] = newArray[arr.length - i - 1];
  }
  return arr;
}

```

```
@After

static int[] reversed(int[] arr) {
  int[] newArray = new int[arr.length];
  for(int i = 0; i < arr.length; i += 1) {
    newArray[i] = arr[arr.length - i - 1];
  }
  return newArray;
}

```

## Why the fix addresses the issue?

- In the Before code, even though a new integer array newArray is created, the line `arr[i] = newArray[arr.length - i - 1];` in the for loop is updating everything element in the arr with elements in the newArray (which is empty as it is newly created) and return arr which causes all the elements in the arr to be 0. 

- In the After code, I replaced the line `arr[i] = newArray[arr.length - i - 1];` with `newArray[i] = arr[arr.length - i - 1];` and also make it `return newArray;` instead of `return arr;`. Thus, when the for loop runs, all the empty elements inside the newly made newArray will be updated with the elements inside arr in reverse order. Furthremore, as we return to newArray, it will result it a reverse order of the input arr when `reversed(int[] arr)` is called.

## Part 2 - Researching Commands
