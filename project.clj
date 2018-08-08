(defproject sparkl "0.1.0-SNAPSHOT"
            :description "SPARKL: Graphics Adventures in Clojure"
            :url "http://example.com/FIXME"
            :license {:name "Eclipse Public License"
                      :url "http://www.eclipse.org/legal/epl-v10.html"}
            :dependencies [[org.clojure/clojure "1.8.0"]
                           [quil "2.7.1"]]
            :main ^:skip-aot sparkl.core
            :target-path "target/%s"
            :profiles {:uberjar {:aot :all}})
