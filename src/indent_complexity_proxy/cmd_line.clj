;;; Copyright (C) 2014 Adam Tornhill
;;;
;;; Distributed under the GNU General Public License v3.0,
;;; see http://www.gnu.org/licenses/gpl.html

(ns indent-complexity-proxy.cmd-line
  (:require [indent-complexity-proxy.complexity :as c]
            [clojure.math.numeric-tower :as math]
            [clojure.tools.cli :as cli]
            [clojure.string :as string])
  (:gen-class :main true))

;; This program calculates a complexity metric using the indentation of
;; the source code as a complexity proxy.
;; The program reads code from either a file or from stdin (the
;; program is designed to work together with the rest of the Unix world).

;;;
;;; Command line tools
;;;

(def cli-options
  [["-s" "--spaces SPACES" "The number of spaces to consider one logical indent."
    :default 4 :parse-fn #(Integer/parseInt %)]
   ["-t" "--tabs TABS" "The number of tabs to consider one logical indent."
    :default 1 :parse-fn #(Integer/parseInt %)]
   ["-h" "--help"]])

(defn- usage [options-summary]
  (->>
   ["This program calculates code complexity using white space as a proxy."
    ""
    "Usage: program-name [options] file|stdin"
    ""
    "Options:"
    options-summary]
   (string/join \newline)))

(defn- error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn- exit [status msg]
  (println msg)
  (System/exit status))

;;;
;;; Supported input streams
;;;

(defn- read-from-stdin
  []
  (line-seq (java.io.BufferedReader. *in*)))

(defn- read-from-file
  [file-name]
  (with-open [rdr (clojure.java.io/reader file-name)]
    (doall (line-seq rdr))))

(defn- read-input-based-on
  [[file-name]]
  (if file-name
    (read-from-file file-name)
    (read-from-stdin)))

;;;
;;; Application logic
;;;

(def as-presentation-value (comp int math/ceil))

(defn- run-analysis
  [options arguments]
  (->>
   (read-input-based-on arguments)
   (c/total-indent-complexity options)
   as-presentation-value
   println))

(defn -main
  [& args]
  (let [{:keys [options arguments errors summary]} (cli/parse-opts args cli-options)]
    (cond
     (:help options) (exit 0 (usage summary))
     errors (exit 1 (error-msg errors)))
    ; arguments fine -> let's calculate:
    (run-analysis options arguments)))
      
