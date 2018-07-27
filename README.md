The purpose of this diploma thesis was to develop a methodology for quantifying
cohesion and coupling at package level in order to propose solutions to improve the design
quality of object-oriented systems.

The consistency of classes in a package is desirable once it promotes encapsulation. It
shows how powerful the functions are in each program package. Well-structured packages
lead to highly coherent programs. The weakness or lack of cohesion suggest that the package
may have to be split into two or more packages or that some classes have to be moved to
another package where the dependencies are more powerful.

Coupling measures how much each module depends on the other sections of the
program. Interactions between classes occur because there is coupling. Loosely linked
programs have high flexibility and extendability. Increased coupling levels are undesirable in
systems composed of sub-units and are a brake on reuse.

Lack of cohesion and coupling increases the complexity and likelihood of errors
occurring during the development, maintenance, and expansion stages of software. Loose
coupling and strong cohesion provide the best software.

The methodology developed was implemented in Java. It accepts as input a software
program, follows a structural analysis of relations between classes and packages and
calculates cohesion and coupling metrics. Then, it creates groups of n classes (n = 1, 2, 3, ...)
for the purpose of transferring them to another package where the above mentioned metrics
are improved. The goal is to draw conclusions about the number of classes that bring the
optimal result at cohesion and coupling level.

The validity of the methodology was tested in java-based open source programs
implemented with object-oriented programming.

Keywords: Software Engineering, Move Classes, Package Coupling, Package Cohesion,
Software Quality, Object Oriented Design, Modularity
