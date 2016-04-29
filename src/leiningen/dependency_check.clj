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
  [engine]
  (info "Generating report...")
  (let [generator (ReportGenerator. "Analysis Report"
                                    (.getDependencies engine)
                                    (.getAnalyzers engine)
                                    (db-properties))]
    (.generateReports generator "target" ReportGenerator$Format/HTML))
  (info "Done.")

  engine)

(defn dependency-check
  "Scans the JAR files found on the class path and writes a report to the ./target directory"
  [project & args]
  (-> project
      target-files
      scan-files
      analyze-files
      write-report))
