(require '[lumo.build.api :as b])


(defn build []
  (b/build
   (b/inputs "src")
   {:main 'muon.muon
    :output-to "main.js"
    :output-dir "out/"
    :optimizations :none
    :target :nodejs
    :infer-externs true}))

(defn release []
  (b/build
   (b/inputs "src")
   {:main 'muon.muon
    :output-to "main.js"
    :output-dir "out/"
    :optimizations :simple
    :target :nodejs
    :infer-externs true}))

(defn build-bin []
 (b/build
  (b/inputs "src")
  {:main 'muon.install
   :output-to "bin/muon.js"
   :output-dir "out/"
   :optimizations :simple
   :target :nodejs
   :infer-externs true})
  (.log js/console "Muon bin/executable compiled."))

(defn -main []
  (let [args js/process.argv
        command (last args)]
    (.log js/console "Exec script: " command)
    (case command
      "build-bin" (build-bin)
      "release" (do (release) (build-bin)))
      (build)))

(-main)



