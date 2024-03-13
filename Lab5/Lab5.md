# Lab Report 5 - Putting it All Together (Week 9)

## Part 1 – Debugging Scenario

Student: Hello! I am writing a bash script for grading the `ListExamples.java` in lab 6. But one of the sample submissions never passed and even ended up saying no such file and missing. I checked the code in GitHub for that sample submission and it is the same as other samples. It should have passed the test. I am quite sure my coding for grading cases is right and I do see the file in the `grading-area` and `student-submission` directory. My guess is my file did not go through the code properly despite it being shown in the directories but I am not sure why. This is what the symptoms looks like on the terminal. The file & directory structure are shown on the left side of the screenshot. I also took a screenshot for file & directory structure in the GitHub link that I am cloning. Please help me!

Content of `grade.sh`:

```
CPATH='.:lib/hamcrest-core-1.3.jar:lib/junit-4.13.2.jar'

rm -rf student-submission
rm -rf grading-area

mkdir grading-area

git clone $1 student-submission
echo 'Finished cloning'


# Draw a picture/take notes on the directory structure that's set up after
# getting to this point

# Then, add here code to compile and run, and do any post-processing of the
# tests

cp student-submission/*.java grading-area
cp TestListExamples.java grading-area 
cp -r lib grading-area

cd grading-area


if ! [ -f ListExamples.java ]
then 
    echo "Missing ListExamples.java in student submission"
    echo "Score: 0"
    exit
fi

javac -cp $CPATH *.java &> compile.txt 
if [ $? -ne 0 ]
then
    echo "Compilation Error"
    echo "Score: 0"
    exit
fi

java -cp $CPATH org.junit.runner.JUnitCore TestListExamples > outputResults 2>&1

SUCCESS=$(grep "OK" outputResults)

if [[ -n $SUCCESS ]]
then 
    echo "Everything passed! Your score is 100%"
else
    totalTests=$(grep -oE "Tests run: [0-9]+" outputResults | grep -oE "[0-9]+")
    Failures=$(grep -oE "Failures: [0-9]+" outputResults | grep -oE "[0-9]+")
    Success=$((totalTests - Failures))
    Percentage=$(echo "scale=2; $Success * 100 / $totalTests" | bc)
    echo "Score: $Percentage%"
fi
```

Content of `GradeServer.java`:

```
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.stream.Stream;

class ExecHelpers {

  /**
    Takes an input stream, reads the full stream, and returns the result as a
    string.

    In Java 9 and later, new String(out.readAllBytes()) would be a better
    option, but using Java 8 for compatibility with ieng6.
  */
  static String streamToString(InputStream out) throws IOException {
    String result = "";
    while(true) {
      int c = out.read();
      if(c == -1) { break; }
      result += (char)c;
    }
    return result;
  }

  /**
    Takes a command, represented as an array of strings as it would by typed at
    the command line, runs it, and returns its combined stdout and stderr as a
    string.
  */
  static String exec(String[] cmd) throws IOException {
    Process p = new ProcessBuilder()
                    .command(Arrays.asList(cmd))
                    .redirectErrorStream(true)
                    .start();
    InputStream outputOfBash = p.getInputStream();
    return String.format("%s\n", streamToString(outputOfBash));
  }

}

class Handler implements URLHandler {
    public String handleRequest(URI url) throws IOException {
       if (url.getPath().equals("/grade")) {
           String[] parameters = url.getQuery().split("=");
           if (parameters[0].equals("repo")) {
               String[] cmd = {"bash", "grade.sh", parameters[1]};
               String result = ExecHelpers.exec(cmd);
               return result;
           }
           else {
               return "Couldn't find query parameter repo";
           }
       }
       else {
           return "Don't know how to handle that path!";
       }
    }
}

class GradeServer {
    public static void main(String[] args) throws IOException {
        if(args.length == 0){
            System.out.println("Missing port number! Try any number between 1024 to 49151");
            return;
        }

        int port = Integer.parseInt(args[0]);

        Server.start(port, new Handler());
    }
}

class ExecExamples {
  public static void main(String[] args) throws IOException {
    String[] cmd1 = {"ls", "lib"};
    System.out.println(ExecHelpers.exec(cmd1));

    String[] cmd2 = {"pwd"};
    System.out.println(ExecHelpers.exec(cmd2));

    String[] cmd3 = {"touch", "a-new-file.txt"};
    System.out.println(ExecHelpers.exec(cmd3));
  }
}
```

Content of `Server.java`:

```
// A simple web server using Java's built-in HttpServer

// Examples from https://dzone.com/articles/simple-http-server-in-java were useful references

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

interface URLHandler {
    String handleRequest(URI url) throws IOException;
}

class ServerHttpHandler implements HttpHandler {
    URLHandler handler;
    ServerHttpHandler(URLHandler handler) {
      this.handler = handler;
    }
    public void handle(final HttpExchange exchange) throws IOException {
        // form return body after being handled by program
        try {
            String ret = handler.handleRequest(exchange.getRequestURI());
            // form the return string and write it on the browser
            exchange.sendResponseHeaders(200, ret.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(ret.getBytes());
            os.close();
        } catch(Exception e) {
            String response = e.toString();
            exchange.sendResponseHeaders(500, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}

public class Server {
    public static void start(int port, URLHandler handler) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        //create request entrypoint
        server.createContext("/", new ServerHttpHandler(handler));

        //start the server
        server.start();
        System.out.println("Server Started! Visit http://localhost:" + port + " to visit.");
    }
}
```

