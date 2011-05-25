(ns foreclojure.core
  (:use compojure.core
        [foreclojure static problems login register golf ring
         users config social version graphs mongo utils]
        ring.adapter.jetty
        somnium.congomongo
        (ring.middleware (reload :only [wrap-reload])
                         (stacktrace :only [wrap-stacktrace])
                         [file-info :only [wrap-file-info]]))
  (:require [compojure [route :as route] [handler :as handler]]
            [sandbar.stateful-session :as session]))

(defroutes main-routes
  (GET "/" [] (welcome-page))
  login-routes
  register-routes
  problems-routes
  users-routes
  static-routes
  social-routes
  version-routes
  graph-routes
  golf-routes
  (-> (resources "/*")
      (wrap-url-as-file)
      (wrap-file-info))
  (route/not-found "Page not found"))

(def app (-> #'main-routes
             ((if (:wrap-reload config)
                #(wrap-reload % '(foreclojure.core))
                identity))
             session/wrap-stateful-session
             handler/site
             wrap-uri-binding))

(defn run []
  (prepare-mongo)
  (run-jetty (var app) {:join? false :port 8080}))

(defn -main [& args]
  (run))