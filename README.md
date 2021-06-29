# Settlers of Catan Game

Welcome to Settlers of Catan.
This game works like the classic version of the board game, same rules and some features.

Features:
- Colored TUI
- GUI
- Expandability
- Save Game via XML and JSON
- Undo/Redo


## Expandability

Each component has an ComponentImpl class to inherit from which needs to be initialized with the init() method.
So build the implementation and add the according companion object which inherits from the ComponentImpl and initialize it before using.


## Builds

### Main:  
[![Build Status](https://travis-ci.org/Vincent-76/Catan.svg?branch=main)](https://travis-ci.org/Vincent-76/Catan)  
[![Coverage Status](https://coveralls.io/repos/github/Vincent-76/Catan/badge.svg?branch=main)](https://coveralls.io/github/Vincent-76/Catan?branch=main)

### Dev:    
[![Build Status](https://travis-ci.org/Vincent-76/Catan.svg?branch=dev)](https://travis-ci.org/Vincent-76/Catan)  
[![Coverage Status](https://coveralls.io/repos/github/Vincent-76/Catan/badge.svg?branch=dev)](https://coveralls.io/github/Vincent-76/Catan?branch=dev)