(ns nptr-clj.http
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [mount.core :refer [defstate]]
            [clojure.string :as str]
            [compojure.route :as route]
            [compojure.core :refer :all]
            [ring.middleware.params :refer [wrap-params]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.util.mime-type :refer [ext-mime-type]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [digest]
            [nptr-clj.config :refer [config]]))


(def DEFAULT-MIME-TYPE "text/plain")


(defn get-file-extension [filename]
  (if-let [ext (second (re-find #"(\.[^./\\]+)$" filename))]
    (str/lower-case ext)
    ""))


(defn get-file-path [filename]
  (let [path (io/file (:storage-dir config) filename)]
    (io/make-parents path)
    path))


(defn compose-link [filename]
  (let [domain (-> config :http :domain)]
    (format "%s/%s" domain filename)))


(defn save-file [filebuffer path]
  (when-not (.exists path)
    (with-open [out (io/output-stream path)]
      (io/copy filebuffer out))))


(defn index-handler [req]
  (let [domain (-> config :http :domain)]
    {:status 200
     :headers {"Content-Type" DEFAULT-MIME-TYPE}
     :body (str "curl -F'f=@file' " domain)}))


(defn store-content-handler [req]
  (if-let [f (-> req :params :f)]
    (let [buffer (:tempfile f)
          hash (digest/sha-256 buffer)
          ext (get-file-extension (:filename f))
          filename (str hash ext)
          path (get-file-path filename)
          link (compose-link filename)]
      (save-file buffer path)
      {:status 200 :headers {"Content-Type" DEFAULT-MIME-TYPE} :body link})
    {:status 400 :headers {"Content-Type" DEFAULT-MIME-TYPE} :body "file key param not found"}))


(defn get-content-handler [req]
  (let [filename (-> req :route-params :file)
        path (get-file-path filename)]
    (if (.exists path)
      (let [f (io/input-stream path)
            mime-type (ext-mime-type filename)]
        {:status 200 :headers {"Content-Type" mime-type} :body f})
      {:status 404 :headers {"Content-Type" DEFAULT-MIME-TYPE} :body "file not found"})))


(defroutes app-raw
  (GET "/" [] index-handler)
  (POST "/" [] store-content-handler)
  (ANY "/health" _ "ok")
  (GET "/:file" [] get-content-handler)
  (route/not-found "not found"))


(def app
  (-> app-raw
      wrap-keyword-params
      wrap-params
      wrap-multipart-params))


(defstate ^{:on-reload :noop} server
  :start
  (let [conf (assoc (:http config) :join? false)]
    (run-jetty app conf)
    (log/info "Server running..." conf))
  :stop (.stop server))