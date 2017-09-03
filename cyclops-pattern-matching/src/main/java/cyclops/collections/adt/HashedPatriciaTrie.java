package cyclops.collections.adt;

import cyclops.patterns.CaseClass1;
import cyclops.patterns.CaseClass2;
import cyclops.patterns.Sealed4;
import cyclops.stream.ReactiveSeq;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple1;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

public interface HashedPatriciaTrie<K, V>  {

    static final int BITS = 5;
    static final int BUCKET_SIZE = 1 << BITS;
    static final int MASK = (1 << BITS) - 1;
    static final Node[] EMPTY_ARRAY = createBaseEmptyArray();

    static <K,V> Node<K,V> empty(){
        return EmptyNode.Instance;
    }
    static Node[] createBaseEmptyArray() {
        Node[] emptyArray = new Node[BUCKET_SIZE];
        Arrays.fill(emptyArray, EmptyNode.Instance);
        return emptyArray;
    }

    static <K, V> Node<K, V>[] emptyArray() {
        return Arrays.copyOf((Node<K, V>[]) EMPTY_ARRAY, BUCKET_SIZE);
    }




    interface Node<K, V> extends Sealed4<EmptyNode<K,V>,SingleNode<K,V>,CollisionNode<K,V>,ArrayNode<K,V>> {

        boolean isEmpty();

        int size();

        Node<K, V> put(int hash, K key, V value);

        Optional<V> get(int hash, K key);

        Node<K, V> minus(int hash, K key);


    }

    static class EmptyNode<K,V> implements Node<K,V>{

        static EmptyNode Instance = new EmptyNode();
        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public Node<K, V> put(int hash, K key, V value) {
            if (hash == 0) {
                return new SingleNode<>(key, value);
            } else {
                int newHash = hash >>> BITS;
                int index = hash & MASK;
                Node<K, V>[] nodes = emptyArray();
                if (newHash == 0) {
                    nodes[0] = this;
                    nodes[index] = new SingleNode<>(key, value);
                } else {
                    nodes[index] = Instance.put(newHash, key, value);
                    if (index != 0) {
                        nodes[0] = this;
                    }
                }
                return new ArrayNode<>(nodes);
            }
        }

        @Override
        public Optional<V> get(int hash, K key) {
            return Optional.empty();
        }

        @Override
        public Node<K, V> minus(int hash, K key) {
            return this;
        }

