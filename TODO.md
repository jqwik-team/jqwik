-  Allow exhaustive generation
   - Optional<ExhaustiveGenerator> Arbitrary.exhaustiveGenerator
   - Property.generation = GenerationMode.EXHAUSTIVE|RANDOMIZED|AUTO
   - PropertyConfiguration.generation
   - ExhaustiveGenerator<T> extends Iterator<T> {
        long maxCount();
     }
     - BooleanArbitrary
     - DefaultShort|Byte|LongArbitrary
     - ExamplesArbitrary
     - MappedArbitrary
     - FilteredArbitrary
     - FlatMappedArbitrary
   - ExhaustiveShrinkablesGenerator implements ShrinkablesGenerator
   - User Guide

