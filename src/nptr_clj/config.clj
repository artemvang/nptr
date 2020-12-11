(ns nptr-clj.config
  (:require [mount.core :refer [defstate]]
            [aero.core :as aero]
            [clojure.java.io :as io]))


(defn -config []
  (let [cfg (io/resource "config.edn")]
    (aero/read-config cfg)))


(defstate config
  :start (-config))
