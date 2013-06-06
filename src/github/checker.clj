(ns github.checker
  (:require [tentacles.issues :as issues]))

(def ^:dynamic *gh-account* "RedHatQE")
(def ^:dynamic *repo* "katello.auto")

(defn closed? [issue]
  (= "closed" (:state issue)))

(defn get-issues [issue-ids]
  (for [id issue-ids]
    (issues/specific-issue *gh-account* *repo* id)))

(defn open-gh-issues
  [ & ids]
  (with-meta (fn [_]
               (for [issue (->> ids get-issues (filter (complement closed?)))]
                 (format "<a href='%1s'>%2s</a>"
                         (:url issue)
                         (:title issue))))
    {:type :gh-blocker
     ::source '(~'open-gh-issues ~@ids)}))

(defmethod print-method :gh-blocker [o ^java.io.Writer w]
  (print-method (::source (meta o)) w))
