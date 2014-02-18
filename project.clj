(defproject indent-complexity-proxy "0.1.0"
  :description "Calculates complexity of a given code snippet using indentation as a proxy."
  :url "https://github.com/adamtornhill/indent-complexity-proxy"
  :license {:name "GNU General Public License v3.0"
            :url "http://www.gnu.org/licenses/gpl.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/math.numeric-tower "0.0.4"]
                 [org.clojure/tools.cli "0.3.1"]]
  :main indent-complexity-proxy.cmd-line
  :aot [indent-complexity-proxy.cmd-line])
