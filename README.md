# Citizen Registry System – Java Implementation

This repository contains a citizen registry system implemented in Java, developed as part of a programming assignment for the course **"Development in Amazon Cloud"**. The system supports citizen record management, including serialization to file and optional integration with a relational database.

---

## 🎯 Features

- Add, delete, update, and search citizen records
- In-memory registry with **serialization/deserialization**
- Bonus: Database-backed registry with **auto DB/table creation**
- Full validation and error messaging

---

## 🧩 Data Model

Each citizen record includes:

- **ID (ΑΤ)**: 8-character unique identifier *(required)*
- **First Name**, **Last Name**, **Gender**, **Birth Date** *(required)*
- **Tax ID (ΑΦΜ)**: 9-digit optional, must be unique if provided
- **Address**: optional

---

## 🧱 Project Structure

```
├── src/                         → Java source code
│   ├── Application.java         → Main application loop  
│   ├── Citizen.java             → POJO for citizen entity  
│   ├── Registry.java            → Registry management with  
│   └── DatabaseManager.java     → Database I/O logic (bonus)  
│
├── sql/
│   └── sql_queries_first_exercise_bonus.sql   → All DDL/DML   
│  
└── README.md
```

---

## 💾 Serialization

After each add/delete/update operation, the in-memory registry is serialized to a `.ser` file. At startup, it is deserialized to recover the previous state.

- The file path can be:
  - passed via command-line argument
  - or defaulted (e.g., `data/citizens.ser`)

---

## 🛢️ Bonus: Database Integration

The application supports persisting citizen records to a relational database.

- `DatabaseManager.java` handles:
  - Establishing connection
  - Auto-creating the database and table if they don’t exist
  - Performing CRUD operations

**SQL schema:**

```sql
CREATE TABLE Citizens (
    id VARCHAR(8) PRIMARY KEY,
    firstName NVARCHAR(50) NOT NULL,
    lastName NVARCHAR(50) NOT NULL,
    gender NVARCHAR(6) NOT NULL,
    birthDate DATE NOT NULL,
    taxId VARCHAR(9) UNIQUE NULL,
    address NVARCHAR(100) NULL,
    CONSTRAINT check_id_length CHECK (LEN(id) = 8),
    CONSTRAINT check_taxId_length CHECK (LEN(taxId) = 9 OR taxId IS NULL),
    CONSTRAINT check_gender_value CHECK (LOWER(gender) IN ('male', 'female'))
);
```

---

## ✅ Execution

Compile and run via command line or any IDE:

```bash
javac -d bin src/*.java
java -cp bin Application data/citizens.ser
```

---

## 📄 License

Distributed under the [MIT License](LICENSE).
