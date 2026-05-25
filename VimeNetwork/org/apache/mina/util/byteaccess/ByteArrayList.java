package org.apache.mina.util.byteaccess;

import java.util.NoSuchElementException;

class ByteArrayList {
   private final Node header = new Node();
   private int firstByte;
   private int lastByte;

   protected ByteArrayList() {
   }

   public int lastByte() {
      return this.lastByte;
   }

   public int firstByte() {
      return this.firstByte;
   }

   public boolean isEmpty() {
      return this.header.next == this.header;
   }

   public Node getFirst() {
      return this.header.getNextNode();
   }

   public Node getLast() {
      return this.header.getPreviousNode();
   }

   public void addFirst(ByteArray ba) {
      this.addNode(new Node(ba), this.header.next);
      this.firstByte -= ba.last();
   }

   public void addLast(ByteArray ba) {
      this.addNode(new Node(ba), this.header);
      this.lastByte += ba.last();
   }

   public Node removeFirst() {
      Node node = this.header.getNextNode();
      this.firstByte += node.ba.last();
      return this.removeNode(node);
   }

   public Node removeLast() {
      Node node = this.header.getPreviousNode();
      this.lastByte -= node.ba.last();
      return this.removeNode(node);
   }

   protected void addNode(Node nodeToInsert, Node insertBeforeNode) {
      nodeToInsert.next = insertBeforeNode;
      nodeToInsert.previous = insertBeforeNode.previous;
      insertBeforeNode.previous.next = nodeToInsert;
      insertBeforeNode.previous = nodeToInsert;
   }

   protected Node removeNode(Node node) {
      node.previous.next = node.next;
      node.next.previous = node.previous;
      node.removed = true;
      return node;
   }

   public class Node {
      private Node previous;
      private Node next;
      private ByteArray ba;
      private boolean removed;

      private Node() {
         this.previous = this;
         this.next = this;
      }

      private Node(ByteArray ba) {
         if (ba == null) {
            throw new IllegalArgumentException("ByteArray must not be null.");
         } else {
            this.ba = ba;
         }
      }

      public Node getPreviousNode() {
         if (!this.hasPreviousNode()) {
            throw new NoSuchElementException();
         } else {
            return this.previous;
         }
      }

      public Node getNextNode() {
         if (!this.hasNextNode()) {
            throw new NoSuchElementException();
         } else {
            return this.next;
         }
      }

      public boolean hasPreviousNode() {
         return this.previous != ByteArrayList.this.header;
      }

      public boolean hasNextNode() {
         return this.next != ByteArrayList.this.header;
      }

      public ByteArray getByteArray() {
         return this.ba;
      }

      public boolean isRemoved() {
         return this.removed;
      }
   }
}
