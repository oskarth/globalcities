(ns globalcities.core
  (:require [net.cgrand.enlive-html :as html]))

;; useful
(defmacro redir [filename & body]
  `(binding [*out* (clojure.java.io/writer ~filename)] ~@body))

(defn debug-html [seq]
  (redir "/tmp/foo.html" (print (apply str (html/emit* seq)))))

(defn debug-text [seq]
  (redir "/tmp/foo.txt" (print seq)))

;; faux wikipedia
(def ^:dynamic *url* "http://127.0.0.1:4567/global_cities.html")

;; this gets all alpha cities.
(def ^:dynamic *scrape* (nth (html/select (html/html-resource (java.net.URL. *url*)) [[:table]]) 1))

(defn link? [node] (= (:tag node) :a))

(defn create-linkmap [content]
  [{:name (:title (:attrs content))
     :url (:href (:attrs content))}])

(defn content->links
  "turns scrape content into a seq with country-city repeated"
  [content]
  (cond
   (link? content) (create-linkmap content)
   (map? content) (content->links (:content content))
   (coll? content) (mapcat content->links content)))

;; turns name-url list into structured with country and city put together
(defn create-item [scrape]
  (let [prep (content->links scrape)]
    (map (fn [[country city]]
           {:country (:name country)
            :country-url (:url country)
            :city (:name city)
            :city-url (:url city)})
         (partition 2 prep))))

;; dev
(debug-html *scrape*)
(debug-text *scrape*)


;; TODO: how to save / memoize a url page to avoid needless scraping?
;; TODO: create a list of all the jobs to request (coords / languages),
;; then connect this back data structure - agents/watches?
;; TODO: find a map
;; TODO: once you got coordinates, figure out how to map these to map
;; TODO: put debug stuff in my own utils-file? how import nicely?
;; TODO: grab betas
