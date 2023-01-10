(def dependency-check-version "7.4.4")

(defproject com.livingsocial/lein-dependency-check "1.4.0"
  :description "Clojure command line tool for detecting vulnerable project dependencies"
  :url "https://github.com/livingsocial/lein-dependency-check"
  :license {:name "The MIT License (MIT)"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.owasp/dependency-check-core ~dependency-check-version]
                 [org.owasp/dependency-check-utils ~dependency-check-version]
                 [org.clojure/tools.cli "1.0.206"]
                 [org.clojure/tools.logging "1.2.4"]
                 [org.slf4j/jcl-over-slf4j "2.0.3"]
                 [org.slf4j/slf4j-api "2.0.3"]
                 [org.apache.logging.log4j/log4j-api "2.19.0"]
                 [org.apache.logging.log4j/log4j-slf4j-impl "2.19.0"]
                 [org.apache.logging.log4j/log4j-core "2.19.0"]]
  :eval-in-leiningen true)
