# Anonymous Alchemists - Software Design Project

## 1. Project Description
Exam project for the **Software Engineering** course.
A Java implementation of the domain logic for the board game **"Alchemists"** by Matúš Kotry (CGE).

**Goal:** model the core game rules and experiment flow using **GRASP** principles, based on the conceptual model and SSDs (start/ pay/ conduct/ cancel experiment).

* **Rulebook:** [rules.txt](./rules.txt) or [Official PDF](https://cdn.1j1ju.com/medias/ef/97/2b-alchemists-rulebook.pdf)

## 2. Team Members
| Nome | GitHub |
| :--- | :--- |
| **Daniele Antonucci** | [@danlerr](https://github.com/danlerr) |
| **Leonardo Pastorelli** | [@SonoLeon](https://github.com/SonoLeon) |
| **Anastasia Di Francesco** | [@AnastasiaDif](https://github.com/AnastasiaDif) |
| **Federico Palmerini** | [@federicopalme02](https://github.com/federicopalme02) |


## 3. Project Structure

```text
Anonymous-Alchemists/
├── src/
│   └── main/
│       └── java/
│           └── alchgame/
│               ├── App.java                (optional entry point)
│               ├── controller/             (use-case handlers)
│               ├── model/                  (domain entities / value objects)
│               └── application/            (domain coordination + services)
├── rules.txt
└── README.md
```

