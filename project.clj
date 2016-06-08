(defproject com.livingsocial/lein-dependency-check "0.1.1"
  :description "Clojure command line tool for detecting vulnerable project dependencies"
  :url "https://github.com/livingsocial/lein-dependency-check"
  :license {:name "The MIT License (MIT)"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.owasp/dependency-check-core "1.3.6"]
                 [org.owasp/dependency-check-utils "1.3.6"]]
  :eval-in-leiningen true)
