(def dependency-check-version "6.5.3")

(defproject com.livingsocial/lein-dependency-check "1.2.0"
  :description "Clojure command line tool for detecting vulnerable project dependencies"
  :url "https://github.com/livingsocial/lein-dependency-check"
  :license {:name "The MIT License (MIT)"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.owasp/dependency-check-core ~dependency-check-version]
                 [org.owasp/dependency-check-utils ~dependency-check-version]
                 [org.clojure/tools.cli "1.0.194"]
                 [org.clojure/tools.logging "1.2.4"]
                 [org.slf4j/jcl-over-slf4j "1.7.30"]
                 [org.slf4j/slf4j-api "1.7.32"]
                 [org.apache.logging.log4j/log4j-api "2.17.1"]
                 [org.apache.logging.log4j/log4j-slf4j-impl "2.14.1"]
                 [org.apache.logging.log4j/log4j-core "2.17.1"]]
  :eval-in-leiningen true)
