package org.ditw.rschr.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dev on 2018-06-01.
 */
public class VarianceTestsJ {

    public static abstract class Animal {
        protected Animal(String n) {
            _name = n;
        }
        private String _name;
        public String getName() {
            return _name;
        }
        public abstract String name();
    }

    public static class Dog extends Animal {
        public Dog(String n) {
            super(n);
        }
        @Override
        public String name() {
            return "Dog: " + getName();
        }
    }

    public static class Cat extends Animal {
        public Cat(String n) {
            super(n);
        }
        @Override
        public String name() {
            return "Cat: " + getName();
        }
    }

    private static void traceAnimals(List<Animal> animals) {
        for (Animal a : animals) {
            System.out.println(a.getName());
        }
    }

    public static void main(String[] args) {
        List<Animal> animals = new ArrayList<>();

        animals.add(new Dog("d1"));
        animals.add(new Dog("d2"));

        traceAnimals(animals);

        List<Dog> dogs = new ArrayList<>();
        dogs.add(new Dog("d1"));
        dogs.add(new Dog("d2"));
        //do not compile: traceAnimals(dogs);

    }
}
