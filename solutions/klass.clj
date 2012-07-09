;;; Exercise 1

;;; I'm using a new Clojure function: `assoc-in`. It lets you create a
;;; new map with a deeply nested value changes. You give it the path
;;; to the value you want replaced.

(def Klass
     (assoc-in Klass
               [:__instance_methods__ :to-string]
               (fn [class]
                 (str "class " (:__own_symbol__ class)))))


;;; Exercise 2


;; I'll mark classes invisible by tagging them with metadata.

(def invisible
     (fn [class]
       (assoc class :__invisible__ true)))

(def invisible?
     (fn [class-symbol] (:__invisible__ (eval class-symbol))))

;; Change the already-defined metaclasses to be invisible:

(def MetaAnything (invisible MetaAnything))
(def MetaKlass (invisible MetaKlass))
(def MetaPoint (invisible MetaPoint))

;; Ancestors just removes invisible classes from the
;; reversed lineage.

(def Klass
     (assoc-in Klass
               [:__instance_methods__ :ancestors]
               (fn [class]
                 (remove invisible?
                         (reverse (lineage (:__own_symbol__ class)))))))

;; New metaclasses need to be created to be invisible.

(def MetaKlass
     (assoc-in MetaKlass
               [:__instance_methods__ :new]
                (fn [this
                     new-class-symbol superclass-symbol
                     instance-methods class-methods]
                  ;; Metaclass
                  (install
                   ;; VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV     new
                   (invisible
                    ;; ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ 
                    (basic-class (metasymbol new-class-symbol)
                                 :left 'Klass
                                 :up 'MetaAnything
                                 class-methods)))
                  ;; Class
                  (install
                   (basic-class new-class-symbol
                                :left (metasymbol new-class-symbol)
                                :up superclass-symbol
                                instance-methods)))))


;; Test data:

(send-to Klass :new
         'ColoredPoint 'Point
         {
          :color :color
         
          :add-instance-values
          (fn [this x y color]
            (assoc (send-super this :add-instance-values x y)
              :color color))
         }
         {
          :origin (fn [class]
                    (send-to class :new 0 0 'white))
          })

(prn (send-to Anything :ancestors))     
(prn (send-to Klass :ancestors))     
(prn (send-to Point :ancestors))
(prn (send-to ColoredPoint :ancestors))


;;; Exercise 3

(def Anything
     (assoc-in Anything
               [:__instance_methods__ :class-name]
               (fn [this]
                 (first (send-to (eval (:__class_symbol__ this))
                                 :ancestors)))))

(def Anything
     (assoc-in Anything
               [:__instance_methods__ :class]
               (fn [this]
                 (eval (send-to this :class-name)))))



(prn (send-to Anything :class-name))     
(prn (send-to Klass :class-name))     
(prn (send-to Point :class-name))
(prn (send-to ColoredPoint :class-name))