Content of `TestListExamples.java`:

```
import static org.junit.Assert.*;
import org.junit.*;
import java.util.Arrays;
import java.util.List;

class IsMoon implements StringChecker {
  public boolean checkString(String s) {
    return s.equalsIgnoreCase("moon");
  }
}

public class TestListExamples {
  @Test
  public void testFilter() {
   List<String> inputList = Arrays.asList("apple", "banana", "cherry");
   StringChecker sc = s -> s.contains("a"); 

   List<String> actual = ListExamples.filter(inputList, sc);
   List<String> expected = Arrays.asList("apple", "banana");
   
   assertEquals(expected, actual);
  }

  @Test(timeout = 500)
  public void testMergeRightEnd() {
    List<String> left = Arrays.asList("a", "b", "c");
    List<String> right = Arrays.asList("a", "d");
    List<String> merged = ListExamples.merge(left, right);
    List<String> expected = Arrays.asList("a", "a", "b", "c", "d");
    assertEquals(expected, merged);
  }

  @Test
  public void testMerge2() {
    List<String> left = Arrays.asList("a", "b", "c");
    List<String> right = Arrays.asList("f", "d");
    List<String> merged = ListExamples.merge(left, right);
    List<String> expected = Arrays.asList("a", "b", "c", "f", "d");
    assertEquals(expected, merged);
  }

  @Test
  public void testMerge3() {
    List<String> left = Arrays.asList("a", "b");
    List<String> right = Arrays.asList("a", "b");
    List<String> merged = ListExamples.merge(left, right);
    List<String> expected = Arrays.asList("a", "a", "b", "b");
    assertEquals(expected, merged);
  }

}
```

Content of `ListExamples.java` in `student-submission` directory:

```
import java.util.ArrayList;
import java.util.List;

interface StringChecker { boolean checkString(String s); }

class ListExamples {

  // Returns a new list that has all the elements of the input list for which
  // the StringChecker returns true, and not the elements that return false, in
  // the same order they appeared in the input list;
  static List<String> filter(List<String> list, StringChecker sc) {
    List<String> result = new ArrayList<>();
    for(String s: list) {
      if(sc.checkString(s)) {
        result.add(s);
      }
    }
    return result;
  }


  // Takes two sorted list of strings (so "a" appears before "b" and so on),
  // and return a new list that has all the strings in both list in sorted order.
  static List<String> merge(List<String> list1, List<String> list2) {
    List<String> result = new ArrayList<>();
    int index1 = 0, index2 = 0;
    while(index1 < list1.size() && index2 < list2.size()) {
      if(list1.get(index1).compareTo(list2.get(index2)) < 0) {
        result.add(list1.get(index1));
        index1 += 1;
      }
      else {
        result.add(list2.get(index2));
        index2 += 1;
      }
    }
    while(index1 < list1.size()) {
      result.add(list1.get(index1));
      index1 += 1;
    }
    while(index2 < list2.size()) {
      result.add(list2.get(index2));
      index2 += 1;
    }
    return result;
  }


}
```

Content of `TestListExamples.java` in `grading-area` directory:

```
import static org.junit.Assert.*;
import org.junit.*;
import java.util.Arrays;
import java.util.List;

class IsMoon implements StringChecker {
  public boolean checkString(String s) {
    return s.equalsIgnoreCase("moon");
  }
}




public class TestListExamples {
  @Test
  public void testFilter() {
   List<String> inputList = Arrays.asList("apple", "banana", "cherry");
   StringChecker sc = s -> s.contains("a"); 

   List<String> actual = ListExamples.filter(inputList, sc);
   List<String> expected = Arrays.asList("apple", "banana");
   
   assertEquals(expected, actual);
  }

  @Test(timeout = 500)
  public void testMergeRightEnd() {
    List<String> left = Arrays.asList("a", "b", "c");
    List<String> right = Arrays.asList("a", "d");
    List<String> merged = ListExamples.merge(left, right);
    List<String> expected = Arrays.asList("a", "a", "b", "c", "d");
    assertEquals(expected, merged);
  }


  @Test
  public void testMerge2() {
    List<String> left = Arrays.asList("a", "b", "c");
    List<String> right = Arrays.asList("f", "d");
    List<String> merged = ListExamples.merge(left, right);
    List<String> expected = Arrays.asList("a", "b", "c", "f", "d");
    assertEquals(expected, merged);
  }

  @Test
  public void testMerge3() {
    List<String> left = Arrays.asList("a", "b");
    List<String> right = Arrays.asList("a", "b");
    List<String> merged = ListExamples.merge(left, right);
    List<String> expected = Arrays.asList("a", "a", "b", "b");
    assertEquals(expected, merged);
  }

}
```

