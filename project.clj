(defproject roborunner "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [compojure "1.6.1"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-json "0.4.0"]
                 [ring-cors "0.1.13"]
                 [org.clojure/data.json "0.2.6"]
                 [http-kit "2.3.0"]]
  :ring {:handler roborunner.core/app}
  :main ^:skip-aot roborunner.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
