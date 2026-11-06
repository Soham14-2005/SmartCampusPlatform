import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

/*
 * Smart Campus Platform
 * My Core Java capstone: everything I learned, in one project.
 *
 * Run it from the folder that contains notices.txt:
 *   javac SmartCampusPlatform.java
 *   java SmartCampusPlatform
 */
public class SmartCampusPlatform {

    public static void main(String[] args) {
        System.out.println("===== SMART CAMPUS PLATFORM =====");

        List<Student> roster = peopleDemo();
        servicesDemo();
        exceptionDemo(roster);
        collectionsDemo(roster);

        System.out.println("\n===== DONE =====");
    }

    // ---------- 1. People: encapsulation, inheritance, constructors ----------

    static List<Student> peopleDemo() {
        System.out.println("\n--- 1. People ---");
        List<Student> roster = new ArrayList<>();

        try {
            Student soham = new Student("Soham", 21, "Computer Engineering");
            Student aarav = new Student("Aarav", 20, "Information Technology");
            Student meera = new Student("Meera", 22, "Mechanical Engineering");
            Student diya = new Student("Diya", 19, "Electronics and Telecom");
            Student kabir = new Student("Kabir", 21); // no course given, so it falls back to "Undeclared"

            soham.setCgpa(9.1);
            aarav.setCgpa(8.4);
            meera.setCgpa(8.8);
            diya.setCgpa(9.4);
            kabir.setCgpa(7.9);
            Collections.addAll(roster, soham, aarav, meera, diya, kabir);

            Staff unknown = new Staff();
            Staff junior = new Staff("Ms. Iyer", 29);
            Staff professor = new Staff("Prof. Rao", 45, 85000);

            // one list of Person, each object prints itself its own way
            List<Person> everyone = new ArrayList<>(roster);
            Collections.addAll(everyone, unknown, junior, professor);
            for (Person p : everyone) {
                System.out.println("  " + p.describe());
            }

            System.out.println("\n  Setting an impossible age on Soham...");
            soham.setAge(-5);
        } catch (InvalidAgeException e) {
            System.out.println("  Rejected: " + e.getMessage());
        }
        return roster;
    }

    // ---------- 2. Services: interface, abstract class, overloading ----------

    static void servicesDemo() {
        System.out.println("\n--- 2. Services ---");

        CampusWallet wallet = new CampusWallet(1001, 5000);
        wallet.deposit(2000);
        System.out.println("  Wallet balance after top-up: Rs." + wallet.getBalance());

        List<PaymentMethod> methods = new ArrayList<>();
        methods.add(new UpiPayment("soham@upi"));
        methods.add(new CardPayment("4242"));
        methods.add(new WalletPayment(wallet));
        for (PaymentMethod m : methods) {
            try {
                m.pay(1500);
            } catch (InsufficientBalanceException e) {
                System.out.println("  Payment failed: " + e.getMessage());
            }
        }

        try {
            new WalletPayment(wallet).pay(99999); // too big on purpose
        } catch (InsufficientBalanceException e) {
            System.out.println("  Payment failed: " + e.getMessage());
        }

        List<CampusVehicle> fleet = new ArrayList<>();
        fleet.add(new ShuttleBus("MH-04-1234"));
        fleet.add(new EScooter("SC-007"));
        for (CampusVehicle v : fleet) {
            v.start();
            v.stop();
        }

        CampusAnimal gateDog = new GuardDog();
        gateDog.makeSound();

        System.out.println("  Fee total (int, int)       = " + FeeCalculator.total(10, 20));
        System.out.println("  Fee total (int, int, int)  = " + FeeCalculator.total(10, 20, 30));
        System.out.println("  Fee total (double, double) = " + FeeCalculator.total(10.5, 20.25));
    }

    // ---------- 3. Exception handling ----------

    static void exceptionDemo(List<Student> roster) {
        System.out.println("\n--- 3. Exception handling ---");

        // council election: Soham and Diya are 18+, Junior is 16
        Student junior = null;
        try {
            junior = new Student("Junior", 16);
        } catch (InvalidAgeException e) {
            System.out.println("  Could not create Junior: " + e.getMessage());
        }
        for (Person voter : Arrays.asList(roster.get(0), roster.get(3), junior)) {
            try {
                checkVoter(voter);
            } catch (InvalidAgeException e) {
                System.out.println("  " + e.getMessage());
            }
        }

        System.out.println();
        multiCatchDemo(0);
        multiCatchDemo(1);

        System.out.println();
        readNotices("notices.txt");
        readNotices("missing.txt"); // doesn't exist, so we land in the catch block

        System.out.println();
        try {
            fetchGrades();
        } catch (Exception e) {
            System.out.println("  Caught from fetchGrades(): " + e);
        }
    }

