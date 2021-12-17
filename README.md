# TriteJection

___
Dependency injection for java.

###

maven repository:

```xml

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
<dependency>
    <groupId>com.github.TryDev07</groupId>
    <artifactId>TriteJection</artifactId>
    <version>-SNAPSHOT</version>
</dependency>
</dependencies>
```

###

Examples of your module class:

```java
public class ExampleModule extends TriteJectionModule {

    @Override
    public void bindings() {
        bind(ExampleClass.class).asEagerSingleton();

        bind(ExampleTestClass.class).toInstance(new ExampleTestClass("hello world")).asEagerSingleton();
        bind(ExampleTestClass.class).asEagerSingleton();

        bind(ExampleInterface.class).annotatedWith("ExampleBicycle").to(new ExampleBycicle()).asEagerSingleton();
    }
}
```

###

Example of initialization:

```java
public class Example {

    public static void main(String[] args) {
        TriteJection.createTriteJection(new ExampleModule(), new ExampleModule2());
    }
}
```

###

Field injection in a class:

```java
public class ExampleTestClass {

    @TriteJect private ExampleClass exampleClass;

    public ExampleTestClass(String input) {
        System.out.println(input);
    }

    public void doSomething() {
        exampleClass.run();
    }
}
```

###

Constructor injection in a class:

```java
public class ExampleTestClass {

    @TriteJect
    public ExampleTestClass(ExampleClass exampleClass) {
        exampleClass.doSomething();
    }

    public void doSomething() {
        exampleClass.run();
    }
}
```
