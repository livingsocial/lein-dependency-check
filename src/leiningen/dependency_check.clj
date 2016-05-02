(ns leiningen.dependency-check
  (:require [clojure.java.io :as io]
            [leiningen.core.classpath :as cp]
            [leiningen.core.main :refer [info warn debug]]
            [clojure.pprint :refer [pprint]])
  (:import (org.owasp.dependencycheck Engine)
           (org.owasp.dependencycheck.utils Settings)
           (org.owasp.dependencycheck.data.nvdcve CveDB)
           (org.owasp.dependencycheck.reporting ReportGenerator ReportGenerator$Format)))

(defn- db-properties
  "Returns the CVE database properties"
  []
  (let [cve-db (CveDB.)
        _ (.open cve-db)
        result (.getDatabaseProperties cve-db)
        _ (.close cve-db)]
    result))

(defn- target-files
  "Selects the files to be scanned"
  [project]
  (->> (cp/get-classpath project)
       (filter (partial re-find #"\.jar$"))
       (map io/file)))

(defn- scan-files
  "Scans the specified files and returns the engine used to scan"
  [files]
  (Settings/initialize)
  (let [engine (Engine.)]
    (info "Scanning" (count files) "file(s)...")
    (doseq [file files]
      (info "Scanning file" (.getCanonicalPath file))
      (.scan engine file))
    (info "Done.")

    engine))

(defn- analyze-files
  "Analyzes the files scanned by the specified engine and returns the engine"
  [engine]
  (info "Analyzing dependencies...")
  (.analyzeDependencies engine)
  (info "Done.")

  engine)

(defn- write-report
  "Writes a report using the analysis information in the specified engine and returns the engine"
  [engine report-name output-format output-directory]
  (info "Generating report...")
  (let [generator (ReportGenerator. report-name
                                    (.getDependencies engine)
                                    (.getAnalyzers engine)
                                    (db-properties))]
    (.generateReports generator output-directory output-format))
  (info "Done.")

  engine)

(def report-format-map
  {:xml  ReportGenerator$Format/XML
   :html ReportGenerator$Format/HTML})

(defn- report-format
  "Accepts a keyword (:xml :html) and returns the Java Enum value used by the underlying library to indicate the report format"
  [format-key]
  (get report-format-map format-key ReportGenerator$Format/HTML))

(defn dependency-check
  "Scans the JAR files found on the class path and creates a vulnerability report.

Accepts the following parameters
  output-format     The format in which the report will be written. Either :xml or :html
  output-directory  The directory in which the report will be written. The default is ./target"
  ([project] (dependency-check project ":html" "target"))
  ([project output-format] (dependency-check project output-format "target"))
  ([project output-format output-directory]
   (let [format-key (-> output-format read-string report-format)]
     (-> project
         target-files
         scan-files
         analyze-files
         (write-report (:name project) format-key output-directory)
         .cleanup))))
