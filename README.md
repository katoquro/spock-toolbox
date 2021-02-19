# Spock Toolbox

Toolbox is a set of small utilities to make Spock Framework Specifications more readable and easier to write

## Getting Started

Get artifact from
[Maven Central Repo](https://search.maven.org/search?q=g:%22com.ainrif.gears%22%20a:%22spock-toolbox%22)

- group: `com.ainrif.gears`
- artifact: `spock-toolbox`
- version: [![Version][artifact_img]][artifact]

[artifact_img]: https://img.shields.io/maven-central/v/com.ainrif.gears/spock-toolbox?color=blue&style=flat-square
[artifact]: https://search.maven.org/search?q=g:%22com.ainrif.gears%22

## Usage

There are several "tools" which are accessible through the one entry point
`com.ainrif.gears.spock_toolbox.SpockToolbox`

### Replicator

Replicator creates objects based on given settings and check that there are no extra ~~atoms~~
setters which weren't used

```groovy
def pogo = SpockToolbox.replicate(CustomPogoClass) {
    pogoStringField = 'value'
    pogoIntField = 42
}

assert pogo.pogoStringField == 'value'
assert pogo.pogoIntField == 42

// throws exception about there are fields (maybe after new feature or refactoring)
// which were not set throug the initialization
def pogo = SpockToolbox.replicate(CustomPogoClass) {
    pogoStringField = 'value'
}
```

### Tricorder

Tricorder analyzes given objects

#### Reflection equals

To compare two objects toolbox provides `reflects` method. You can use it in assertion stanza.

```groovy
// Spock Specification on Groovy  
then:
SpockToolbox.reflects(actual, expected)

// Java assertion
assert SpockToolbox.reflects(actual, expected).asBoolean()
```

#### Exclude fields from comparison

Sometimes objects have dynamic fields (like time, date, id) and should be excluded from comparison.

```groovy
SpockToolbox.reflects(actual, expected)
        .excludeField('fieldToxclude')
        .excludeField('nestedObject.fieldToExclude')
```

#### Exclude map keys and array items from comparison

There is special syntax to exclude map keys and array items by index

```groovy
SpockToolbox.reflects(actual, expected)
        .excludeField('mapField.keyToExclude')
        .excludeField('arrayField.1')
        .excludeField('arrayField.2')
        .excludeField('arrayField') // to exclude the whole array
``` 

If some fields are undefined are dynamic they can be replaced with wildcard `*`

```groovy
SpockToolbox.reflects(actual, expected)
        .excludeField('a.*.c') // a.b.c, a.d.c etc. will be excluded
        .excludeField('a.b.*') // the same as to exclude a.b
``` 

#### Custom comparator for objects

Some rules of comparison can be override with custom comparators. 
Custom comparators must implement `org.unitils.reflectionassert.comparator.Comparator` 
and set into the reflection builder:

```groovy
SpockToolbox.reflects(actual, expected)
        .comparator(new CustomComparator(settings))
```    

#### Predefined comparators (a.k.a comparison `modes`)

Toolbox already has some predefined comparators which are singletons and can be used via `mode` method. 
Modes usually are immutable and must have default constructor. 
Every customization produces new `Comparator` which can be used to override the previous mode.

Modes can be found at package: `com.ainrif.gears.spock_toolbox.comparator`

1. `DOUBLE_SCALE` - relaxed comparison for doubles with given scale (default is `1e-14`)
1. `IGNORE_DEFAULTS` - relaxed comparison for null and default values of primitive types
1. `IGNORE_TIME_DIFF` - objects with diff only in time are equal, also supports jsr310
1. `STRICT_ORDER` - validated orders for ordered collections
1. `IGNORE_ABSENT_EXPECTED_FIELDS` - if _expected_ is superclass of _actual_ absent fields won't generate difference

```groovy
SpockToolbox.reflects(actual, expected)
        .mode(STRICT_ORDER)
        .comparator(DOUBLE_SCALE.scale(1e-2d))
```   