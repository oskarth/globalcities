(ns globalcities.core
  (:require [clj-http.client :as client]))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(client/get "http://google.com")