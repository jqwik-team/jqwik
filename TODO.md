-  Allow exhaustive generation
   - Optional<ExhaustiveGenerator> Arbitrary.exhaustiveGenerator
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
   - Report actual generationMode
   - User Guide

