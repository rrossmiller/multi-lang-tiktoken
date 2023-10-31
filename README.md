# multi-lang-tiktoken

Ensuring that tiktoken in other languages works the same as in python. Also tests speed


## How each test chunk is constructed
Split by blank lines.

Given the following text:
```
First Citizen:
Before we proceed any further, hear me speak.

All:
Speak, speak.

First Citizen:
You are all resolved rather to die than to famish?
```

This  will split into 3 different test cases
