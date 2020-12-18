# emcat
emcat - extract method comparison analysis tool

## Motivation

There are plenty of source code refactoring automation systems detecting 
["long method"](https://refactoring.guru/smells/long-method) code smell 
and fixing it with ["extract method"](https://refactoring.guru/extract-method) 
refactoring, like [JDeodorant](https://github.com/tsantalis/JDeodorant), 
[SEMI](https://ieeexplore.ieee.org/abstract/document/7801138), etc.
This tool provide a way to compare them on the unlabeled data,
i.e. the raw source code from any project.

## Comparison analysis
Suppose we have two such systems _A_ and _B_, and a set of Java source code 
files _J_. To compare such systems we propose following approach. 
First, we distinguish two functionalities such tools provide: 
detecting "long methods" and "extracting methods". 
Those functionalities are independent on each other, 
and a system can support any of it, or both. 
Without loss of generality we consider systems _A_ and _B_ support
both functionalities.

Analysis of detection functionality is done through calculating
source code metrics 
([NCSS](https://pmd.github.io/latest/pmd_java_metrics_index.html#non-commenting-source-statements-ncss)
and 
[Cyclomatic Complexity](https://pmd.github.io/latest/pmd_java_metrics_index.html#cyclomatic-complexity-cyclo)) 
of all detected method. 
Let Detected<sub>A</sub>(_J_) and Detected<sub>B</sub>(_J_) be a two sets 
of detected "long methods", by systems _A_ and _B_ respectively, 
among files in _J_. 
Having distribution of source code metrics on those sets of methods may tell 
as which system targets large and complex methods. 
In addition, we can compare those distributions with distribution 
of source code metrics of all methods in files _J_.

To compare extraction functionality we need each detected method 
to be annotated with source code range marked for extraction. 
Having that range we may commit that extraction. 
Let AfterExtraction<sub>A</sub>(_J_) and Extracted<sub>A</sub>(_J_) 
be sets of methods after extraction and extracted methods itself. 
Calculate same source code metrics, as in previous paragraph, 
allows us to get the distribution of Cyclomatic complexity decrease 
during extraction and the ratio of extracted block size to the 
whole method size in terms of non-commented source code statements (NCSS).

The whole process of analysis is reduced to distribution comparison. 
It could be done in many different ways, like mean, divergence, etc, 
and is not subject of current tool.

## Tool status
In development

## Functionality

TBD

## Usage example

TBD

## License

Licensed under [GPL 3](https://github.com/aravij/emcat/blob/master/LICENSE)
