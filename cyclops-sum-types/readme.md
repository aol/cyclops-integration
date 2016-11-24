# Cyclops Sum Types

cyclops-sum-types defines Either implementations from Either through Either4 (to be extended).

All Either implementations have the following features


1. Right biased for ease of use
1. Rich API
1. Totally lazy
1. Tail recursive map / flatMap methods
1. Interopability via Reactive Streams Publisher - all Eithers implement Publisher
1. Accept Publisher in API calls (flatMapPublisher)
1. Interopability via Iterable - all Eithers implement Iterable
1. Accept Iterable in API calls (flatMapIterable)
1. sequence, traverse, accumulate operators

Either extends cyclops-react Xor, providing a lazy and tail call optimized alternative.

# Naming conventions

### Either 
Left,Right

### Either3
Left1,Left2,Right

### Either4
Left1,Left2,Left3,Right
