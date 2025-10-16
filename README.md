# Shadow Casting Project Description

This is a simple project to deepen the knowledge of Python language.
The main goal of the project was to write down an algorithm capable of casting shadows for a determined sight radius using the pygame library.

The project is splitted in three different files: la_functions.py where I wrote all the necessary functions and keep the main code as clean as possible for better identifying errors and bugs. "lighting algorithm.py" is where the core of the program is written and, finally, the sight-radius.config file which is a simple file where the first row tells the program if the sight-range has to be limited to a radius or has to be the full window. Additionally there is a .pdf simple guide where all the keybindings are explained and on the functioning of the .config file.

## Quick Guide

There are two main phases in the program:

1. Building phase where you can draw obstacles and non-casting objects by using the left alt key to switch between them. Then use the left mouse Button dragging and dropping to draw the object.
2. Testing phase activatable and disactivatable by pressing the left CTRL key.

Further informations can be found in the .pdf guide.

### DESCRIPTION:

There are 2 different types of objects: visible and shadowing:

- SHADOWNIG → printed as blue rectangules
- VISIBLE → printed as light_blue rectangules

### HOW TO DRAW:

click left alt key to pass from shadowing object drawing to visible object drawing, then, holding down the left mouse
button, drag like in paint to draw the desired object corner to corner in the desired position.

### TEST:

when you want to test just click left CTRL key once. To close the test just click left CTRL key again.

### sight_radius.config:

In the project folder there should be a file named: sight_radius.config (if you don’t see it the program should not work,
so create one inside the folder containing the .exe), Open this file with a text editor or notepad++

#### File Format:

- 1st line → set the fullscreen mode (False, the next line will be considered; True, the sight radius will take the
  whole map)
- 2nd line → if the 1st line was False then the program will set the sight radius to the value of this line

#### Data Types

- 1ST LINE → True/False (key sensitive)
- 2ND LINE → any integer

## Conclusive Comments

This is just a personal implementation of the shadow casting algorithm typical of RTS games to train algorithms and moreover to advance in the python language knowledge. Interacting with configuration files to change the functionalities of a program and also train on making the code simple and clean in order to better recognize errors and bugs.

## VIRTUALENV

To make it work in a virtual environment just install virtualenv: `pip install virtualenv` and run `virutalenv --system-site-packages -p python3 .venv`, then `.\.venv\Scripts\activate.ps1`, finally `pip install -r package.lock`

## CONDA

conda env -f environment.yml -n <env_name>

## BUILD

pip install pyinstaller && pyinstaller --onefile lighting_algorithm.py
