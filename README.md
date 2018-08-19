# sparkl -  Animating Quadric Surfaces in Clojure

This project is inspired by "Graphing Quadric Surfaces: Methods of simulating 3-D graphics on a color display in BASIC" by George Haroney in the December 1986 issue of BYTE.

I've had this article on 3D graphics sitting in my drawer for over 30 years. While looking for something to cut my teeth on in Clojure I decided to give it a go. This will be updated periodically as I discover better ways to write Clojure as well as add new surfaces and rendering options.

[![Graphing Quadric Surfaces with Clojure](http://img.youtube.com/vi/jEnbVVxLk_I/0.jpg)](http://www.youtube.com/watch?v=jEnbVVxLk_I)



## Introduction
Quadric surfaces are graphs of 2nd-degree equations in three variables (x, y, z). The surfaces I've graphed are:

- paraboloid
- hyperbolic paraboloid (aka saddle)
- cone
- hyperboloid of one sheet\*
- hyperboloid of two sheets
- ellipsoid

The basic approach is to solve a quadric equation (i.e. *x<sup>2</sup> + y<sup>2</sup> + z<sup>2</sup> = r<sup>2</sup>*) for z, then generate a "point cloud" by calculating z for a range of x and y values.

These coordinates are then projected onto the screen by using the angles (Ax, Ay, Az) each axis makes with plane of the screen from a given screen origin (h0, v0).

*h = x cosA<sub>x</sub> + y cosA<sub>y</sub> + z cosA<sub>z</sub> + h<sub>0</sub>*

*v = x sinA<sub>x</sub> + y sinA<sub>y</sub> + z sinA<sub>z</sub> + v<sub>0</sub>*

In order to animate the surface, the range of xy values used to generate the point cloud is rotated around the z-axis each frame.  A grid step for x and y is chosen for clarity and realtime performance.

The code also contains a feature for rendering out frames that can be stitched together into a video.

\*I am still working on getting the hyperboloid of one sheet to render as expected.


## Installation

The easiest way to run this code is with [Leiningen](https://leiningen.org/). Once Leiningen is installed, type `lein run` from application root to start the program. This program uses the [Quil](https://github.com/quil/quil) library (itself based on [Processing](https://processing.org/)) for rendering. It will install these dependencies on initial startup. The program will enter fullscreen mode and render the selected surface. Press escape to quit.


## Usage

There are four files, core.clj (basic app and screen settings), quadric.clj (renders the surfaces), styling.clj (contains color definitions) and surfaces.clj (contains surface equations and display settings for each surface).

Select a surface to render by setting the value of *config* on line 13 of quadric.clj i.e `(def config (:one-sheet s/settings))` or `(def config (:cone s/settings))`. The rotation speed, framerate and xy range may be set from this file.

To adjust how each surface is rendered play around with the values in surfaces.clj. Grid spacing, xyz axis angles, rendering colors and more can be controlled here.


## Future Work

There are a number of things I'd like to add, these are listed below:

1. Base rotation on time so the rotation speed will be independent of the frame rate.
2. Stitch together rendered frames into a video.
3. Provide UI for selecting surfaces and modifying rendering values.
4. Improve overall rendering performance (Quil may be overkill for my needs).
5. Render one-sheet hyperboloid correctly.
6. Link axes rendering to complete 3D illusion.
7. Add more complex surfaces.


## License

Copyright © 2018 Chris Braithwaite

Distributed under the MIT Public License.