        @Override
        public <R> R match(Function<? super EmptyNode<K, V>, ? extends R> fn1, Function<? super SingleNode<K, V>, ? extends R> fn2, Function<? super CollisionNode<K, V>, ? extends R> fn3, Function<? super ArrayNode<K, V>, ? extends R> fn4) {
            return fn1.apply(this);
        }
    }
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    static class SingleNode<K, V> implements Node<K, V>, CaseClass2<K,V> {

        private final K key;
        private final V value;


        private SingleNode(Tuple2<K,V> keyAndValue) {
            key = keyAndValue.v1;
            value = keyAndValue.v2;
        }
        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int size() {
            return 1;
        }

        LazyList<Tuple2<K,V>> bucket(){
            return LazyList.of(Tuple.tuple(key,value));
        }

        @Override
        public Node<K, V> put(int hash, K key, V value) {
            if (hash == 0) {
                return new CollisionNode<>(bucket().prepend(Tuple.tuple(key, value)));
            } else {
                int newHash = hash >>> BITS;
                int index = hash & MASK;
                Node<K, V>[] nodes = emptyArray();
                if (newHash == 0) {
                    nodes[0] = this;
                    nodes[index] = new SingleNode<>(key, value);
                } else {
                    nodes[index] = EmptyNode.Instance.put(newHash, key, value);
                    if (index != 0) {
                        nodes[0] = this;
                    } else {
                        nodes[0] = nodes[0].put(0, this.key, this.value);
                    }
                }
                return new ArrayNode<>(nodes);
            }
        }

        @Override
        public Optional<V> get(int hash, K key) {
            if(hash==0 && this.key.equals(key))
                return Optional.of(value);
            return Optional.empty();

        }



        @Override
        public Node<K, V> minus(int hash, K key) {
            if(hash==0 && this.key.equals(key))
                return EmptyNode.Instance;
            return this;
        }


        @Override
        public <R> R match(Function<? super EmptyNode<K, V>, ? extends R> fn1, Function<? super SingleNode<K, V>, ? extends R> fn2, Function<? super CollisionNode<K, V>, ? extends R> fn3, Function<? super ArrayNode<K, V>, ? extends R> fn4) {
            return fn2.apply(this);
        }

        @Override
        public Tuple2<K, V> unapply() {
            return Tuple.tuple(key,value);
        }
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    static class CollisionNode<K, V> implements Node<K, V>, CaseClass1<LazyList<Tuple2<K,V>>> {

        private final LazyList<Tuple2<K, V>> bucket;

        @Override
        public boolean isEmpty() {
            return bucket.isEmpty();
        }

        @Override
        public int size() {
            return bucket.size();
        }

        @Override
        public Node<K, V> put(int hash, K key, V value) {
            if (hash == 0) {
                return new CollisionNode<>(bucket.filter(p -> !p.v1.equals(key)).prepend(Tuple.tuple(key, value)));
            } else {
                int newHash = hash >>> BITS;
                int index = hash & MASK;
                Node<K, V>[] nodes = emptyArray();
                if (newHash == 0) {
                    nodes[0] = this;
                    nodes[index] = new SingleNode<>(key, value);
                } else {
                    nodes[index] = EmptyNode.Instance.put(newHash, key, value);
                    if (index != 0) {
                        nodes[0] = this;
                    } else {
                        bucket.iterable().forEach(t2 -> nodes[0] = nodes[0].put(0, t2.v1(), t2.v2()));
                    }
                }
                return new ArrayNode<>(nodes);
            }
        }

        @Override
        public Optional<V> get(int hash, K key) {
            return (hash == 0)
                    ? bucket.filter(t2 -> t2.v1.equals(key)).get(0).map(Tuple2::v2)
                    : Optional.empty();
        }



        @Override
        public Node<K, V> minus(int hash, K key) {
            if (hash != 0)
                return this;

            LazyList<Tuple2<K, V>> newBucket = bucket.filter(t2 -> !t2.v1.equals(key));
            return newBucket.match(c->c.size()>1? new CollisionNode<K,V>(newBucket) : new SingleNode<>(newBucket.get(0).get()),nil->  HashedPatriciaTrie.empty());
        }


        @Override
        public <R> R match(Function<? super EmptyNode<K, V>, ? extends R> fn1, Function<? super SingleNode<K, V>, ? extends R> fn2, Function<? super CollisionNode<K, V>, ? extends R> fn3, Function<? super ArrayNode<K, V>, ? extends R> fn4) {
            return fn3.apply(this);
        }

        @Override
        public Tuple1<LazyList<Tuple2<K, V>>> unapply() {
            return Tuple.tuple(bucket);
        }
    }

    static class ArrayNode<K, V> implements Node<K, V>, CaseClass1<Node<K,V>[]> {
        private final Node<K, V>[] nodes;

        private ArrayNode(Node<K, V>[] nodes) {
            this.nodes = nodes;
        }

        @Override
        public Node<K, V> put(int hash, K key, V value) {
            int newHash = hash >>> BITS;
            int index = hash & MASK;
            Node<K, V>[] newNodes = Arrays.copyOf(nodes, nodes.length);
            newNodes[index] = nodes[index].put(newHash, key, value);
            return new ArrayNode<>(newNodes);
        }

        @Override
        public Optional<V> get(int hash, K key) {
            int newHash = hash >>> BITS;
            int index = hash & MASK;
            return nodes[index].get(newHash, key);
        }

        @Override
        public Node<K, V> minus(int hash, K key) {
            int newHash = hash >>> BITS;
            int index = hash & MASK;
            Node<K, V> node = nodes[index];
            if (node.isEmpty()) {
                return this;
            } else {
                Node<K, V> newNode = node.minus(newHash, key);
                if (newNode == node) {
                    return this;
                } else {
                    Node<K, V>[] newNodes = Arrays.copyOf(nodes, nodes.length);
                    newNodes[index] = newNode;
                    Node<K, V> branch = new ArrayNode<>(newNodes);
                    return branch.isEmpty() ? EmptyNode.Instance : branch;
                }
            }

        }
        @Override
        public boolean isEmpty() {
            return ReactiveSeq.of(nodes).anyMatch(Node::isEmpty);
        }

        @Override
        public int size() {
            return ReactiveSeq.of(nodes).sumInt(Node::size);
        }



        @Override
        public <R> R match(Function<? super EmptyNode<K, V>, ? extends R> fn1, Function<? super SingleNode<K, V>, ? extends R> fn2, Function<? super CollisionNode<K, V>, ? extends R> fn3, Function<? super ArrayNode<K, V>, ? extends R> fn4) {
            return fn4.apply(this);
        }

        @Override
        public Tuple1<Node<K, V>[]> unapply() {
            return Tuple.tuple(nodes);
        }
    }


}
