# lein-dependency-check

A Leiningen plugin for detecting vulnerable project dependencies. Basic clojure wrapper for [OWASP Dependency Check](https://www.owasp.org/index.php/OWASP_Dependency_Check).

## Configuration

### As a User-Level Plugin:

To run dependency-check without having to add it to every Leiningen project as a project-level plugin,
add dependency-check to the `:plugins` vector of your `:user` profile. E.g., a `~/.lein/profiles.clj` with dependency-check as a plugin -
```
{:user {:plugins [[com.livingsocial/lein-dependency-check "1.0.4"]]}}
```

If you are on Leiningen 1.x do `lein plugin install lein-dependency-check 1.0.4`.

### As a Project-Level Plugin:

Add `[com.livingsocial/lein-dependency-check "1.0.4"]` to the `:plugins` vector of your project.clj.

Project-level configuration may be provided under a `:dependency-check` key in your project.clj. Currently supported options are:
 * `:log` log each vulnerability found to stdout
 * `:throw` throw an exception after analysis and reporting if vulnerabilities are found, eg. to fail a build
 * `:properties-file` properties file to merge with DependencyCheck settings

## Usage

To generate a `dependency-check-report.html` report file to the current project's `target` directory, run:

    $ lein dependency-check

To generate the report in XML format, run:

    $ lein dependency-check :xml

To write the report to a different directory (e.g., `/tmp`), run:

    $ lein dependency-check :html /tmp

##  Suppressing False Positives

Support for suppressing false positives can be utilized by creating `suppression.xml` in your project's root directory.

Suppression snippets can be copied from the HTML report file directly into `suppression.xml`. Upon rerun of `lein-dependency-check`, the suppression file will be used and warnings will not be present in report.

For more information about dependency-check suppression system see https://jeremylong.github.io/DependencyCheck/general/suppression.html

## License

Copyright © 2016 LivingSocial

Distributed as open source under the terms of the [MIT
License](http://opensource.org/licenses/MIT).
