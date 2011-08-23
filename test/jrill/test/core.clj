(ns jrill.test.core
  (:refer-clojure :exclude [get])
  (:require [jrill.core :as jrill])
  (:use [clojure.test]))

(deftest gets-string-int-methods
  (is (= '("hashCode" "compareTo" "compareTo"
	   "indexOf" "indexOf" "indexOf"
	   "indexOf" "codePointAt"
	   "codePointBefore" "codePointCount"
	   "compareToIgnoreCase" "lastIndexOf"
	   "lastIndexOf" "lastIndexOf"
	   "lastIndexOf" "length"
	   "offsetByCodePoints")
	 (jrill/gets String "methods[returnType='int']/name"))))


(deftest gets-map
  (is (= '("A" "C")
	 (jrill/gets
		 {"a" {"f" 1 "n" "A"}
		  "b" {"f" 0 "n" "B"}
		  "c" {"f" 1 "n" "C"}}
		 "/*[f=1]/n"))))

