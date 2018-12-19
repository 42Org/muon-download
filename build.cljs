(require '[lumo.build.api :as b])

(b/build
 (b/inputs "src")
 {:main muon
  :output-to "main.js"
  :output-dir "out/"
  :optimizations :simple
  :target :nodejs
  :infer-externs true})
