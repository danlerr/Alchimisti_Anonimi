# Anonymous Alchemists - Software Design Project

## 1. Project Description
Exam project for the **Software Engineering** course.
A Java implementation of the domain logic for the board game **"Alchemists"** by Matúš Kotry (CGE).

**Goal:** model the core game rules and experiment flow using **GRASP** principles, based on the conceptual model and SSDs (start/ pay/ conduct/ cancel experiment).

* **Rulebook:** [rules.txt](./rules.txt) or [Official PDF](https://cdn.1j1ju.com/medias/ef/97/2b-alchemists-rulebook.pdf)

## 2. Team Members
--

## 3. Project Structure
The current code is a minimal Java domain layer. Packages are split by responsibility and the `GameEngine` coordinates the domain on behalf of the controller.

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

## 4. Key Packages
- `alchgame.controller`
  - Use-case controller that forwards requests to `GameEngine`.
- `alchgame.model`
  - Core entities like `Experiment`, `Player`, `Student`, `PrivateLaboratory`, `PublicPlayerBoard`.
- `alchgame.application`
  - Application flow and logic: `GameEngine`, `AlchemicAlgorithm`, response DTOs.

## 5. Main Use-Case Flow (SSD)
- `ExperimentHandler.startExperiment(target)`
- If payment required: `ExperimentHandler.payGold()`
- `ExperimentHandler.conductExperiment(ingredientId1, ingredientId2)`
- Alternative path: `ExperimentHandler.cancelExperiment()`
