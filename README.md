# Anonymous Alchemists - Software Design Project

## 1. Project Description
Exam project for the **Software Engineering** course.
A Java implementation of the domain logic for the board game **"Alchemists"** by Matúš Kotry (CGE).

**Project Goal:** To design a flexible architecture capable of managing complex game rules and deduction logic using **GRASP** principles and **Unified Process (UP)**.

* **Rulebook:** [rules.txt](./rules.txt) or [Official PDF](https://cdn.1j1ju.com/medias/ef/97/2b-alchemists-rulebook.pdf)

## 2. Team Members
--

## 3. Project Structure
This repository follows a UP-style structure. The current active code lives in `00/` and is organized as a minimal Java domain layer that maps the conceptual model and SSDs (start/ conduct/ pay/ cancel experiment).

```text
Anonymous-Alchemists/
├── 00/
│   ├── docs/
│   └── src/
│       └── main/
│           └── java/
│               └── it/univaq/alchimisti_anonimi/
│                   ├── application/   (ExperimentHandler + UC orchestration)
│                   ├── domain/        (Entities/VOs: Experiment, Player, etc.)
│                   ├── game/          (GameEngine access to shared state)
│                   └── services/      (AlchemicAlgorithm)
├── diagrams/
├── rules.txt
└── README.md
```

## 4. Key Entry Points
- `it.univaq.alchimisti_anonimi.application.ExperimentHandler`
  - `startExperiment(target)`
  - `payGold()`
  - `conductExperiment(ingredientId1, ingredientId2)`
  - `cancelExperiment()`
