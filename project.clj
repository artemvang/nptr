(defproject nptr-clj "0.1.0-SNAPSHOT"
  :description "Simple service for fast files sharing via curl"
  :url "http://github.com/artemvang/nptr"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [ring/ring-jetty-adapter "1.8.2"]
                 [compojure "1.6.2"]
                 [ring/ring-core "1.8.2"]
                 [mount "0.1.16"]
                 [digest "1.4.9"]
                 [org.clojure/tools.logging "1.1.0"]
                 [aero "1.1.6"]]
  :main ^:skip-aot nptr-clj.core
  :target-path "target/%s"
  :profiles {
   :uberjar {:aot :all
             :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
