(ns github.checker
  (:require [tentacles.issues :as issues]
            [slingshot.slingshot :refer [throw+]]))

(defrecord Issue [account repo number])

(defn closed? [issue]
  (= "closed" (:state issue)))

(defn exists? [issue]
  (not= 404 (:status issue)))

(defn get-issue
  ([account repo number]
     (get-issue (Issue. account repo number)))
  ([issue]
     (let [server-issue (issues/specific-issue (:account issue) (:repo issue) (:number issue))]
       (if (exists? server-issue)
         (merge issue server-issue)
         (throw+ {:type ::not-found,
                  :issue issue
                  :response server-issue})))))

(defn link [issue]
  (format "<a href='%1s'>%2s</a>"
          (:url issue)
          (:title issue)))

