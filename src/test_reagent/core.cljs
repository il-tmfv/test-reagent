(ns test-reagent.core
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [reagent.core :refer [track cursor] :as r]))

(enable-console-print!)

(defonce app-state* (r/atom {:count 0 :show-counter-with-state true :second-id "id2"}))
(def count* (cursor app-state* [:count]))
(def show-counter-with-state* (cursor app-state* [:show-counter-with-state]))
(def second-id* (cursor app-state* [:second-id]))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

(defn counter-with-with-let [c]
  (r/with-let [inited-str "inited in with-let"]
    [:div (str c " with with-let (passes prop) - " inited-str)]))

(defn wrapper-for-counter-with-with-let []
  [counter-with-with-let @count*])

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

(defn counter-with-watcher-for-prop [id value*]
  ; with-let is important here, need to call remove-watch coz value came from the outside
  (r/with-let [_ (add-watch
                  value*
                  :watch
                  (fn [_ _ _ new-state]
                    (println (str "Atom " id " changed: " new-state))))]
    [:div
     [:div @value*]
     [:button {:on-click #(swap! value* inc)} "+1"]]
    (finally
      (remove-watch value* :watch))))

(defn id-increment []
  [:button {:on-click #(swap! second-id* str "+")} "inc id2"])

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
   [id-increment]
   (when @show-counter-with-state* [counter-with-state "id1"])
   (when @show-counter-with-state* [counter-with-watcher-for-prop @second-id* count*])
   [:hr]
   [:div "with-let test"]
   [wrapper-for-counter-with-with-let]
   ])

(r/render [app] (.getElementById js/document "app"))