(ns leiningen.dependency-check
  (:require [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]]
            [leiningen.core.classpath :as cp]
            [leiningen.core.eval :as eval]))

(def ^:private cli-options
  "Command line options accepts:
  log, throw, output-format, output-directory, properties-file."
  [[nil "--log" "Log to stdout"]
   [nil "--throw" "throw error when vulnerabilities found"]
   [nil "--min-cvss-v3 NUMBER" "minimum cvss score required to throw (use in conjunction with --throw)"
    :parse-fn #(Double/parseDouble %)]
   ["-p" "--properties-file FILE" "Specifies a file that contains properties to merge with defaults."]
   ["-f" "--output-format FORMAT(S)" "The output format to write to (XML, HTML, CSV, JSON, VULN, ALL). Default is HTML"
    :parse-fn (fn [output-format]
                (-> (string/replace output-format #":" "")
                    (string/split #",")))]
   ["-o" "--output-directory DIR" "The folder to write to. The default is ./target"]
   ["-s" "--suppression-file FILE" "Path to the suppression XML file"]])

(def ^:private cli-defaults
  "Default options."
  {:output-format    ["html"]
   :output-directory "target"
   :suppression-file "suppression.xml"
   :log              false
   :throw            false
   :min-cvss-v3      0})

(defn- dependency-check-project
  "Create a project to launch dependency-check, with only dependency-check as a dependency."
  [project]
  (if-let [dependency-check-vec (->> (:plugins project)
                                     (filter #(= 'com.livingsocial/lein-dependency-check
                                                 (first %)))
                                     first)]
    {:dependencies [dependency-check-vec]}
    (throw (Exception. (str "dependency-check should be in your :plugins vector, "
                            "either in your ~/.lein/profiles.clj or in "
                            "the project itself.")))))

(defn dependency-check
  "CLI options will overwrite config options.
  Default options:
  log => false
  throw => false
  output-format => :html
  output-directory => ./target"
  [project & args]
  (let [classpath (cp/get-classpath project)
        name (:name project)
        config (merge cli-defaults
                      (:dependency-check project)
                      (:options (parse-opts args cli-options)))]
    (eval/eval-in-project (dependency-check-project project)
                          `(lein-dependency-check.core/main '~classpath '~name '~config)
                          '(require 'lein-dependency-check.core))))
