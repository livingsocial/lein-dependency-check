# lein-dependency-check

A Leiningen plugin for detecting vulnerable project dependencies. Basic clojure wrapper for [OWASP Dependency Check](https://www.owasp.org/index.php/OWASP_Dependency_Check).

## Configuration

### As a User-Level Plugin:

To run dependency-check without having to add it to every Leiningen project as a project-level plugin,
add dependency-check to the `:plugins` vector of your `:user` profile. E.g., a `~/.lein/profiles.clj` with dependency-check as a plugin -
```
{:user {:plugins [[com.livingsocial/lein-dependency-check "0.1.1"]]}}
```

If you are on Leiningen 1.x do `lein plugin install lein-dependency-check 0.1.1`.

### As a Project-Level Plugin:

Add `[com.livingsocial/lein-dependency-check "0.1.1"]` to the `:plugins` vector of your project.clj.

## Usage
To generate a `dependency-check-report.html` report file to the current project's `target` directory, run:

    $ lein dependency-check

To generate the report in XML format, run:

    $ lein dependency-check :xml

To write the report to a different directory (e.g., `/tmp`), run:

    $ lein dependency-check :html /tmp

## License

Copyright © 2016 LivingSocial

Distributed as open source under the terms of the [MIT
License](http://opensource.org/licenses/MIT).
