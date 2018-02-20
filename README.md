# Spock Toolbox

Toolbox is a set of small utilities to make Spock Framework Specifications 
more readable and easier to write

[TOC]

## Getting Started

Get artifact from bintray repo [https://dl.bintray.com/ainrif/maven](https://dl.bintray.com/ainrif/maven)

- group: `com.ainrif.gears`
- artifact: `spock-toolbox`
- version: [ ![Download](https://api.bintray.com/packages/ainrif/maven/spock-toolbox/images/download.svg) ](https://bintray.com/ainrif/maven/spock-toolbox/_latestVersion)

You can use `Set Me Up!` guide from 
[bintray repo](https://bintray.com/ainrif/maven/spock-toolbox)

## Usage

There are several "tools" which are accessible through the one entry point
`com.ainrif.gears.spock_toolbox.SpockToolbox` 

### Replicator 

Replicator creates objects based on given settings and check 
that there are no extra ~~atoms~~ setters which weren't used

```
#!groovy
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

To compare two objects toolbox provides `reflects` method.
You can use it in assertion stanza. 
  
```
#!groovy
// Spock Specification on Groovy  
then:
SpockToolbox.reflects(actual, expected)

// Java assertion
assert SpockToolbox.reflects(actual, expected).asBoolean()
```

#### Exclude fields from comparison

Sometimes objects have dynamic fields (like time, date, id) 
and should be excluded from comparison. 

```
#!groovy
SpockToolbox.reflects(actual, expected)
    .excludeField('fieldToxclude')
    .excludeField('nestedObject.fieldToExclude')
```

#### Exclude map keys and array items from comparison

There is special syntax to exclude map keys and array items by index

```
#!groovy
SpockToolbox.reflects(actual, expected)
    .excludeField('mapField[keyToExclude]')
    .excludeField('arrayField[1]')
    .excludeField('arrayField[2]')
    .excludeField('arrayField') // to exclude the whole array
``` 

#### Custom comparator for objects

Some rules of comparison can be override with custom comparators.
Custom comparators must implement `org.unitils.reflectionassert.comparator.Comparator`
and set into the reflection builder: 

```
#!groovy
SpockToolbox.reflects(actual, expected)
    .comparator(new CustomComparator(settings))
```    

#### Predefined comparators (a.k.a comparison `modes`)

Toolbox already have some predefined comparators which are singletons and 
can be used via `mode` method.
Modes usually are immutable and must have default constructor. 
Every customization produces new `Comparator` which can be used to override mode.

Modes can be found at package: `com.ainrif.gears.spock_toolbox.comparator`

1. DOUBLE_SCALE - relaxed comparison for doubles with given scale (default is `1e-14`)
1. IGNORE_DEFAULTS - relaxed comparison for null and default values of primitive types
1. IGNORE_TIME_DIFF - objects with diff only in time are equal, also supports jsr310
1. STRICT_ORDER - validated orders for ordered collections

```
#!groovy
SpockToolbox.reflects(actual, expected)
    .mode(STRICT_ORDER)
    .comparator(DOUBLE_SCALE.scale(1e-2d))
```   