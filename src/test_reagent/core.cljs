(ns test-reagent.core
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [reagent.core :refer [track cursor] :as r]))

(enable-console-print!)

(defonce app-state* (r/atom {:count 0 :show-counter-with-state true}))
(def count* (cursor app-state* [:count]))
(def show-counter-with-state* (cursor app-state* [:show-counter-with-state]))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

(defn counter-simple []
  [:div (str @count* " simple")])

;; only this one do not work
(defn counter-with-let-and-fn []
  (let [display-value (str @count* " with let and fn (will not work)")]
    (fn []
      [:div display-value])))

(defn counter-with-let []
  (let [display-value (str @count* " with let, but without fn")]
    [:div display-value]))

(defn counter-with-track []
  (let [display-value (track (fn [] (str @count* " with track")))]
    (fn []
      [:div @display-value])))

(defn counter-with-reaction []
  (let [display-value (reaction (str @count* " with reaction"))]
    (fn []
      [:div @display-value])))

(defn formatting-fn [v*]
  (str @v* " with formatting-fn"))

(defn counter-with-formatting-fn []
  [:div (formatting-fn count*)])

(defn inc-button []
  [:button {:on-click #(swap! count* inc)} "+1"])

(defn counter-with-state [id]
  (let [state (r/atom 0)]
    (add-watch
     state
     :watch
     (fn [_ _ _ new-state]
       (println (str "Atom " id " changed: " new-state))))
    (fn []
      [:div
       [:div @state]
       [:button {:on-click #(swap! state inc)} "+1"]])))

(defn app []
  [:div
   [inc-button]
   [counter-simple]
   [counter-with-let-and-fn]
   [counter-with-let]
   [counter-with-track]
   [counter-with-reaction]
   [counter-with-formatting-fn]
   [:hr]
   [:div "add-watch test"]
   [:button {:on-click #(swap! show-counter-with-state* not)} "toggle"]
   (when @show-counter-with-state* [counter-with-state "id1"])])

(r/render [app] (.getElementById js/document "app"))