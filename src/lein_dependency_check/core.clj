(ns lein-dependency-check.core
  (:require [clojure.java.io :as io])
  (:import (org.owasp.dependencycheck Engine)
           (org.owasp.dependencycheck.utils Settings Settings$KEYS)
           (org.owasp.dependencycheck.data.nvdcve CveDB)
           (org.owasp.dependencycheck.reporting ReportGenerator ReportGenerator$Format)))

(defonce SUPPRESSION_FILE "suppression.xml")

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
  [project-classpath]
  (->> project-classpath
       (filter (partial re-find #"\.jar$"))
       (map io/file)))

(defn- scan-files
  "Scans the specified files and returns the engine used to scan"
  [files]
  (Settings/initialize)
  (when (.exists (io/as-file SUPPRESSION_FILE))
    (Settings/setString Settings$KEYS/SUPPRESSION_FILE SUPPRESSION_FILE))
  (let [engine (Engine.)]
    (prn "Scanning" (count files) "file(s)...")
    (doseq [file files]
      (prn "Scanning file" (.getCanonicalPath file))
      (.scan engine file))
    (prn "Done.")

    engine))

(defn- analyze-files
  "Analyzes the files scanned by the specified engine and returns the engine"
  [engine]
  (prn "Analyzing dependencies...")
  (.analyzeDependencies engine)
  (prn "Done.")

  engine)

(defn- write-report
  "Writes a report using the analysis information in the specified engine and returns the engine"
  [engine report-name output-format output-directory]
  (prn "Generating report...")
  (let [dependencies (->> engine
                          (.getDependencies)
                          (sort-by #(.getFileName %)))
        generator (ReportGenerator. report-name
                                    dependencies
                                    (.getAnalyzers engine)
                                    (db-properties))]
    (.generateReports generator output-directory output-format))
  (prn "Done.")

  engine)

(def report-format-map
  {:xml  ReportGenerator$Format/XML
   :html ReportGenerator$Format/HTML
   :all  ReportGenerator$Format/ALL
   :vuln ReportGenerator$Format/VULN})

(defn- report-format
  "Accepts a keyword (:xml :html) and returns the Java Enum value used by the underlying library to indicate the report format"
  [format-key]
  (get report-format-map format-key ReportGenerator$Format/HTML))

(defn main
  "Scans the JAR files found on the class path and creates a vulnerability report."
  [project-classpath project-name output-format output-directory]
  (let [format-key (-> output-format read-string report-format)]
    (-> project-classpath
        target-files
        scan-files
        analyze-files
        (write-report project-name format-key output-directory)
        .cleanup)))