# Smart Campus Platform

My Core Java capstone. Instead of finishing with a certificate, I merged everything I learned into one small campus system: students, staff, payments, shuttles, help-desk tickets and council-election voting.

It is a single Java file plus a text file, with no frameworks and no build tools.

## Run it

You need a JDK (Java 8 or newer; I tested on a recent JDK).

```bash
javac SmartCampusPlatform.java
java SmartCampusPlatform
```

Run both commands from the folder that contains `notices.txt`. The program reads that file by relative path, so running it from somewhere else will print "Could not read file" for the file demo.

## What's inside

| Area | What it shows | Where to look |
|---|---|---|
| **People** | Encapsulation, inheritance, constructor overloading and chaining, `super` | `Person`, `Student`, `Staff` |
| **Services** | Interface vs abstract class, polymorphism, method overloading | `PaymentMethod`, `CampusVehicle`, `FeeCalculator` |
| **Exception handling** | Custom checked exceptions, multi-catch, `finally`, try-with-resources on a real file, `throws` | `InvalidAgeException`, `exceptionDemo()` |
| **Collections** | ArrayList, Stack, Queue, Deque, PriorityQueue (min and max heap), HashSet, TreeSet, HashMap, TreeMap | `collectionsDemo()` |

### Why each collection

| Collection | Used for |
|---|---|
| `ArrayList` | Quiz scores, with access by index |
| `Stack` | Back-button history in the student portal |
| `Queue` (`LinkedList`) | Help-desk tickets, first come first served |
| `Deque` (`ArrayDeque`) | Cafeteria line where the Dean can jump to the front |
| `PriorityQueue` (min) | Deadlines, where the smallest number is the most urgent |
| `PriorityQueue` (max) | CGPA leaderboard, highest first |
| `HashSet` | Hackathon attendees, with duplicates ignored |
| `TreeSet` | Roll numbers, always sorted |
| `HashMap` | Find a student by roll number without looping |
| `TreeMap` | Course catalog, printed alphabetically |

## Sample output

```
--- 3. Exception handling ---
  Soham is eligible to vote
  Diya is eligible to vote
  Junior is 16, must be 18+ to vote in the council election

  Multi-catch: ArrayIndexOutOfBoundsException -> Index 5 out of bounds for length 3
  finally ran for scenario 0
  Multi-catch: ArithmeticException -> / by zero
  finally ran for scenario 1

  Reading notices.txt
    > Exams start Monday
    > Library open till 10 PM
    > Hackathon registrations are open
  Reading missing.txt
  Could not read file: missing.txt (No such file or directory)
```

The exact text of the "Index 5 out of bounds" message depends on your Java version.

## Two bugs that taught me the most

1. **Naming a class `Exception`.** In an early version I named one of my own classes `Exception`. It hid the real `java.lang.Exception`, and the compiler failed with *"Exception cannot be converted to Throwable"*. Sorting it out meant learning how Java resolves names across packages. Now my custom exceptions say `extends java.lang.Exception` explicitly.
2. **`list.remove(3)` vs `list.remove(Integer.valueOf(3))`.** On a `List<Integer>`, the first removes the element at index 3. The second removes the value 3. The `ArrayList` section of the demo shows both.

## Project layout

```
.
├── SmartCampusPlatform.java   # all the code (main class + supporting classes)
├── notices.txt                # sample file read in the try-with-resources demo
└── README.md
```

## Ideas for next steps

- Split the single file into packages (`model`, `service`, `util`).
- Replace the console output with unit tests (JUnit).
- Store students and wallets in a database instead of in memory.
- Add a simple UI or REST API on top.

## About

Built by Soham as the final project of my Core Java learning journey. Feedback and suggestions are welcome.
