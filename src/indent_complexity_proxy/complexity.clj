;;; Copyright (C) 2014 Adam Tornhill
;;;
;;; Distributed under the GNU General Public License v3.0,
;;; see http://www.gnu.org/licenses/gpl.html

(ns indent-complexity-proxy.complexity
  (:require [clojure.string :as string]
            [incanter.stats :as stats]))

(defn- trim-indent
  "For simplicity we drop all matches - doesn't
   really matter what comes after the inital
   white space anyway."
  [line regexp]
  (string/replace line regexp ""))

(defn trim-leading-spaces
  [line]
  (trim-indent line #"[ ]"))

(defn trim-leading-tabs
  [line]
  (trim-indent line #"[\t]"))

(defn leading-tabs-count
  "Returns the number of leading tabs, each forms
   a logical indent."
  [line]
  (->>
   (trim-leading-spaces line)
   (re-find #"^\t+")
   count))

(defn leading-spaces-count
  "Returns the number of leading spaces.
   Note that this is a raw number and has
   to be adjusted to a logical indent."
  [line]
  (->>
   (trim-leading-tabs line)
   (re-find #"^ +")
   count))

(defn as-logical-indents
  "Returns the total number of logical indents at
   the start of the given line."
  [{:keys [tabs spaces]} line]
  (let [raw-tabs (leading-tabs-count line)
        logical-tabs (/ raw-tabs tabs)
        raw-spaces (leading-spaces-count line)
        logical-spaces (/ raw-spaces spaces)]
    (+ logical-tabs logical-spaces)))

(defn- drop-empty-lines
  [text]
  (remove #(re-matches #"^\s*$" %) text))

;; Specify functions for calculating the statistics.
;; Each of these functions operate on a sequence of
;; lines as specified by their indentation:

(defn- as-presentation-value
  [v]
  (format "%.2f" v))

(def total (comp float (partial reduce +)))

(def mean (comp as-presentation-value stats/mean))

(def median (comp as-presentation-value stats/median))

(def sd (comp as-presentation-value stats/sd))

(defn max-c [v]
  (if (seq v)
    (int (apply max v))
    0))

(defn- stats-from
  [indented-lines]
  {:total (total indented-lines)
   :n (count indented-lines)
   :mean (mean indented-lines)
   :median (median indented-lines)
   :sd (sd indented-lines)
   :max (max-c indented-lines)})

(defn total-indent-complexity
  "Accumulates the total complexity (expressed as
   leading logical units of indentation) of the
   given seq of lines."
  [options lines]
  (->>
   (drop-empty-lines lines)
   (map (partial as-logical-indents options))
   stats-from))
