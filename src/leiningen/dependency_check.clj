(ns leiningen.dependency-check
  (:require [clojure.java.io :as io]
	        [leiningen.core.classpath :as cp])
  (:import (org.owasp.dependencycheck Engine)
           (org.owasp.dependencycheck.data.nvdcve CveDB)
		   (org.owasp.dependencycheck.data.lucene LuceneUtils)
		   (org.owasp.dependencycheck.dependency Dependency)
		   (org.owasp.dependencycheck.dependency Identifier)
		   (org.owasp.dependencycheck.dependency Vulnerability)
		   (org.owasp.dependencycheck.reporting ReportGenerator)
		   (org.owasp.dependencycheck.utils Settings)))

(defn dependency-check
  "I don't do a lot."
  [project & args]
  (Settings/initialize)
  (let [engine (Engine.)
	     jars (map io/file (filter (partial re-find #"\.jar$") (cp/get-classpath project)))]
		 (CpeMemoryIndex/getInstance)
    (doseq [jar jars]
		(println "Scanning" (.getPath jar) (.getName jar))
		(.scan engine jar)
		(.analyzeDependencies engine)))
  (println "Hi!"))