Content of `ListExamples.java` in `pa1` directory:

```
import java.util.ArrayList;
import java.util.List;

interface StringChecker { boolean checkString(String s); }

class ListExamples {

  // Returns a new list that has all the elements of the input list for which
  // the StringChecker returns true, and not the elements that return false, in
  // the same order they appeared in the input list;
  static List<String> filter(List<String> list, StringChecker sc) {
    List<String> result = new ArrayList<>();
    for(String s: list) {
      if(sc.checkString(s)) {
        result.add(s);
      }
    }
    return result;
  }


  // Takes two sorted list of strings (so "a" appears before "b" and so on),
  // and return a new list that has all the strings in both list in sorted order.
  static List<String> merge(List<String> list1, List<String> list2) {
    List<String> result = new ArrayList<>();
    int index1 = 0, index2 = 0;
    while(index1 < list1.size() && index2 < list2.size()) {
      if(list1.get(index1).compareTo(list2.get(index2)) < 0) {
        result.add(list1.get(index1));
        index1 += 1;
      }
      else {
        result.add(list2.get(index2));
        index2 += 1;
      }
    }
    while(index1 < list1.size()) {
      result.add(list1.get(index1));
      index1 += 1;
    }
    while(index2 < list2.size()) {
      result.add(list2.get(index2));
      index2 += 1;
    }
    return result;
  }


}
```

Command line:

```
bash grade.sh https://github.com/ucsd-cse15l-f22/list-methods-nested.git
```

![Image](Image/Bug.png)

![Image](Image/link.png)

TA: Hello! Thank you for the screenshot of the symptoms and the code block for contents in each file. Since the output shown it finished cloning but yet `cp: student-submission/*.java: No such file or directory` and `Missing ListExamples.java in student submission`, this might be because the files are not cloned properly. The `ListExamples.java` in `student-submission` directory looks exactly the same as the code from the GitHub link. From screenshot for file & directory structure in the GitHub link, seems like you are testing the Sample Submission for implementation saved in a nested directory. So, you are thinking in the right direction! Do you remember what does line 18 `cp student-submission/*.java grading-area` in the bash script do? Does it going to go inside the nester directory and copy the code for bash script? If not, think of what commands should you use here. (Hint: what command can recursively traverse the path and list all the files in the directory) 


Student: Thank you for the reply! I changed my line 18 from `cp student-submission/*.java grading-area` to `find student-submission -name "ListExamples.java" -exec cp {} grading-area \;` and now it passes all the test! The bug is really because of my bash script did not recursively traverse the nested directory and obtain all files inside.

New content of `grade.sh`:

```
CPATH='.:lib/hamcrest-core-1.3.jar:lib/junit-4.13.2.jar'

rm -rf student-submission
rm -rf grading-area

mkdir grading-area

git clone $1 student-submission
echo 'Finished cloning'


# Draw a picture/take notes on the directory structure that's set up after
# getting to this point

# Then, add here code to compile and run, and do any post-processing of the
# tests

find student-submission -name "ListExamples.java" -exec cp {} grading-area \;
cp TestListExamples.java grading-area 
cp -r lib grading-area

cd grading-area


if ! [ -f ListExamples.java ]
then 
    echo "Missing ListExamples.java in student submission"
    echo "Score: 0"
    exit
fi

javac -cp $CPATH *.java &> compile.txt 
if [ $? -ne 0 ]
then
    echo "Compilation Error"
    echo "Score: 0"
    exit
fi

java -cp $CPATH org.junit.runner.JUnitCore TestListExamples > outputResults 2>&1

SUCCESS=$(grep "OK" outputResults)

if [[ -n $SUCCESS ]]
then 
    echo "Everything passed! Your score is 100%"
else
    totalTests=$(grep -oE "Tests run: [0-9]+" outputResults | grep -oE "[0-9]+")
    Failures=$(grep -oE "Failures: [0-9]+" outputResults | grep -oE "[0-9]+")
    Success=$((totalTests - Failures))
    Percentage=$(echo "scale=2; $Success * 100 / $totalTests" | bc)
    echo "Score: $Percentage%"
fi
```

Command line:

```
bash grade.sh https://github.com/ucsd-cse15l-f22/list-methods-nested.git
```

![Image](Image/fix.png)

## Part 2 – Reflection

In the second half of this quarter, I think `bash` and `vim` is 2 very cool things that I have learn in CSE 15L. The bash scripts makes me understand more how autograder work and vim allows me to view files in terminal easier. `grep` and `find` are two very useful commands I learned. Also when doing my practice skill demo, I am able to understand in what situation `mkdir -p`, `touch`, `rm` and `mv` can be used. `mv` and `rm` is especially useful when I am only using my terminal and type or create the wrong file and would like to rename it or delete it. When doing my skill demo, I also realize despite I learned and able to do most of the commands, I am still a little unfamiliar on how to apply them, especially for the new tasks that appear in the real skill demo test. This encourage me to review my notes and make sure I understand all the concepts such that I can improve my coding skills.
