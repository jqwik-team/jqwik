-  Allow exhaustive generation
   - Introduce Arbitrary.exhaustiveGenerator
   - Property.generation = GenerationMode.EXHAUSTIVE|RANDOMIZED|AUTO
   - ExhaustiveGenerator<T> implements Iterator<T>
   - ExhaustiveShrinkablesGenerator implements ShrinkablesGenerator
   - User Guide

