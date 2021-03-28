# 

<!-- badges -->
[![cljdoc badge](https://cljdoc.org/badge/lambdaisland/daedalus)](https://cljdoc.org/d/lambdaisland/daedalus) [![Clojars Project](https://img.shields.io/clojars/v/lambdaisland/daedalus.svg)](https://clojars.org/lambdaisland/daedalus)
<!-- /badges -->

ClojureScript wrapper for hxdaedalus-js path-finding and triangulation

<!-- opencollective -->

&nbsp;

<img align="left" src="https://github.com/lambdaisland/open-source/raw/master/artwork/lighthouse_readme.png">

&nbsp;

## Support Lambda Island Open Source

daedalus is part of a growing collection of quality Clojure libraries and
tools released on the Lambda Island label. If you are using this project
commercially then you are expected to pay it forward by
[becoming a backer on Open Collective](http://opencollective.com/lambda-island#section-contribute),
so that we may continue to enjoy a thriving Clojure ecosystem.

&nbsp;

&nbsp;

<!-- /opencollective -->

## What does it do?

Path finding in 2D space based on Delaunay triangulation. The main use case is
for 2D games, where you want to determine a path from A to B, while keeping
obstacles into account. For example, in point-in-click games, walking towards
where the player has clicked.

The original implementation was written in ActionScript by [Cédric
Jules](https://github.com/totologic) in 2013. This code was later ported to Haxe
as [hxDaedalus](https://github.com/hxDaedalus/hxDaedalus), a language which
transpiles to multiple other languages, including JavaScript. This is how we end
up with [hxdaedalus-js](https://www.npmjs.com/package/hxdaedalus-js), which this
library is a ClojureScript wrapper for.

- [Cédric's original announcement post](https://web.archive.org/web/20151102235416/http://totologic.blogspot.com/2013/12/introducing-daedalus-lib_19.html)
- [Demo video](https://www.youtube.com/watch?v=5fZJ1x7R_u8)
- [Original google code project](https://code.google.com/archive/p/daedalus-lib/)
- [hxDaedalus JS examples](https://github.com/hxDaedalus/hxDaedalus-Examples/tree/master/hxDaedalus-Examples/web)

Cédric based their code on three research papers:

- [Efficient Triangulation-Based Pathfinding, by Douglas Jon Demyen](/docs/pdf/thesis_demyen_2006.pdf)
- [Fully Dynamic Constrained Delaunay Triangulations, by Kallmann, Bieri and Thalmann](/docs/pdf/fully_dynamic_constrained_delaunay_triangulation.pdf)
- [An improved incremental algorithm for constructing restricted Delaunay triangulations, by Marc Vigo Anglada](/docs/pdf/An_Improved_Incremental_Algorithm_for_Constructing.pdf)

This library

- Adds ClojureScript printer definitions for all of Daedalus's types, so you can
  see what you are doing when working on a REPL
- Implement `ILookup`, so you can access plain variables and object getters with
  `(:x obj)` style keyword access (also supports destructuring, `get-in`, etc.)
- Implement `ITransientCollection` for `Mesh`, so you can conveniently `conj!`
  things onto it
- Create constructor functions, for a more idiomatic API
- Adds a few helper functions, `build-rect-mesh`, `rect`, `find-path`

Before:

``` clojure
(def p (d/PathFinder.))
(set! (.-entity p) entity)
(set! (.-mesh p) world)
```

After

``` clojure
(d/path-finder {:entity entity :mesh world})
```

The best docs for what this library can do are the [wiki pages of the original
project](/docs/original-wiki), which I have archived here in case the original
disappear.

<!-- installation -->
## Installation
deps.edn

```
lambdaisland/daedalus {:mvn/version "0.0.25"}
```

project.clj

```
[lambdaisland/daedalus "0.0.25"]
```
<!-- /installation -->

## Usage

``` clojure
(require '[lambdaisland.daedalus :as d])

;; Entity that is looking for a path, 
(def entity (d/entity-ai {:x 10 :y 10 :radius 1}))

;; you can update `:x` / `:y` to set the start position
;; (set! (.-x entity) 20)

(def world (d/build-rect-mesh 100 100))
(def path-finder (d/path-finder {:entity entity :mesh world}))

;; add obstacle
(conj! world (d/rect 10 12 10 10))

;; find a path
(d/find-path path-finder 30 30)
;;=>
([10 10]
 [20.09901714548254 11.004914272587285]
 [20.38268343236509 11.076120467488714]
 [20.707106781186546 11.292893218813452]
 [20.89671072740957 11.557382929216907]
 [30 30])
```

<!-- contributing -->
## Contributing

Everyone has a right to submit patches to daedalus, and thus become a contributor.

Contributors MUST

- adhere to the [LambdaIsland Clojure Style Guide](https://nextjournal.com/lambdaisland/clojure-style-guide)
- write patches that solve a problem. Start by stating the problem, then supply a minimal solution. `*`
- agree to license their contributions as MPL 2.0.
- not break the contract with downstream consumers. `**`
- not break the tests.

Contributors SHOULD

- update the CHANGELOG and README.
- add tests for new functionality.

If you submit a pull request that adheres to these rules, then it will almost
certainly be merged immediately. However some things may require more
consideration. If you add new dependencies, or significantly increase the API
surface, then we need to decide if these changes are in line with the project's
goals. In this case you can start by [writing a pitch](https://nextjournal.com/lambdaisland/pitch-template),
and collecting feedback on it.

`*` This goes for features too, a feature needs to solve a problem. State the problem it solves, then supply a minimal solution.

`**` As long as this project has not seen a public release (i.e. is not on Clojars)
we may still consider making breaking changes, if there is consensus that the
changes are justified.
<!-- /contributing -->

<!-- license -->
## License

Copyright &copy; 2021 Arne Brasseur and Contributors

Licensed under the term of the Mozilla Public License 2.0, see LICENSE.
<!-- /license -->