    // throws: this method won't handle the problem, whoever calls it has to
    static void checkVoter(Person p) throws InvalidAgeException {
        if (p.getAge() < 18) {
            throw new InvalidAgeException(p.getName() + " is " + p.getAge()
                    + ", must be 18+ to vote in the council election");
        }
        System.out.println("  " + p.getName() + " is eligible to vote");
    }

    static void multiCatchDemo(int scenario) {
        try {
            int[] marks = {1, 2, 3};
            if (scenario == 0) {
                System.out.println(marks[5]);
            } else {
                System.out.println(10 / (marks[0] - 1));
            }
        } catch (ArithmeticException | ArrayIndexOutOfBoundsException e) {
            System.out.println("  Multi-catch: " + e.getClass().getSimpleName() + " -> " + e.getMessage());
        } finally {
            System.out.println("  finally ran for scenario " + scenario);
        }
    }

    // try-with-resources: no close() calls, Java closes both readers for us
    static void readNotices(String path) {
        System.out.println("  Reading " + path);
        try (FileReader fr = new FileReader(path);
             BufferedReader br = new BufferedReader(fr)) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("    > " + line);
            }
        } catch (IOException e) {
            System.out.println("  Could not read file: " + e.getMessage());
        }
    }

    static void fetchGrades() throws Exception {
        throw new Exception("Grade service is offline");
    }

    // ---------- 4. Collections ----------

    static void collectionsDemo(List<Student> roster) {
        System.out.println("\n--- 4. Collections ---");

        // ArrayList: quiz scores, I need get() by index
        System.out.println("\n  ArrayList (quiz scores)");
        List<Integer> scores = new ArrayList<>(Arrays.asList(30, 10, 40, 20, 10));
        System.out.println("    scores           = " + scores);
        System.out.println("    get(2)           = " + scores.get(2));
        scores.remove(3); // int means index, so this removes the 20
        System.out.println("    remove(index 3)  = " + scores);
        scores.remove(Integer.valueOf(10)); // Integer means value, so this removes the first 10
        System.out.println("    remove(value 10) = " + scores);
        scores.add(25);
        Collections.sort(scores);
        System.out.println("    sorted           = " + scores);
        System.out.println("    max / min        = " + Collections.max(scores) + " / " + Collections.min(scores));
        System.out.println("    contains(40)     = " + scores.contains(40));

        // removing inside a for-each throws ConcurrentModificationException, so use the iterator
        Iterator<Integer> it = scores.iterator();
        while (it.hasNext()) {
            if (it.next() < 26) {
                it.remove();
            }
        }
        System.out.println("    scores >= 26     = " + scores);

        // Stack: Back button history, last page opened is the first one closed
        System.out.println("\n  Stack (portal Back button)");
        Stack<String> history = new Stack<>();
        history.push("Home");
        history.push("Courses");
        history.push("Fees");
        System.out.println("    history          = " + history);
        System.out.println("    current page     = " + history.peek());
        history.pop();
        System.out.println("    after Back       = " + history);

        // Queue: help-desk tickets, first come first served
        System.out.println("\n  Queue (help-desk tickets)");
        Queue<String> tickets = new LinkedList<>();
        tickets.add("T1: WiFi down");
        tickets.offer("T2: Password reset");
        tickets.offer("T3: Projector issue");
        tickets.add("T4: Library card");
        System.out.println("    queue            = " + tickets);
        System.out.println("    remove()         = " + tickets.remove());
        System.out.println("    poll()           = " + tickets.poll());
        System.out.println("    peek()           = " + tickets.peek());
        System.out.println("    offer(T5)        = " + tickets.offer("T5: Locker key"));
        System.out.println("    queue            = " + tickets);

        // Deque: cafeteria line where the Dean can jump to the front
        System.out.println("\n  Deque (cafeteria line)");
        Deque<String> line = new ArrayDeque<>();
        line.offer("Ananya");
        line.offer("Rohan");
        line.offerLast("Isha");
        line.pollLast(); // Isha leaves the line
        line.offerFirst("Dean (priority)");
        System.out.println("    line             = " + line);

        // PriorityQueue: min-heap for deadlines (smallest = most urgent)
        System.out.println("\n  PriorityQueue");
        PriorityQueue<Integer> daysLeft = new PriorityQueue<>();
        Collections.addAll(daysLeft, 12, 3, 4, 2);
        System.out.println("    min-heap peek    = " + daysLeft.peek() + " days left");
        System.out.println("    poll, poll       = " + daysLeft.poll() + ", " + daysLeft.poll());

        // max-heap is the same queue with a reversed comparator
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
        Collections.addAll(maxHeap, 12, 3, 4, 2);
        System.out.println("    max-heap peek    = " + maxHeap.peek());

        // CGPA leaderboard, highest first
        PriorityQueue<Student> leaderboard =
                new PriorityQueue<>(Comparator.comparingDouble(Student::getCgpa).reversed());
        leaderboard.addAll(roster);
        System.out.println("    Top 3 by CGPA:");
        for (int rank = 1; rank <= 3; rank++) {
            Student s = leaderboard.poll();
            System.out.printf("      #%d %-6s %.1f%n", rank, s.getName(), s.getCgpa());
        }

        // HashSet: hackathon attendees, duplicates ignored
        System.out.println("\n  HashSet (hackathon attendees)");
        Set<String> attendees = new HashSet<>();
        attendees.add("Soham");
        attendees.add("Diya");
        System.out.println("    add Soham again  = " + attendees.add("Soham")); // false, already in
        System.out.println("    size             = " + attendees.size());
        System.out.println("    contains(Diya)   = " + attendees.contains("Diya"));

        // TreeSet: roll numbers come out already sorted
        System.out.println("\n  TreeSet (roll numbers)");
        TreeSet<Integer> rolls = new TreeSet<>();
        for (Student s : roster) {
            rolls.add(s.getRollNo());
        }
        rolls.add(103); // already there, ignored
        System.out.println("    rolls            = " + rolls);
        System.out.println("    first / last     = " + rolls.first() + " / " + rolls.last());
        System.out.println("    after 103        = " + rolls.higher(103));

        // HashMap: find a student by roll number without looping
        System.out.println("\n  HashMap (roll number -> student)");
        Map<Integer, Student> byRoll = new HashMap<>();
        for (Student s : roster) {
            byRoll.put(s.getRollNo(), s);
        }
        System.out.println("    get(101)         = " + byRoll.get(101).getName());
        System.out.println("    keys             = " + byRoll.keySet());
        byRoll.remove(105);
        System.out.println("    has 105 after remove? " + byRoll.containsKey(105));

        // TreeMap: course catalog printed alphabetically
        System.out.println("\n  TreeMap (course catalog)");
        TreeMap<String, Integer> credits = new TreeMap<>();
        credits.put("Operating Systems", 4);
        credits.put("Java Programming", 3);
        credits.put("Data Structures", 4);
        credits.put("Computer Networks", 3);
        System.out.println("    catalog          = " + credits);
        System.out.println("    first course     = " + credits.firstKey());
        System.out.println("    before 'J'       = " + credits.headMap("J").keySet());
        credits.replace("Java Programming", 3, 4); // only changes if the current value is 3
        credits.remove("Computer Networks");
        System.out.println("    updated          = " + credits);
    }
}

