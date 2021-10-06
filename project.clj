(def dependency-check-version "6.2.2")

(defproject com.livingsocial/lein-dependency-check "1.1.7"
  :description "Clojure command line tool for detecting vulnerable project dependencies"
  :url "https://github.com/livingsocial/lein-dependency-check"
  :license {:name "The MIT License (MIT)"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.owasp/dependency-check-core ~dependency-check-version]
                 [org.owasp/dependency-check-utils ~dependency-check-version]
                 [org.clojure/tools.cli "0.4.1"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.slf4j/slf4j-log4j12 "1.7.1"]
                 [log4j/log4j "1.2.17"]]
  :eval-in-leiningen true)
