(ns github.checker
  (:require [tentacles.issues :as issues]))

(defn closed? [issue]
  (= "closed" (:state issue)))

(defn gh-exists? [issue]
  (not= 404 (:status issue)))

(defn get-issues [account repo issue-ids]
  (for [id issue-ids]
    (issues/specific-issue account repo id)))

(defn open-gh-issues
  [account repo & ids]
  (with-meta (fn [_]
               (for [issue (->> ids (get-issues account repo) (filter (and gh-exists? (complement closed?))))]
                 (format "<a href='%1s'>%2s</a>"
                         (:url issue)
                         (:title issue))))
    {:type :gh-blocker
     ::source '(~'open-gh-issues ~account ~repo & ~@ids)}))

(defmethod print-method :gh-blocker [o ^java.io.Writer w]
  (print-method (::source (meta o)) w))
