# Phœbius

Phœbius is a music player inspired from [CyanogenMod's Eleven](https://github.com/CyanogenMod/android_packages_apps_Eleven) and [N7](http://n7player.com/) music players.
Its aim is to be a simple to use, fast and powerful music player while combining the benefits of the best Android music players.

![](img/overview.png)

## Developement status, credits and license

Phœbius is licensed under GNU/GPL v.3 and currently in early alpha status and was initially started as a school project.
The project uses [Groovy](http://www.groovy-lang.org) (no Java inside) [Otto event bus](https://square.github.io/otto) and [SwissKnife annotations library](http://arasthel.github.io/SwissKnife).

You may find an installable APK [here](dist/phoebius).

## Contribution guidelines

If you're willing to contribute, please respect the following rules:

 * follow the [Groovy style guide](http://groovy-lang.org/style-guide.html) as much as possible and in particular:
   * use no semi-colons,
   * omit `return` keyword when it's non-ambigious; in particular, omit in getters ans `toString()` methods,
   * you can use `def` keyword for local variables when they are initialized on the same line,
   * use `==` instead of `.equals()`,
   * use inline declarations for maps, arrays and lists

 * use `@CompileStatic` on every class, interface, trait or enum you write. Using this annotation lets the Groovy compiler bypass the meta-object protocol and perform static compilation. Please be aware that it may cause compilation problems and force you to explicitely cast variables

 * **do not** write interfaces unless you only need to share constants between objects. If you need polymorphism, use [Groovy traits](http://docs.groovy-lang.org/next/html/documentation/core-traits.html).