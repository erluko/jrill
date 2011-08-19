(ns jrill.core
  (:refer-clojure :exclude [get])
  (:require [clojure.java.io :as io])
  (:import (org.apache.commons.jxpath BasicVariables
                                      JXPathContext
                                      CompiledExpression
                                      Function)
           (org.apache.commons.jxpath.xml DOMParser)))

(defn- variables
  [amap]
  (let [vs (BasicVariables.)]
    (doseq [[k v] amap]
      (.declareVariable vs (name k) v))
    vs))

(defn- jxpath-function
  "Create a JXPath function that calls CLJFN with the
ExpressionContext and args as restargs."
  [cljfn]
  (reify Function
    (invoke [_ expr-ctx params]
      (apply cljfn expr-ctx params))))

(defn context
  "Return a XPath context for OBJ."
  ([obj]
     (if (instance? JXPathContext obj)
       obj
       (JXPathContext/newContext obj)))
  ([obj vars]
     (doto (context obj)
       (.setVariables (variables vars)))))

(defn xpath
  "Return a compiled XPath for XPATH."
  [xpath]
  (if (instance? CompiledExpression xpath)
    xpath
    (JXPathContext/compile xpath)))

(defn lift-ctx-xpath
  "Treat F's first two args as JXPath context specifier and XPath
expression, respectively."
  [f]
  (fn [ctx an-xpath & args]
    (apply f (context ctx) (xpath an-xpath) args)))

(defmacro defxpathfn
  [nom & defn-forms]
  `(do (defn ~nom ~@defn-forms)
       (alter-var-root #'~nom lift-ctx-xpath)))

(defxpathfn get
  "Retrieve a single element from CTX by XPATH.  Further values are
discarded."
  [ctx an-xpath]
  (.getValue an-xpath ctx))

(defxpathfn gets
  "Retrieve all matching elements from CTX by XPATH.  Further values
are discarded."
  [ctx an-xpath]
  (iterator-seq (.iterate an-xpath ctx)))
