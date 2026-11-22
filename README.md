# Java-Based Library Book Management System

A polished Java Swing desktop application for managing library operations with role-based access control.

## Features

- **Authentication**: Sign In / Sign Up with role-based access (Admin/Librarian and Student)
- **Admin Panel**: Dashboard, Books CRUD, Issue/Return, Members, Overdue Tracking, Transaction History
- **Student Panel**: Browse Books, Reserve/Cancel, My Books, My History
- **Modern UI**: Flat design with dark navy sidebar, accent blue, styled tables and cards
- **Persistence**: File-based storage (CSV) in `data/` folder
- **Security**: SHA-256 password hashing, input validation

## Default Login Credentials

| Role    | Email                      | Password    |
|---------|----------------------------|-------------|
| Admin   | usman.admin@library.com    | Admin@123   |
| Student | ali@gmail.com              | Student@123 |

## Compile and Run

### Windows
```
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
java -cp out library.Main
```

### Linux / Mac
```
javac -d out $(find src -name "*.java")
java -cp out library.Main
```

## Generate Javadoc
```
javadoc -d docs/javadoc -sourcepath src -subpackages library -author -private
```

## Run Tests
### Windows
```
dir /s /b src\*.java test\*.java > sources.txt
javac -d out @sources.txt
java -cp out library.LibraryServiceTest
```

## Project Structure

```
SC-OEL/
├── src/
│   └── library/
│       ├── model/        # Data models (Book, Member, User, Loan, etc.)
│       ├── exception/    # Custom exceptions
│       ├── service/      # Business logic services
│       ├── storage/      # File persistence
│       ├── ui/           # Swing GUI components
│       └── Main.java     # Application entry point
├── test/
│   └── library/          # Test cases
├── docs/                 # Documentation and test results
├── data/                 # Runtime data files (gitignored)
└── README.md
```

## Author

**Usman** - Open-Ended Lab Project

## Requirements

- JDK 17 or higher
- No external libraries required
