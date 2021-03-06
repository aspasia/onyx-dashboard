(ns onyx-dashboard.state-query
  (:require [lib-onyx.replica-query :as rq]))

(defn message-id [deployment]
  (or (:time-travel-message-id deployment)
      (:message-id-max deployment)))

(defn deployment->latest-replica [deployment]
  (get-in deployment [:replica-states (message-id deployment) :replica] {}))

(defn deployment->latest-entry [deployment]
  (get-in deployment [:replica-states (message-id deployment) :entry] {}))

(defn job-info->task-hosts [replica job-info]
  (let [{:keys [task-name->id id]} job-info
                job-state (rq/job-state replica id)]
    (if (not= :running job-state) 
      {}
      (zipmap (keys task-name->id)
              (map (fn [task-id]
                     (rq/task->peer-sites replica id task-id))
                   (vals task-name->id))))))