// ---------- Exceptions ----------

// I spell out java.lang.Exception on purpose. Early on I named my own class
// "Exception" and it hid the real one, which broke the whole project.
class InvalidAgeException extends java.lang.Exception {
    InvalidAgeException(String message) {
        super(message);
    }
}

class InsufficientBalanceException extends java.lang.Exception {
    InsufficientBalanceException(String message) {
        super(message);
    }
}

// ---------- People ----------

abstract class Person {
    private String name;
    private int age;

    Person() {
        this.name = "Unknown";
        this.age = 0;
    }

    Person(String name, int age) throws InvalidAgeException {
        this.name = name;
        setAge(age); // reuse the setter so age is validated in one place
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public final void setAge(int age) throws InvalidAgeException {
        if (age < 0 || age > 120) {
            throw new InvalidAgeException("Age must be between 0 and 120, got " + age);
        }
        this.age = age;
    }

    abstract String role();

    String describe() {
        return "[" + role() + "] " + name + ", age " + age;
    }
}

class Student extends Person {
    private static int nextRollNo = 101;

    private final int rollNo;
    private final String course;
    private double cgpa;

    Student(String name, int age) throws InvalidAgeException {
        this(name, age, "Undeclared");
    }

    Student(String name, int age, String course) throws InvalidAgeException {
        super(name, age);
        this.course = course;
        this.rollNo = nextRollNo++;
    }

