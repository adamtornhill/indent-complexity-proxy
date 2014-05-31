;;; Copyright (C) 2014 Adam Tornhill
;;;
;;; Distributed under the GNU General Public License v3.0,
;;; see http://www.gnu.org/licenses/gpl.html

(ns indent-complexity-proxy.complexity-test
  (:require [indent-complexity-proxy.complexity :as c])
  (:use clojure.test))

(def ^:constant options
  {:tabs 1
   :spaces 4})

(deftest trims-leading-spaces
  (is (= (c/trim-leading-spaces "  abc") "abc"))
  (is (= (c/trim-leading-spaces "\t \t  abc") "\t\tabc") "Tabs are preserved."))

(deftest trims-leading-tabs
  (is (= (c/trim-leading-tabs "\t\tabc") "abc"))
  (is (= (c/trim-leading-tabs "\t \t  abc") "   abc") "Spaces are preserved."))

(deftest calculates-complexity-by-indent
  (is (= (c/as-logical-indents options "    if x == 0") 1)     "Spaces, one logical unit.")
  (is (= (c/as-logical-indents options "        if x == 0") 2) "Spaces, two logical unit.")
  (is (= (c/as-logical-indents options "\t\tif x == 0") 2)     "Tabs, two logical unit.")
  (is (= (c/as-logical-indents options "\t    \tif x == 0") 3) "Combination of tabs and spaces (evil)."))

(deftest sums-the-complexity-of-text-blocks
  "The text blocks are expressed as a seq of lines."
  (is (= (c/total-indent-complexity options ["\tSome code" "\t\tOn multiple" "    lines"])
         {:total 4.0 :n 3 :mean "1.33" :median "1.00" :sd "0.58" :max 2})))

(deftest empty-lines-are-ignored
  (is (= (c/total-indent-complexity options [""])
        {:total 0.0 :n 0 :mean "NaN" :median "0.00" :sd "-0.00" :max 0}))
  (is (= (c/total-indent-complexity options ["    \t    \t"])
        {:total 0.0, :n 0 :mean "NaN" :median "0.00" :sd "-0.00" :max 0})))
