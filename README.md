# Fitness Lesson Booking System (FLC)

This project is a Java application for managing fitness lessons, bookings, reviews, and reports.

## Project Structure

- `src/flc` - Application source code
- `test/flc` - JUnit test code
- `lib` - External libraries (JUnit jar)
- `out/production` - Compiled application classes
- `out/test` - Compiled test classes

## Prerequisites

- Java JDK installed (`javac`, `java`, `jar` available in terminal)
- PowerShell terminal (commands below are for Windows PowerShell)

## Compile Application

From project root (`FLC\FLC`):

```powershell
javac -d out/production src/flc/*.java
```

## Run Application (GUI)

```powershell
java -cp out/production flc.MainGUI
```

## Compile Tests

```powershell
javac -cp "out/production;lib/junit-platform-console-standalone-1.10.0.jar" -d out/test test/flc/BookingSystemTest.java
```

## Run Tests (JUnit 5)

```powershell
java -jar lib/junit-platform-console-standalone-1.10.0.jar execute --class-path "out/production;out/test" --scan-class-path
```

## Create Runnable JAR

```powershell
jar cfe FLC.jar flc.MainGUI -C out/production .
```

## Run Runnable JAR

```powershell
java -jar FLC.jar
```

## Full Build + Test Sequence

```powershell
javac -d out/production src/flc/*.java
javac -cp "out/production;lib/junit-platform-console-standalone-1.10.0.jar" -d out/test test/flc/BookingSystemTest.java
java -jar lib/junit-platform-console-standalone-1.10.0.jar execute --class-path "out/production;out/test" --scan-class-path
```
