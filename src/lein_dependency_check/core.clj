(ns lein-dependency-check.core
  (:require [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [clojure.string :refer [starts-with?]])
  (:import (org.owasp.dependencycheck Engine)
           (org.owasp.dependencycheck.utils Settings Settings$KEYS)
           (org.owasp.dependencycheck.data.nvdcve CveDB)
           (org.owasp.dependencycheck.reporting ReportGenerator ReportGenerator$Format)
		   (org.apache.log4j PropertyConfigurator)))

(defonce SUPPRESSION_FILE "suppression.xml")
(defonce SOURCE_DIR       "src")
(defonce LOG_CONF_FILE    "log4j.properties")

(defn reconfigure-log4j
  "Reconfigures log4j from a log4j.properties file"
  []
  (let [config-file (io/file SOURCE_DIR LOG_CONF_FILE)]
    (when (.exists config-file)
	   (prn "Reconfiguring log4j")
	   (PropertyConfigurator/configure (.getPath config-file)))))

(defn- target-files
  "Selects the files to be scanned"
  [project-classpath]
  (->> project-classpath
       (filter (partial re-find #"\.jar$"))
       (map io/file)))

(defn- scan-files
  "Scans the specified files and returns the engine used to scan"
  [files {:keys [properties-file]}]
  (let [settings (Settings.)
        _ (when (.exists (io/as-file SUPPRESSION_FILE))
            (.setString settings Settings$KEYS/SUPPRESSION_FILE SUPPRESSION_FILE))
        _ (when properties-file
            (.mergeProperties settings properties-file))
        engine (Engine. settings)]
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
  [engine report-name output-format output-directory]
  (.writeReports engine report-name output-directory output-format)
  engine)

(defn- handle-vulnerabilities [engine {:keys [log throw min-cvss] :or {min-cvss 0}}]
  (.close engine)
  (when-let [vulnerable-dependencies (->> (.getDependencies engine)
                                          (filter #((complement empty?) (.getVulnerabilities %)))
                                          (map (fn [dep] {:dependency dep
                                                          :vulnerabilities (.getVulnerabilities dep)}))
                                          seq)]
    (when log
      (doall (map #(prn "Vulnerable Dependency:" (.toString %)) vulnerable-dependencies)))
    (when throw
      (let [max-score (->> vulnerable-dependencies
                           (mapcat :vulnerabilities)
                           (map #(.getCvssScore %))
                           (apply max-key identity))]
        (when (> max-score min-cvss)
          (throw (ex-info "Vulnerable Dependencies!" {:vulnerable vulnerable-dependencies}))))))
  engine)

(defn main
  "Scans the JAR files found on the class path and creates a vulnerability report."
  [project-classpath project-name output-format output-directory config]
  (reconfigure-log4j)
  (let [output-format (if (starts-with? output-format ":")
                        (.substring output-format 1)
                        output-format)
        output-target (io/file output-directory)]
    (-> project-classpath
        target-files
        (scan-files config)
        analyze-files
        (write-report project-name output-format output-target)
        (handle-vulnerabilities config))))
