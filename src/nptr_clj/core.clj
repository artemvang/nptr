(ns nptr-clj.core
  (:gen-class)
  (:require [mount.core :as mount]
            [nptr-clj.http]))


(defn -main [& args]
  (mount/start))
