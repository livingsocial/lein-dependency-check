(ns leiningen.dependency-check
  (:require [leiningen.core.classpath :as cp]
            [leiningen.core.eval :as eval]))

(defn- dependency-check-project
  "Create a project to launch dependency-check, with only dependency-check as a dependency."
  [project]
  (if-let [dependency-check-vec (first
                                 (drop-while
                                  (complement
                                   (fn [v] (= (first v) 'com.livingsocial/lein-dependency-check)))
                                  (:plugins project)))]
    {:dependencies [dependency-check-vec]}
    (throw (Exception. (str "dependency-check should be in your :plugins vector, "
                            "either in your ~/.lein/profiles.clj or in "
                            "the project itself.")))))

(defn dependency-check
  " Accepts the following parameters
  output-format     The format in which the report will be written. Either :xml or :html
  output-directory  The directory in which the report will be written. The default is ./target"
  ([project] (dependency-check project ":html" "target"))
  ([project output-format] (dependency-check project output-format "target"))
  ([project output-format output-directory]
   (let [classpath (cp/get-classpath project)
         name      (:name project)]
     (eval/eval-in-project (dependency-check-project project)
                           `(lein-dependency-check.core/main '~classpath '~name '~output-format '~output-directory)
                           '(require 'lein-dependency-check.core)))))
