(ns nptr-clj.core-test
  (:require [clojure.test :refer :all]
            [nptr-clj.http :refer :all]))

(deftest test-get-file-extension
  (testing "Test file extension extraction from filename"
    (is (= (get-file-extension "test.tar.gz") ".gz"))))


(deftest test-get-file-extension-no-ext
  (testing "Test file extension extraction from filename without extension"
    (is (= (get-file-extension "test") ""))))
