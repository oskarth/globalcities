(ns globalcities.core
  (:require [net.cgrand.enlive-html :as html]))

;; city, city-url (for coordinates) and country(-url) (for language)
;; how to locally save pages the most straight-forward way?
;; when city-parsing: multithread requests
;; find map

;; left off at: get urls too, pattern match for country-city

;; great! now match every other. take or drop or something.

;; useful
(defmacro redir [filename & body]
  `(binding [*out* (clojure.java.io/writer ~filename)] ~@body))

(defn debug-html [seq]
  (redir "/tmp/foo.html" (print (apply str (html/emit* seq)))))

(defn debug-text [seq]
  (redir "/tmp/foo.txt" (print seq)))

;; faux wikipedia
(def ^:dynamic *url* "http://127.0.0.1:4567/global_cities.html")

;; this gets all alphas. (2 for betas)
(def ^:dynamic *scrape* (nth (html/select (html/html-resource (java.net.URL. *url*)) [[:table]]) 1))

(defn link? [node] (= (:tag node) :a))

(defn create-linkmap [content]
  [{:name (:title (:attrs content))
     :url (:href (:attrs content))}])

;; false name
(defn content->cities
  "turns scrape content into a seq with country-city repeated"
  [content]
  (cond
   (link? content) (create-linkmap content)
   (map? content) (content->cities (:content content))
   (coll? content) (mapcat content->cities content)))

;; turns name-url list into structured with country and city put together
(defn create-item [scrape]
  (let [prep (content->cities scrape)]
    (map (fn [[country city]]
           {:country (:name country)
            :country-url (:url country)
            :city (:name city)
            :city-url (:url city)})
         (partition 2 prep))))


;; dev
(debug-html *scrape*)
(debug-text *scrape*)
