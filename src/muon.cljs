(ns muon.muon
  (:require ["fs" :as fs]
            ["os" :as os]
            ["https" :as https]
            ["shelljs" :as shell]
            ["cljs.nodejs" :as nodejs]
            ["decompress" :as decompress]
            ["progress-download" :as download]))

(def host-name
  (str "api.github.com"))

(def path 
  (str "/repos/brave/muon/releases/latest"))

(def json (atom nil))

(defn os-name []
  (let [os (.platform os)]
      (re-pattern os)))

(defn find-package [assets]
    (loop [down nil size nil name nil lst assets]
      (if down
        #js{:name name :url down :size size}
        (do
          (let [url (aget (first lst) "browser_download_url")
                size (aget (first lst) "size")
                name (aget (first lst) "name")
                os (re-find (os-name) url)
                symbols (re-find #"symbols" url)]

            (if (and os (not symbols))
              (recur url size name (rest lst))
              (recur nil size name (rest lst))))))))

(defn save-package [pkg]
  (let [path (str "vendor/" (aget pkg "name"))
        file (.createWriteStream fs path)]
      (.pipe (download (aget pkg "url") "vendor/" #js{:extract true}) file)))

(defn json-start [chunk]
  (reset! json (str @json chunk)))

(defn json-end []
  (let [jsonp (.parse js/JSON @json)
        pkg (find-package (aget jsonp "assets"))]
    (save-package pkg)))

(defn fetch-assets []
  (->
   (.get https #js{:hostname host-name
                   :headers #js{:User-Agent "Muon/DWNLD Agent for B42 Browser."}
                   :path path
                   :method "GET"}
         (fn[res] 
           (.on res "data" (fn[chunk] (json-start chunk)))
           (.on res "end" json-end)))
   (.on "error" (fn[err](.log js/console "Error: " err)))))


(fetch-assets)

