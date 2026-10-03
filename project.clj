(defproject sparkl "0.1.0-SNAPSHOT"
            :description "SPARKL: Graphics Adventures in Clojure"
            :url "https://github.com/cbraith/sparkl"
            :license {:name "The MIT License"
                      :url "https://opensource.org/licenses/MIT"}
            :dependencies [[org.clojure/clojure "1.12.1"]
                           [quil "4.3.1563"]]
            :main ^:skip-aot sparkl.core
            :target-path "target/%s"
            :profiles {:uberjar {:aot :all}})
