# 🧠 Workflow Orchestration System (ResilientFlow)

> A complete JavaFX-based visual orchestration system for machine learning and decision workflows, designed with solid Object-Oriented Programming (OOP) principles, modular architecture, and a visually interactive GUI.

---

## 📌 Overview

**ResilientFlow** is a powerful and extensible desktop application developed in Java using JavaFX. It allows users to **visually create, connect, and execute modular workflow nodes** representing tasks in a machine learning pipeline or decision system.

This system was developed as a **final OOP project** and showcases clean software architecture, dynamic UI, extensible components, and real-time workflow execution with meaningful logic and logging.

---

## 🎯 Key Features

- ✅ **Graphical Workflow Canvas** with zoom, pan, drag, and grid alignment
- 🎨 **Node Types with Dynamic Styling**:
  - `TASK`, `CONDITION`, `START`, `END`
  - ML-Specific: `TRAINING`, `TESTING`, `INFERENCE`, `EVALUATION`, `CLUSTERING`, `FEATURE_ENGINEERING`, etc.
- 🔁 **Undo / Redo Functionality** using the Command Pattern
- 🧩 **Condition Node Logic** with YES/NO branches
- 🛠️ **Editable Sidebar Panel** with live updates for:
  - Node name
  - Node type
  - Details description
  - Execution status
- 💾 **Save / Load Workflows** in JSON format
- 🚀 **Node Execution Engine**:
  - Simulates execution and logs activity in real-time
  - Animates node highlight during execution
- 📦 **Modular OOP Design** using:
  - Interfaces (`Executable`, `Describable`)
  - Abstract classes (`WorkflowNode`, `ExecutableNode<T>`)
  - Factory pattern (`NodeFactory`)
  - Command pattern (`UndoableAction`)
- 📘 **Final Project PDF Report** included

---


---

## 📂 Project Structure

```bash
.
├── src/
│   ├── main/java/com/farid/workfloworchestration/
│   │   ├── model/              # Node classes (TaskNode, ConditionNode, etc.)
│   │   ├── view/               # JavaFX UI components (FXML, MainView)
│   │   ├── controller/         # MainViewController, MainController
│   │   ├── service/            # Execution logic, validation
│   │   ├── command/            # Undo/Redo logic
│   │   ├── factory/            # NodeFactory (OOP design pattern)
│   │   ├── exception/          # Custom exceptions
│   │   ├── util/               # Metadata printer, helpers
│   └── resources/
│       ├── node-view.fxml      # Node UI template
│       ├── main-view.fxml      # Application layout
│       └── style.css           # Visual styles
├── pom.xml                     # Maven project file
└── WorkflowProject_Report.pdf  # Final project documentation

---

## ⚙️ Technologies Used

| Category         | Technologies                               |
|------------------|--------------------------------------------|
| Language         | Java 17                                    |
| GUI Framework    | JavaFX (FXML, Scene Builder)               |
| Build Tool       | Maven                                      |
| Data Format      | JSON (save/load workflows)                 |
| Design Patterns  | Factory, Command, Inheritance, Interface   |
| IDE Recommended  | IntelliJ IDEA                              |

---

## 📄 Project Report

The full academic documentation of this project — including architecture diagrams, OOP principle coverage, class analysis, and execution design — is available in the repository:

📎 **[WorkflowProject_Report.pdf](WorkflowProject_Report.pdf)**  
_A detailed walkthrough of the system for professors, peers, and reviewers._

---

## 👨‍💻 Author

**Farid Nowrouzi**  
Bachelor’s Degree in Computer Science  
University of Messina  
Final Year Object-Oriented Programming Project

📌 _Designed, implemented, and documented with focus on clean architecture and future extensibility._

---

## 🚀 Future Extensions

Here are some of the ideas planned for future versions of the system:

- 📦 **Microservices Integration**  
  Enable each node (e.g., `TRAINING`, `INFERENCE`) to act as a deployable service.

- 📊 **Live Performance Visualization**  
  Display execution times, logs, and ML metrics visually.

- ☁️ **Cloud Deployment Ready**  
  Extend workflow export for Kubernetes or Docker-based ML platforms.

- 🧩 **Custom Node Plugin System**  
  Allow external devs to create and import new node types.

- 📈 **Versioned Workflow Snapshots**  
  Track workflow evolution over time.

---

## 📬 Contact

For any feedback, questions, or collaboration:

**📧 Email:** _available through university systems_  
**📁 GitHub:** [github.com/Farid-Nowrouzi](https://github.com/Farid-Nowrouzi)

_You may also leave a message via Issues tab in this repository._

---

> ⭐ *Thank you for reviewing this project. Your feedback is highly appreciated.*

