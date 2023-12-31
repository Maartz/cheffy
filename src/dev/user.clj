(ns user
  (:require [io.pedestal.http :as http] [io.pedestal.http.route :as route]))

(defn list-recipes [request]
  {:status 200 :body "list recipes"})

(defn upsert-recipe [request]
  {:status 200 :body "upsert recipes"})

(def table-routes
  (route/expand-routes
   #{{:app-name :cheffy :schema :http :host "localhost"}
     ["/recipes" :get list-recipes :route-name :list-recipes]
     ["/recipes" :post upsert-recipe :route-name :create-recipe]
     ["/recipes/:recipe-id" :put upsert-recipe :route-name :update-recipe]}))

; More compact way to deal with routes
; (def terse-routes
;   (route/expand-routes
;    [[:cheffy :http "localhost"
;      ["/recipes" {:get `list-recipes :post `upsert-recipe}
;       ["/:recipe-id" {:put [:update-recipe `upsert-recipe]}]]]]))

(defonce system-ref (atom nil))

(defn start-dev []
  (reset! system-ref
          (-> {::http/routes table-routes
               ::http/type :jetty
               ::http/join? false
               ::http/port 3000}
              (http/create-server)
              (http/start)))
  :started)

(defn stop-dev []
  (http/stop @system-ref)
  :stopped)

(defn restart-dev []
  (stop-dev)
  (start-dev)
  :restarted)

(comment

  (start-dev)

  (restart-dev)

  (stop-dev))
