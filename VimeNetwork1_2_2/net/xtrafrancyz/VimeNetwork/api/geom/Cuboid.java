/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork.api.geom;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import net.xtrafrancyz.VimeNetwork.api.geom.Vec3d;
import net.xtrafrancyz.VimeNetwork.api.geom.Vec3f;
import net.xtrafrancyz.VimeNetwork.api.geom.Vec3i;

public class Cuboid
implements Iterable<Vec3i> {
    protected Vec3i min;
    protected Vec3i max;

    public Cuboid() {
        this.min = new Vec3i();
        this.max = new Vec3i();
    }

    public Cuboid(Vec3i p1, Vec3i p2) {
        this.setBounds(p1, p2);
    }

    public void setBounds(Vec3i p1, Vec3i p2) {
        this.min = new Vec3i(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y), Math.min(p1.z, p2.z));
        this.max = new Vec3i(Math.max(p1.x, p2.x), Math.max(p1.y, p2.y), Math.max(p1.z, p2.z));
    }

    public void shift(Vec3i change) {
        this.min = this.min.add(change);
        this.max = this.max.add(change);
    }

    public void expand(Vec3i change) {
        if (change.x > 0) {
            this.max = this.max.setX(Math.max(this.max.x + change.x, this.min.x));
        } else if (change.x < 0) {
            this.min = this.min.setX(Math.min(this.min.x + change.x, this.max.x));
        }
        if (change.y > 0) {
            this.max = this.max.setY(Math.max(this.max.y + change.y, this.min.y));
        } else if (change.y < 0) {
            this.min = this.min.setY(Math.min(this.min.y + change.y, this.max.y));
        }
        if (change.z > 0) {
            this.max = this.max.setZ(Math.max(this.max.z + change.z, this.min.z));
        } else if (change.z < 0) {
            this.min = this.min.setZ(Math.min(this.min.z + change.z, this.max.z));
        }
    }

    public void contract(Vec3i change) {
        if (change.x > 0) {
            this.min = this.min.setX(Math.min(this.min.x + change.x, this.max.x));
        } else if (change.x < 0) {
            this.max = this.max.setX(Math.max(this.max.x + change.x, this.min.x));
        }
        if (change.y > 0) {
            this.min = this.min.setY(Math.min(this.min.y + change.y, this.max.y));
        } else if (change.y < 0) {
            this.max = this.max.setY(Math.max(this.max.y + change.y, this.min.y));
        }
        if (change.z > 0) {
            this.min = this.min.setZ(Math.min(this.min.z + change.z, this.max.z));
        } else if (change.z < 0) {
            this.max = this.max.setZ(Math.max(this.max.z + change.z, this.min.z));
        }
    }

    public void inset(Vec3i change) {
        this.min = this.min.add(change);
        this.max = this.max.subtract(change);
        this.fixOverlap();
    }

    public void outset(Vec3i change) {
        this.max = this.max.add(change);
        this.min = this.min.subtract(change);
        this.fixOverlap();
    }

    public Vec3i getMin() {
        return this.min;
    }

    public Vec3i getMax() {
        return this.max;
    }

    public Vec3f getCenter() {
        return new Vec3f(this.min.add(this.max)).divide(2.0f);
    }

    public int getWidth() {
        return this.max.x - this.min.x + 1;
    }

    public int getHeight() {
        return this.max.y - this.min.y + 1;
    }

    public int getLength() {
        return this.max.z - this.min.z + 1;
    }

    public int getArea() {
        return (this.max.x - this.min.x + 1) * (this.max.y - this.min.y + 1) * (this.max.z - this.min.z + 1);
    }

    public boolean contains(Vec3i vec) {
        return vec.x >= this.min.x && vec.x <= this.max.x && vec.y >= this.min.y && vec.y <= this.max.y && vec.z >= this.min.z && vec.z <= this.max.z;
    }

    public boolean contains(Vec3f vec) {
        return vec.x >= (float)this.min.x && vec.x < (float)(this.max.x + 1) && vec.y >= (float)this.min.y && vec.y < (float)(this.max.y + 1) && vec.z >= (float)this.min.z && vec.z < (float)(this.max.z + 1);
    }

    public boolean contains(Vec3d vec) {
        return vec.x >= (double)this.min.x && vec.x < (double)(this.max.x + 1) && vec.y >= (double)this.min.y && vec.y < (double)(this.max.y + 1) && vec.z >= (double)this.min.z && vec.z < (double)(this.max.z + 1);
    }

    public Vec3i size() {
        return this.max.add(this.min.invert());
    }

    public Cuboid asFlatCuboid() {
        return new Cuboid(this.min, this.max.setY(this.min.y));
    }

    public Cuboid clone() {
        return new Cuboid(this.min, this.max);
    }

    @Override
    public Iterator<Vec3i> iterator() {
        return new Iterator<Vec3i>(){
            private Vec3i min;
            private Vec3i max;
            private int nextX;
            private int nextY;
            private int nextZ;
            {
                this.min = Cuboid.this.getMin();
                this.max = Cuboid.this.getMax();
                this.nextX = this.min.x;
                this.nextY = this.min.y;
                this.nextZ = this.min.z;
            }

            @Override
            public boolean hasNext() {
                return this.nextX != Integer.MIN_VALUE;
            }

            @Override
            public Vec3i next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                Vec3i answer = new Vec3i(this.nextX, this.nextY, this.nextZ);
                if (++this.nextX > this.max.x) {
                    this.nextX = this.min.x;
                    if (++this.nextY > this.max.y) {
                        this.nextY = this.min.y;
                        if (++this.nextZ > this.max.z) {
                            this.nextX = Integer.MIN_VALUE;
                        }
                    }
                }
                return answer;
            }
        };
    }

    @Override
    public Spliterator<Vec3i> spliterator() {
        return Spliterators.spliterator(this.iterator(), (long)this.getArea(), 0);
    }

    public String toString() {
        return "Cuboid[" + this.min.x + " " + this.min.y + " " + this.min.z + " to " + this.max.x + " " + this.max.y + " " + this.max.z + "]";
    }

    public boolean equals(Object obj) {
        if (obj instanceof Cuboid) {
            Cuboid other = (Cuboid)obj;
            return other.min.equals(this.min) && other.max.equals(this.max);
        }
        return false;
    }

    private void fixOverlap() {
        int val;
        if (this.min.x > this.max.x) {
            val = (this.min.x + this.max.x) / 2;
            this.min.setX(val);
            this.max.setX(val);
        }
        if (this.min.y > this.max.y) {
            val = (this.min.y + this.max.y) / 2;
            this.min.setY(val);
            this.max.setY(val);
        }
        if (this.min.z > this.max.z) {
            val = (this.min.z + this.max.z) / 2;
            this.min.setZ(val);
            this.max.setZ(val);
        }
    }
}

