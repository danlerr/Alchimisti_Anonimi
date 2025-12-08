# Anonymous Alchemists - Software Design Project

## 1. Project Description
Exam project for the **Software Engineering** course.
A software implementation of the domain logic for the board game **"Alchemists"** by Matúš Kotry (CGE).

**Project Goal:** To design a flexible architecture (Domain Layer) capable of managing complex game rules, alchemy deduction logic, and variable pricing strategies using **GRASP** principles and **Unified Process (UP)**.

* **Rulebook:** [rules.txt](./rules.txt) or [Official PDF](https://cdn.1j1ju.com/medias/ef/97/2b-alchemists-rulebook.pdf)

## 2. Team Members
--

## 3. Project Structure (Iterative Process)
This repository follows the Unified Process (UP) structure. Artifacts are "frozen" at the end of each phase to demonstrate the evolution of the design.

```text
Anonymous-Alchemists/
├── 00_Work_In_Progress/       <-- Current active development
│   ├── Docs/                  (Drafts of Vision, Glossary, UCs)
│   ├── UML/                   (Editable StarUML/Visual Paradigm files)
│   └── Src/                   (Java/C# Source Code)
│
├── 01_Inception_Frozen/       <-- Phase 1 Snapshot (Requirements)
│   ├── 01_Vision.pdf
│   ├── 02_Glossary.pdf
│   ├── 03_UseCase_Model_Overview.pdf  (Diagram + Briefs)
│   └── 04_UC1_Sell_Potion.pdf         (Fully Dressed Use Case)
│
├── 02_Elaboration_1_Frozen/   <-- Phase 2 Snapshot (Analysis - OOA)
│   ├── 01_Domain_Model_v1.pdf         (Conceptual Classes)
│   ├── 02_SSDs.pdf                    (System Sequence Diagrams)
│   └── 03_Operation_Contracts.pdf     (System Operations)
│
└── 03_Elaboration_2_Frozen/   <-- Phase 3 Snapshot (Design - OOD)
    ├── 01_Design_Class_Diagram.pdf    (Software Classes with Methods)
    ├── 02_Sequence_Diagrams.pdf       (Object Interactions)
    └── 03_Pattern_Justification.pdf   (Strategy, Observer, Factory)