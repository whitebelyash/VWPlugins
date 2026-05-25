package org.slf4j.helpers;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.slf4j.Marker;

public class BasicMarker implements Marker {
   private static final long serialVersionUID = 1803952589649545191L;
   private final String name;
   private List referenceList;
   private static String OPEN = "[ ";
   private static String CLOSE = " ]";
   private static String SEP = ", ";

   BasicMarker(String name) {
      if (name == null) {
         throw new IllegalArgumentException("A marker name cannot be null");
      } else {
         this.name = name;
      }
   }

   public String getName() {
      return this.name;
   }

   public synchronized void add(Marker reference) {
      if (reference == null) {
         throw new IllegalArgumentException("A null value cannot be added to a Marker as reference.");
      } else if (!this.contains(reference)) {
         if (!reference.contains((Marker)this)) {
            if (this.referenceList == null) {
               this.referenceList = new Vector();
            }

            this.referenceList.add(reference);
         }
      }
   }

   public synchronized boolean hasReferences() {
      return this.referenceList != null && this.referenceList.size() > 0;
   }

   public boolean hasChildren() {
      return this.hasReferences();
   }

   public synchronized Iterator iterator() {
      if (this.referenceList != null) {
         return this.referenceList.iterator();
      } else {
         List<Marker> emptyList = Collections.emptyList();
         return emptyList.iterator();
      }
   }

   public synchronized boolean remove(Marker referenceToRemove) {
      if (this.referenceList == null) {
         return false;
      } else {
         int size = this.referenceList.size();

         for(int i = 0; i < size; ++i) {
            Marker m = (Marker)this.referenceList.get(i);
            if (referenceToRemove.equals(m)) {
               this.referenceList.remove(i);
               return true;
            }
         }

         return false;
      }
   }

   public boolean contains(Marker other) {
      if (other == null) {
         throw new IllegalArgumentException("Other cannot be null");
      } else if (this.equals(other)) {
         return true;
      } else {
         if (this.hasReferences()) {
            for(Marker ref : this.referenceList) {
               if (ref.contains(other)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public boolean contains(String name) {
      if (name == null) {
         throw new IllegalArgumentException("Other cannot be null");
      } else if (this.name.equals(name)) {
         return true;
      } else {
         if (this.hasReferences()) {
            for(Marker ref : this.referenceList) {
               if (ref.contains(name)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (!(obj instanceof Marker)) {
         return false;
      } else {
         Marker other = (Marker)obj;
         return this.name.equals(other.getName());
      }
   }

   public int hashCode() {
      return this.name.hashCode();
   }

   public String toString() {
      if (!this.hasReferences()) {
         return this.getName();
      } else {
         Iterator<Marker> it = this.iterator();
         StringBuilder sb = new StringBuilder(this.getName());
         sb.append(' ').append(OPEN);

         while(it.hasNext()) {
            Marker reference = (Marker)it.next();
            sb.append(reference.getName());
            if (it.hasNext()) {
               sb.append(SEP);
            }
         }

         sb.append(CLOSE);
         return sb.toString();
      }
   }
}