    public int getRollNo() {
        return rollNo;
    }

    public double getCgpa() {
        return cgpa;
    }

    public void setCgpa(double cgpa) {
        if (cgpa < 0 || cgpa > 10) {
            throw new IllegalArgumentException("CGPA must be between 0 and 10");
        }
        this.cgpa = cgpa;
    }

    @Override
    String role() {
        return "Student";
    }

    @Override
    String describe() {
        return super.describe() + " | Roll " + rollNo + " | " + course + " | CGPA " + cgpa;
    }
}

class Staff extends Person {
    private int salary;

    Staff() {
        super();
        this.salary = 0;
    }

    Staff(String name, int age) throws InvalidAgeException {
        this(name, age, 0);
    }

    Staff(String name, int age, int salary) throws InvalidAgeException {
        super(name, age);
        this.salary = salary;
    }

    @Override
    String role() {
        return "Staff";
    }

    @Override
    String describe() {
        return super.describe() + " | Salary Rs." + salary;
    }
}

// ---------- Payments ----------

interface PaymentMethod {
    void pay(int amount) throws InsufficientBalanceException;
}

class UpiPayment implements PaymentMethod {
    private final String upiId;

    UpiPayment(String upiId) {
        this.upiId = upiId;
    }

    @Override
    public void pay(int amount) {
        System.out.println("  UPI payment of Rs." + amount + " done from " + upiId);
    }
}

class CardPayment implements PaymentMethod {
    private final String lastFour;

    CardPayment(String lastFour) {
        this.lastFour = lastFour;
    }

    @Override
    public void pay(int amount) {
        System.out.println("  Card payment of Rs." + amount + " done (card ending " + lastFour + ")");
    }
}

class WalletPayment implements PaymentMethod {
    private final CampusWallet wallet;

    WalletPayment(CampusWallet wallet) {
        this.wallet = wallet;
    }

    @Override
    public void pay(int amount) throws InsufficientBalanceException {
        wallet.withdraw(amount);
        System.out.println("  Wallet payment of Rs." + amount + " done, balance now Rs." + wallet.getBalance());
    }
}

class CampusWallet {
    private final int walletId;
    private int balance; // private so nothing outside can set it to a random number

    CampusWallet(int walletId, int balance) {
        this.walletId = walletId;
        this.balance = balance;
    }

    public int getBalance() {
        return balance;
    }

    public void deposit(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit must be positive");
        }
        balance += amount;
    }

    public void withdraw(int amount) throws InsufficientBalanceException {
        if (amount > balance) {
            throw new InsufficientBalanceException("Need Rs." + amount + " but wallet " + walletId
                    + " only has Rs." + balance);
        }
        balance -= amount;
    }
}

// ---------- Vehicles and animals ----------

// abstract class because every vehicle shares a plate and stop(), but starts differently
abstract class CampusVehicle {
    private final String plate;

    CampusVehicle(String plate) {
        this.plate = plate;
    }

    String getPlate() {
        return plate;
    }

    abstract void start();

    void stop() {
        System.out.println("  " + plate + " stopped");
    }
}

class ShuttleBus extends CampusVehicle {
    ShuttleBus(String plate) {
        super(plate);
    }

    @Override
    void start() {
        System.out.println("  Shuttle bus " + getPlate() + " started its campus loop");
    }
}

class EScooter extends CampusVehicle {
    EScooter(String plate) {
        super(plate);
    }

    @Override
    void start() {
        System.out.println("  E-scooter " + getPlate() + " unlocked");
    }

    @Override
    void stop() {
        super.stop();
        System.out.println("  " + getPlate() + " switched to battery-saver mode");
    }
}

class CampusAnimal {
    void makeSound() {
        System.out.println("  Animal makes a sound");
    }
}

class GuardDog extends CampusAnimal {
    @Override
    void makeSound() {
        super.makeSound();
        System.out.println("  Woof! Main gate secured");
    }
}

class FeeCalculator {
    static int total(int a, int b) {
        return a + b;
    }

    static int total(int a, int b, int c) {
        return a + b + c;
    }

    static double total(double a, double b) {
        return a + b;
    }
}
