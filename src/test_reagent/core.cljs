(ns test-reagent.core
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [reagent.core :refer [track cursor] :as r]))

(enable-console-print!)

(defonce app-state* (r/atom {:count 0}))
(def count* (cursor app-state* [:count]))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

(defn counter-simple []
  [:div (str @count*)])

;; only this one do not work
(defn counter-with-fn []
  (let [display-value (str @count* " will not work")]
    (fn []
      [:div display-value])))

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

(defn app []
  [:div
   [inc-button]
   [counter-simple]
   [counter-with-fn]
   [counter-with-track]
   [counter-with-reaction]
   [counter-with-formatting-fn]])

(r/render app (.getElementById js/document "app"))